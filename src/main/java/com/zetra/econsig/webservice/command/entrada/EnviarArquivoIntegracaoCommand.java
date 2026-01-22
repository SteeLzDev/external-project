package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO_ENUM;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.solicitacaosuporte.jira.JiraUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webclient.util.RestTemplateFactory;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.jira.exception.JiraException;

public class EnviarArquivoIntegracaoCommand extends RequisicaoExternaFolhaCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarArquivoIntegracaoCommand.class);

    public EnviarArquivoIntegracaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if(TextHelper.isNull(parametros.get(TIPO_ARQUIVO)) || TextHelper.isNull(parametros.get(NOME_ARQUIVO)) ||TextHelper.isNull(parametros.get(ARQUIVO))) {
            throw new ZetraException("mensagem.erro.enviar.arquivo.campos.obrigatorios", responsavel);
        }

        if ("movimento".equals(parametros.get(TIPO_ARQUIVO)) || "integracao".equals(parametros.get(TIPO_ARQUIVO))) {
            throw new ZetraException("mensagem.upload.arquivo.erro.movimento.relatoriointegracao", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        if (!responsavel.isCseSup()) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);

        final String tipo = (String) parametros.get(TIPO_ARQUIVO);
        final TipoArquivoEnum tipoEnum = (TipoArquivoEnum) parametros.get(TIPO_ARQUIVO_ENUM);
        final String orgIdentificador = (String) parametros.get(CODIGO_ORGAO);
        final String estIdentificador = (String) parametros.get(CODIGO_ESTABELECIMENTO);
        final String nomeArquivo = (String) parametros.get(NOME_ARQUIVO);
        final ParamSist ps = ParamSist.getInstance();
        String pathArquivoCompleto = null;
        Date pexPeriodo = null;
        File arquivoSalvo = null;

        String estCodigo = null;
        if (!TextHelper.isNull(estIdentificador)) {
            final EstabelecimentoTransferObject est = consignanteController.findEstabelecimentoByIdn(estIdentificador, responsavel);
            estCodigo = est.getEstCodigo();
        }

        String orgCodigo = null;
        if (!TextHelper.isNull(orgIdentificador)) {
            final OrgaoTransferObject org = consignanteController.findOrgaoByIdn(orgIdentificador, estCodigo, responsavel);
            orgCodigo = org.getOrgCodigo();
        }

        try {
            final String tipoEntidade = retornaTipoEntidade(orgCodigo, estCodigo);
            final String codigoEntidade = AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipoEntidade) ? responsavel.getCodigoEntidade() : AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade) ? orgCodigo : AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade) ? estCodigo : null;
            final String pathArquivoSalvo = retornaPath(orgCodigo, estCodigo, tipo);
            pathArquivoCompleto = salvaArquivo(pathArquivoSalvo);
            arquivoSalvo = new File(pathArquivoCompleto);

            validaTamanhoArquivo(pathArquivoCompleto);

            final String[] extensaoList = extensaoList(tipoEntidade, codigoEntidade, tipo, nomeArquivo);
            validaExtensao(nomeArquivo, extensaoList);

            if (ParamSist.getBoolParamSist(CodedValues.TPC_CONVERTE_AUTOMATICAMENTE_LAYOUT_RET_ORGAO_PARA_GERAL, responsavel) && ("retorno".equals(tipo) || "retornoatrasado".equals(tipo)) && !TextHelper.isNull(orgCodigo)) {
                arquivoSalvo = conversaoAutomaticaLayoutRetorno(arquivoSalvo, orgCodigo);
            }

            arquivoSalvo = renomearExtensaoParaTxt(arquivoSalvo, extensaoList, ps);

            pexPeriodo = pegaPeriodo(orgCodigo, estCodigo, tipoEntidade);
            final String obs = verificaEnvioObs(tipo, tipoEnum) ? (String) parametros.get(OBSERVACAO) : "";
            criaHistoricoEnviaEmail(orgCodigo, estCodigo, tipoEntidade, tipo, tipoEnum, arquivoSalvo, pexPeriodo, obs, responsavel);

            validaQtdArquivo(tipoEntidade, tipo, orgCodigo, estCodigo, pathArquivoSalvo, nomeArquivo, codigoEntidade, obs);
        } catch (final ZetraException e) {
            if ((arquivoSalvo != null) && arquivoSalvo.exists()) {
                arquivoSalvo.delete();
            }
            throw new ZetraException(e.getMessageKey(), responsavel, e.getMessageArgs());
        } catch (IOException | InterruptedException | ParseException | JiraException ex) {
            if ((arquivoSalvo != null) && arquivoSalvo.exists()) {
                arquivoSalvo.delete();
            }
            throw new ZetraException(ex.getMessage(), responsavel);
        }

    }

    private String salvaArquivo(String pathArquivoSalvo) throws ZetraException {
        final File pth = new File(pathArquivoSalvo);
        if (!pth.isDirectory()) {
            pth.mkdirs();
        }
        if (!pth.exists()) {
            throw new ZetraException("mensagem.erro.upload.criacao.diretorio", responsavel);
        }

        final File file = new File(pathArquivoSalvo, (String) parametros.get(NOME_ARQUIVO));
        try {
            if (file.createNewFile()) {
                final byte[] content = (byte[]) parametros.get(ARQUIVO);
                Files.write(file.toPath(), content);
            }
            return pathArquivoSalvo + (String) parametros.get(NOME_ARQUIVO);
        } catch (final IOException e) {
            throw new ZetraException("mensagem.erro.upload.arquivo.arg0", responsavel, (String) parametros.get(NOME_ARQUIVO));
        }
    }

    private String retornaPath(String orgCodigo, String estCodigo, String tipo) {
        final boolean salvarArquivoCse = TextHelper.isNull(orgCodigo) && TextHelper.isNull(estCodigo);
        final boolean salvarArquivoOrg = !TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo);
        final boolean salvarArquivoEst = TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo);

        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathRaizeConsig = absolutePath + File.separatorChar + tipo + File.separatorChar;
        return salvarArquivoCse ? pathRaizeConsig + "cse" + File.separatorChar : salvarArquivoOrg ? pathRaizeConsig + "cse" + File.separatorChar + orgCodigo + File.separatorChar : salvarArquivoEst ? pathRaizeConsig + "est" + File.separatorChar + estCodigo + File.separatorChar : null;
    }

    private String retornaTipoEntidade(String orgCodigo, String estCodigo) {
        final boolean salvarArquivoCse = TextHelper.isNull(orgCodigo) && TextHelper.isNull(estCodigo);
        final boolean salvarArquivoOrg = !TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo);
        final boolean salvarArquivoEst = TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo);

        return salvarArquivoCse ? AcessoSistema.ENTIDADE_CSE : salvarArquivoOrg ? AcessoSistema.ENTIDADE_ORG : salvarArquivoEst ? AcessoSistema.ENTIDADE_EST : null;

    }

    private void validaTamanhoArquivo(String path) throws UploadControllerException {
        final File arquivoEntrada = new File(path);

        //Verifica tamanho do arquivo
        final ParamSist ps = ParamSist.getInstance();
        int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 1;
        maxSize = maxSize * 1024 * 1024;

        if (arquivoEntrada.length() > maxSize) {
            throw new UploadControllerException("mensagem.erro.upload.arquivo.lote.tamanho.arquivo", responsavel, String.valueOf(maxSize));
        }
    }

    private String[] extensaoList(String tipoEntidade, String codigoEntidade, String tipo, String fileName) throws ZetraException {
        final ValidaImportacaoController validaImportacaoController = ApplicationContextProvider.getApplicationContext().getBean(ValidaImportacaoController.class);
        String[] extensaoList = null;

        final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, null, null, responsavel);
        if ("retorno".equals(tipo) || "retornoatrasado".equals(tipo) || "margem".equals(tipo) || "margemcomplementar".equals(tipo) || "contracheque".equals(tipo)) {
            final String padraoNomeArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca"))) ? paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca") : null;
            final String padraoNomeArquivoExemplo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBuscaExemplo"))) ? paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBuscaExemplo") : null;
            extensaoList = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "extensoes"))) ? paramValidacaoArq.get(tipo + "." + "extensoes").split(",") : new String[] { "TXT", "ZIP" };

            String padrao = "";
            if (!TextHelper.isNull(padraoNomeArquivo)) {
                padrao = padraoNomeArquivo;
            } else {
                // Constrói padrão baseado nas extensões permitidas
                padrao = ".*\\.(" + TextHelper.join(extensaoList, "|") + ")";
            }
            // Faz o casamento de padrão ignorando maiúsculas/minúsculas
            final Pattern p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
            final Matcher m = p.matcher(fileName);
            if (!m.matches()) {
                // Grava log da tentativa de upload com nome incorreto
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_ERRO);
                log.add("ARQUIVO: \"" + fileName + "\"");
                log.write();

                if (!TextHelper.isNull(padraoNomeArquivoExemplo)) {
                    throw new ZetraException("mensagem.erro.upload.nome.arquivo.formatacao.soap.exemplos", responsavel, fileName, padraoNomeArquivoExemplo);
                }
                throw new ZetraException("mensagem.erro.upload.nome.arquivo.formatacao.soap", responsavel, fileName);
            }
        } else if (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "extensoes"))) {
            // Para os outros tipos de arquivos, o padrão não é .zip e .txt
            extensaoList = paramValidacaoArq.get(tipo + "." + "extensoes").split(",");
        }

        if ((extensaoList == null) || (extensaoList.length == 0)) {
            extensaoList = new String[] { "TXT", "ZIP" };
        }

        return extensaoList;
    }

    private void validaExtensao(String nomeArquivo, String[] extensoesArquivoPermitidas) throws ZetraException {
        if (extensoesArquivoPermitidas != null) {
            boolean extensaoValida = false;
            for (final String extensoesArquivoPermitida : extensoesArquivoPermitidas) {
                if (nomeArquivo.toLowerCase().endsWith(extensoesArquivoPermitida.toLowerCase())) {
                    extensaoValida = true;
                    break;
                }
            }

            if (!extensaoValida) {
                throw new ZetraException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(extensoesArquivoPermitidas, ", "));
            }
        }
    }

    private File conversaoAutomaticaLayoutRetorno(File arquivoSalvo, String orgCodigo) throws IOException, InterruptedException, ZetraException {
        File arquivoConversao = null;
        final UploadHelper uploadHelper = new UploadHelper();
        String extensaoArquivo = "";
        for (final String extensao : UploadHelper.EXTENSOES_PERMITIDAS_PARA_CONVERSAO_TXT_RETORNO_ORGAO) {
            if (arquivoSalvo.getName().toLowerCase().endsWith(extensao)) {
                extensaoArquivo = extensao;
                break;
            }
        }

        if (!TextHelper.isNull(extensaoArquivo) && !".csv".equals(extensaoArquivo)) {
            FileHelper.rename(arquivoSalvo.getAbsolutePath(), arquivoSalvo.getAbsolutePath() + ".ORIG_CONVERT");
            arquivoConversao = new File(arquivoSalvo.getAbsolutePath() + ".ORIG_CONVERT");
            final String arquivoConvertido = converterDocumentosTxt(arquivoConversao, extensaoArquivo, responsavel);

            if (TextHelper.isNull(arquivoConvertido)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.conversao.para.txt", responsavel));
                throw new ZetraException("mensagem.erro.arquivo.conversao.para.txt", responsavel);
            }

            final byte[] arq = org.apache.commons.codec.binary.Base64.decodeBase64(arquivoConvertido);
            FileUtils.writeByteArrayToFile(new File(arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt")), arq);
            arquivoSalvo = new File(arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt"));
        } else if (!TextHelper.isNull(extensaoArquivo) && ".csv".equals(extensaoArquivo)) {
            FileHelper.rename(arquivoSalvo.getAbsolutePath(), arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt"));
            arquivoSalvo = new File(arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt"));
        }

        final String mensagemErro = uploadHelper.convertArquivoRetornoOrgaoLayoutCse(arquivoSalvo, orgCodigo, responsavel);
        if (!TextHelper.isNull(mensagemErro)) {
            if ((arquivoConversao != null) && arquivoConversao.exists()) {
                FileHelper.delete(arquivoConversao.getAbsolutePath());
            }
            throw new ZetraException("mensagem.erro.arquivo.conf.xml.orgao.nao.existe", responsavel);
        }

        return arquivoSalvo;
    }

    private String converterDocumentosTxt(File file, String extensao, AcessoSistema responsavel) throws IOException, InterruptedException {
        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CONVERSOR_AUDIO_MP3_DOCUMENT_PDF, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.conversor", responsavel));
            return null;
        }

        final String arquivoBase64 = org.apache.commons.codec.binary.Base64.encodeBase64String(Files.readAllBytes(file.toPath()));

        final HashMap<String, String> camposJson = new HashMap<>();
        camposJson.put("file", arquivoBase64);
        camposJson.put("type", extensao);
        camposJson.put("typeTo", "txt");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        final JSONObject jsonObject = new JSONObject(camposJson);
        final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

        final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(responsavel);

        final ResponseEntity<String> response = restTemplateSimple.postForEntity(urlBase + "/api/converter/v1/base64", httpEntity, String.class);

        if ((response.getStatusCode() != HttpStatus.OK) || (response.getBody() == null)) {
            return null;
        }

        return (response.getStatusCode() != HttpStatus.OK) || (response.getBody() == null) ? null : (String) response.getBody();
    }

    private File renomearExtensaoParaTxt(File arquivoSalvo, String[] extensaoList, ParamSist ps) throws IOException, ZetraException {
        boolean rename = true;
        String arquivoSalvoNameLC = arquivoSalvo.getName().toLowerCase();
        for (final String element : extensaoList) {
            if (arquivoSalvoNameLC.endsWith(element.toLowerCase())) {
                rename = false;
                break;
            }
        }

        if (rename) {
            FileHelper.rename(arquivoSalvo.getAbsolutePath(), arquivoSalvo.getAbsolutePath() + ".txt");
            // recupera o arquivo renomeado
            arquivoSalvo = new File(arquivoSalvo.getAbsolutePath() + ".txt");
            arquivoSalvoNameLC = arquivoSalvo.getName().toLowerCase();
        }

        // Se o arquivo for txt, converte as quebras de linhas para o padrão do servidor em que a aplicação está executando.
        if (arquivoSalvoNameLC.endsWith(".txt") || arquivoSalvoNameLC.endsWith(".csv")) {
            FileHelper.convertLineBreaks(arquivoSalvo.getAbsolutePath());
        }

        final boolean converteCharset = (!TextHelper.isNull(ps.getParam(CodedValues.TPC_CONVERTE_CHARSET_ARQUIVO_UPLOAD, responsavel)) && !"N".equalsIgnoreCase(ps.getParam(CodedValues.TPC_CONVERTE_CHARSET_ARQUIVO_UPLOAD, responsavel).toString()));
        if (converteCharset && (arquivoSalvoNameLC.endsWith(".txt") || arquivoSalvoNameLC.endsWith(".csv") || arquivoSalvoNameLC.endsWith(".zip"))) {
            FileHelper.convertCharset(arquivoSalvo);
        }

        return arquivoSalvo;
    }

    private Date pegaPeriodo(String orgCodigo, String estCodigo, String tipoEntidade) throws PeriodoException {
        final PeriodoController periodoController = ApplicationContextProvider.getApplicationContext().getBean(PeriodoController.class);

        java.util.Date pexPeriodo = null;
        try {
            List<String> orgCodigos = null;
            orgCodigo = AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade) ? orgCodigo : null;
            if (!TextHelper.isNull(orgCodigo)) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(orgCodigo);
            }
            List<String> estCodigos = null;
            estCodigo = AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade) ? estCodigo : null;
            if (!TextHelper.isNull(estCodigo)) {
                estCodigos = new ArrayList<>();
                estCodigos.add(estCodigo);
            }
            final TransferObject to = periodoController.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            pexPeriodo = DateHelper.parse(to.getAttribute(Columns.PEX_PERIODO).toString(), LocaleHelper.FORMATO_DATA_INGLES);
        } catch (final Exception e) {
            throw new PeriodoException("mensagem.erro.periodo.impossivel.recuperar", responsavel);
        }

        return pexPeriodo;
    }

    private void criaHistoricoEnviaEmail(String orgCodigo, String estCodigo, String tipoEntidade, String tipo, TipoArquivoEnum tipoArquivo, File arquivoSalvo, Date pexPeriodo, String obs, AcessoSistema Responsavel) throws ZetraException {
        final String tipoEntidadeHistorico = AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) ? null : tipoEntidade;
        final String codEntidadeHistorico = (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) ? orgCodigo : (AcessoSistema.ENTIDADE_EST.equals(tipoEntidade)) ? estCodigo : null;

        final HistoricoArquivoController historicoArquivoController = ApplicationContextProvider.getApplicationContext().getBean(HistoricoArquivoController.class);
        historicoArquivoController.createHistoricoArquivo(tipoEntidadeHistorico, codEntidadeHistorico, tipoArquivo, arquivoSalvo.getAbsolutePath(), null, null, pexPeriodo, "1", CodedValues.FUN_UPL_ARQUIVOS, responsavel);

        // Envia e-mail informando o recebimento do arquivo
        enviarEmailRecebimentoArquivo(tipo, tipoArquivo, arquivoSalvo.getName(), orgCodigo, obs, responsavel);
    }

    private void enviarEmailRecebimentoArquivo(String tipo, TipoArquivoEnum tipoArquivo, String nomeArquivo, String orgCodigo, String obs, AcessoSistema responsavel) throws ZetraException {
        final UploadController uploadController = ApplicationContextProvider.getApplicationContext().getBean(UploadController.class);

        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, responsavel)) {
                // Se for ambiente de testes, não envia e-mail de recebimento de arquivos
                return;
            }

            // Busca o tipo de arquivo, e verifica se este envia notificação no upload
            final TipoArquivo tar = uploadController.buscaTipoArquivoByPrimaryKey(tipoArquivo.getCodigo(), responsavel);

            if (!"S".equalsIgnoreCase(tar.getTarNotificacaoUpload())) {
                // Se o tipo de arquivo não envia notificação, então finaliza o método
                return;
            }

            // Verifica tabela de destinatarios de e-mail para determinar se é enviado ou não o e-mail
            final List<String> papeisDestinatarios = uploadController.listarPapeisEnvioEmailUpload(responsavel);
            if ((papeisDestinatarios == null) || papeisDestinatarios.isEmpty()) {
                // Se não há configuração para destinatários de recebimento de e-mail de notificação de upload, então finaliza o método
                return;
            }

            final boolean enviaEmailCSE = papeisDestinatarios.contains(CodedValues.PAP_CONSIGNANTE);
            final boolean enviaEmailORG = papeisDestinatarios.contains(CodedValues.PAP_ORGAO);
            final boolean enviaEmailCSA = papeisDestinatarios.contains(CodedValues.PAP_CONSIGNATARIA);
            final boolean enviaEmailCOR = papeisDestinatarios.contains(CodedValues.PAP_CORRESPONDENTE);

            // Recupera tipo de email que será concatenado no título do email enviado
            String tipoEmail = tipo;
            if ("margem".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.margens.servidores", responsavel).toString().toLowerCase();
            } else if ("margemcomplementar".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.margem.complementar", responsavel).toString().toLowerCase();
            } else if ("retorno".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.retorno.integracao", responsavel).toString().toLowerCase();
            } else if ("retornoatrasado".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.retorno.atrasado", responsavel).toString().toLowerCase();
            } else if ("bloqueio_ser".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.bloqueio.servidor", responsavel).toString().toLowerCase();
            } else if ("critica".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.critica.integracao", responsavel).toString().toLowerCase();
            } else if ("transferidos".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.transferidos", responsavel).toString().toLowerCase();
            } else if ("falecido".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.falecido", responsavel).toString().toLowerCase();
            } else if ("desligado".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.desligado.bloqueado", responsavel).toString().toLowerCase();
            } else {
                // Por compatibilidade, só usa a descrição do tipo de arquivo para os novos tipos
                tipoEmail = tar.getTarDescricao();
            }

            if (enviaEmailCSE || enviaEmailORG) {
                // Envia notificação de recebimento para papel de CSE/ORG
                EnviaEmailHelper.enviarEmailRecebimentoArquivo(tipoEmail, nomeArquivo, enviaEmailCSE, enviaEmailORG, orgCodigo, obs, responsavel);
            }

            // Envia notificação de recebimento para papel de CSA/COR somente se o parâmetro 545 estiver habilitado
            if ((enviaEmailCSA || enviaEmailCOR) && ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_UPLOAD_ARQ_CSE_PARA_CSA, responsavel)) {
                EnviaEmailHelper.enviarEmailUploadArquivoCsa(tipoEmail, nomeArquivo, enviaEmailCSA, enviaEmailCOR, obs, responsavel);
            }
        } catch (final UploadControllerException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private boolean verificaEnvioObs(String tipoArquivo, TipoArquivoEnum tipoEnum) throws UploadControllerException {
        final UploadController uploadController = ApplicationContextProvider.getApplicationContext().getBean(UploadController.class);
        boolean comentario = "margem".equals(tipoArquivo) || "margem_complementar".equals(tipoArquivo) || "retorno".equals(tipoArquivo) ? ParamSist.paramEquals(CodedValues.TPC_INTEGRA_JIRA, CodedValues.TPC_SIM, responsavel) : false;
        if (!comentario && !TextHelper.isNull(tipoArquivo)) {
            final TipoArquivo data = uploadController.buscaTipoArquivoByPrimaryKey(tipoEnum.getCodigo(), responsavel);
            comentario = (data != null) && CodedValues.TPC_SIM.equals(data.getTarNotificacaoUpload());
        }

        return comentario;
    }

    private void validaQtdArquivo(String tipoEntidadeValidArq, String tipo, String orgCodigo, String estCodigo, String path, String fileName, String codigoEntidade, String obs) throws ZetraException, IOException, ParseException, JiraException {
        final boolean validarQtdeArquivo = ("margem".equals(tipo) || "retorno".equals(tipo));

        if (validarQtdeArquivo) {
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            path = path.substring(absolutePath.length() + 1, path.length() - 1);

            final String mensagem = validarQtdeArquivo(tipo, orgCodigo, estCodigo, path, fileName, tipoEntidadeValidArq, codigoEntidade, obs, responsavel);

            if (!TextHelper.isNull(mensagem)) {
                parametros.put(MENSAGEM, mensagem);
            }
        }

    }

    private String validarQtdeArquivo(String tipo, String orgCodigo, String estCodigo, String path, String fileName, String tipoEntidade, String codigoEntidade, String obs, AcessoSistema responsavel) throws IOException, ParseException, ZetraException, JiraException {
        String mensagem = null;
        final ValidaImportacaoController validaImportacaoController = ApplicationContextProvider.getApplicationContext().getBean(ValidaImportacaoController.class);

        //Path dos arquivos de integração
        final boolean integraJira = ParamSist.paramEquals(CodedValues.TPC_INTEGRA_JIRA, CodedValues.TPC_SIM, responsavel);
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final JiraUtil jiraUtil = new JiraUtil();

        final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, null, null, responsavel);

        // verifica quantidade de arquivos requeridos para o processamento em questão e, baseado nisto, quantos faltam para fazer upload
        int qtdArqNecessarios = 0;
        if ((paramValidacaoArq != null) && !paramValidacaoArq.isEmpty() && paramValidacaoArq.containsKey(tipo + "." + "qtdMinimaArquivos") && !TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "qtdMinimaArquivos"))) {
            qtdArqNecessarios = Integer.parseInt(paramValidacaoArq.get(tipo + "." + "qtdMinimaArquivos"));
        } else {
            qtdArqNecessarios = 0;
        }

        if (qtdArqNecessarios > 1) {
            final int diasIdadeMaximaArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "diasIdadeMaximaArquivo"))) ? Integer.parseInt(paramValidacaoArq.get(tipo + "." + "diasIdadeMaximaArquivo")) : 0;
            final String padraoNomeArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca"))) ? paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca") : null;
            final String[] extensoes = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "extensoes"))) ? paramValidacaoArq.get(tipo + "." + "extensoes").split(",") : new String[] { "TXT", "ZIP" };

            final FileFilter filtroExtArq = pathname -> {
                // Verifica a idade máxima do arquivo
                if ((diasIdadeMaximaArquivo == 0) || (DateHelper.dayDiff(new Date(pathname.lastModified())) <= diasIdadeMaximaArquivo)) {
                    // Se tem a idade máxima correta, verifica o padrão de nome ou as extensões requeridas
                    String padrao = "";
                    if (!TextHelper.isNull(padraoNomeArquivo)) {
                        padrao = padraoNomeArquivo;
                    } else {
                        // Constrói padrão baseado nas extensões permitidas
                        padrao = ".*\\.(" + TextHelper.join(extensoes, "|") + ")";
                    }
                    // Faz o casamento de padrão ignorando maiúsculas/minúsculas
                    final Pattern p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
                    final Matcher m = p.matcher(pathname.getName());
                    return m.matches();
                }
                return false;
            };

            final List<String> nameList = FileHelper.getFilesInDir(absolutePath + File.separatorChar + path, filtroExtArq);

            final int totalCopiado = ((nameList != null) && !nameList.isEmpty()) ? nameList.size() : 1;

            final int restantes = qtdArqNecessarios - totalCopiado;

            if (restantes > 0) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso.parcial.requeridos.disponiveis", responsavel, Integer.toString(totalCopiado), Integer.toString(qtdArqNecessarios));
                if (integraJira) {
                    jiraUtil.atualizaStatusProducao(tipo, responsavel, "comentar", null, fileName, path, obs, orgCodigo, estCodigo);
                }
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso.todos.requeridos.disponiveis", responsavel);
                if (integraJira) {
                    jiraUtil.atualizaStatusProducao(tipo, responsavel, "aguardarValidacaoArquivos", null, fileName, path, obs, orgCodigo, estCodigo);
                }
            }
        } else {
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso", responsavel);
            if (integraJira) {
                jiraUtil.atualizaStatusProducao(tipo, responsavel, "aguardarValidacaoArquivos", null, fileName, path, obs, orgCodigo, estCodigo);
            }
        }

        return mensagem;
    }

}
