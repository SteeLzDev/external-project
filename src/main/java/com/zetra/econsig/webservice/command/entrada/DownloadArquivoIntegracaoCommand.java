package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO_INTEGRACAO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

public class DownloadArquivoIntegracaoCommand extends RequisicaoExternaFolhaCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DownloadArquivoIntegracaoCommand.class);
    public static final String[] EXTENSOES_PERMITIDAS_DOWNLOAD_ARQUIVO  = {".txt", ".zip", ".xls", ".xlsx", ".csv", ".txt.crypt", ".zip.crypt"};

    public DownloadArquivoIntegracaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if (TextHelper.isNull(parametros.get(TIPO_ARQUIVO)) || TextHelper.isNull(parametros.get(NOME_ARQUIVO))) {
            throw new ZetraException("mensagem.erro.download.arquivo.campos.obrigatorios", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        if (!responsavel.isCseSup()) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);

        final String tipo = (String) parametros.get(TIPO_ARQUIVO);
        final String orgIdentificador = (String) parametros.get(CODIGO_ORGAO);
        final String estIdentificador = (String) parametros.get(CODIGO_ESTABELECIMENTO);
        final String nomeArquivo = (String) parametros.get(NOME_ARQUIVO);

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
            final String pathArquivoSalvo = retornaPath(orgCodigo, estCodigo, tipo);
            downloadArquivo(responsavel, pathArquivoSalvo, nomeArquivo);

        } catch (final ZetraException e) {
            throw new ZetraException(e.getMessageKey(), responsavel, e.getMessageArgs());
        } catch (final IOException e) {
            throw new ZetraException(e.getMessage(), responsavel);
        }

    }

    private String retornaPath(String orgCodigo, String estCodigo, String tipo) {
        final boolean salvarArquivoCse = TextHelper.isNull(orgCodigo) && TextHelper.isNull(estCodigo);
        final boolean salvarArquivoOrg = !TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo);
        final boolean salvarArquivoEst = TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo);
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathRaizeConsig = null;
        String pathArquivoSalvo = null;

        if (!"integracao".equals(tipo)) {
            pathRaizeConsig = absolutePath + File.separatorChar + tipo + File.separatorChar;
            if (salvarArquivoCse) {
                pathArquivoSalvo = pathRaizeConsig + "cse" + File.separatorChar;
            } else if (salvarArquivoOrg) {
                pathArquivoSalvo = pathRaizeConsig + "cse" + File.separatorChar + orgCodigo + File.separatorChar;
            } else if (salvarArquivoEst) {
                pathArquivoSalvo = pathRaizeConsig + "est" + File.separatorChar + estCodigo + File.separatorChar;
            }

        } else {
            pathRaizeConsig = absolutePath + File.separatorChar + "relatorio/cse/integracao" + File.separatorChar;
            if (salvarArquivoCse) {
                pathArquivoSalvo = pathRaizeConsig;
            } else if (salvarArquivoOrg) {
                pathArquivoSalvo = pathRaizeConsig + orgCodigo + File.separatorChar;
            } else if (salvarArquivoEst) {
                pathArquivoSalvo = pathRaizeConsig + estCodigo + File.separatorChar;
            }
        }

        return pathArquivoSalvo;
    }

    private void downloadArquivo(AcessoSistema responsavel, String path, String nomeArquivo) throws ZetraException, IOException {
        // Cria filtro para seleção de arquivos .txt, .zip, .xls, .xlsx e .csv
        final FileFilter filtro = arq -> {
            final String arq_name = arq.getName().toLowerCase();
            for (final String element : EXTENSOES_PERMITIDAS_DOWNLOAD_ARQUIVO) {
                if (arq_name.endsWith(element)) {
                    return true;
                }
            }
            return false;
        };

        final File pathDiretorio = new File(path);
        File pathArquivo = new File(path + nomeArquivo);
        final File pathArquivoCrypt = new File(path + nomeArquivo + ".crypt");
        final File[] arquivosPermitidos = pathDiretorio.listFiles(filtro);
        byte[] content = null;

        if (TextHelper.isNull(arquivosPermitidos) || (arquivosPermitidos.length == 0)) {
            throw new ZetraException("mensagem.erro.include.get.file.nao.encontrado", responsavel, nomeArquivo);
        }

        final List<File> arquivos = Arrays.asList(arquivosPermitidos);

        // O arquivo buscado não está na lista de permitidos
        if (!arquivos.contains(pathArquivo) && !arquivos.contains(pathArquivoCrypt)) {
            throw new ZetraException("mensagem.erro.include.get.file.nao.encontrado", responsavel, nomeArquivo);
        }

        // possui somente arquivos crypt
        if (!arquivos.contains(pathArquivo) && arquivos.contains(pathArquivoCrypt)) {
            pathArquivo = pathArquivoCrypt;
        }

        if (pathArquivo.getName().endsWith(".crypt")) {
            final File arquivoTempCript = new File(pathDiretorio.getAbsolutePath() + File.separatorChar + UUID.randomUUID().toString() + "_" + pathArquivo.getName());
            FileUtils.copyFile(pathArquivo, arquivoTempCript);
            final File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(arquivoTempCript.getAbsolutePath(), false, responsavel);
            Files.delete(arquivoTempCript.toPath());
            if (arquivoPlano == null) {
                throw new ZetraException("mensagem.erro.download.arquivo.descriptografar", responsavel);
            }
            content = Files.readAllBytes(arquivoPlano.toPath());
            Files.delete(arquivoPlano.toPath());
        } else {
            content = Files.readAllBytes(pathArquivo.toPath());
        }

        parametros.put(ARQUIVO_INTEGRACAO, content);

        // Gera log de download de arquivo
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + pathArquivo.getAbsolutePath());
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("rotulo.compact.erro.interno", responsavel, nomeArquivo);
        }
    }
}