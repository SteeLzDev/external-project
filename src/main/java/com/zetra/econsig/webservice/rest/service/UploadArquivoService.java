package com.zetra.econsig.webservice.rest.service;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_DIRETORIO_INEXISTENTE;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.lote.LoteHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLote;
import com.zetra.econsig.service.lote.LoteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.UploadLoteRestRequest;
import com.zetra.econsig.webservice.rest.request.UploadLoteRestResponse;
import com.zetra.econsig.webservice.rest.request.UploadVerificaProcessamentoResponse;

import eu.medsea.mimeutil.MimeType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: UploadArquivoService</p>
 * <p>Description: Serviço REST para tratar upload de arquivos</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/uploadArquivo")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class UploadArquivoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadArquivoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/lote")
    public Response uploadArquivoLote(UploadLoteRestRequest dados, @Context
    HttpServletRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        dados.leiaute = dados.leiaute.isEmpty() ? "imp_consignacao" : dados.leiaute;
        final String rootPath = ParamSist.getDiretorioRaizArquivos();
        final String pathLote = rootPath + File.separatorChar + "lote" + File.separatorChar + responsavel.getTipoEntidade().toLowerCase() + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
        final String pathConfLote = rootPath + File.separatorChar + "conf" + File.separatorChar + "lote" + File.separatorChar;
        final String pathConfLoteDefault = rootPath + File.separatorChar + "conf" + File.separatorChar + "lote" + File.separatorChar + "xml" + File.separatorChar;
        final UploadLoteRestResponse responseLote = new UploadLoteRestResponse();

        // Valida se o usuário é CSA
        if (!responsavel.isCsa()) {
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel);
            return Response.status(Response.Status.FORBIDDEN).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Valida obrigatoriedade dos campos
        if (TextHelper.isNull(dados.conteudo)) {
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.lote.campos.obrigatorios", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Verifica se sistema está bloqueado - Se o sistema estiver bloqueado ou inativo, nenhum arquivo de lote pode ser processado
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        try {
            final Short codigo = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            if (codigo.equals(CodedValues.STS_INDISP) || codigo.equals(CodedValues.STS_INATIVO)) {
                responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.bloqueado.inativo", responsavel);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } catch (final ConsignanteControllerException ex) {
            responseLote.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Verifica se usuário tem permissão de validação ou importação de lote
        final String funCodigo = dados.validaLote ? CodedValues.FUN_VALIDAR_PROCESSAMENTO_VIA_LOTE : CodedValues.FUN_IMPORTACAO_VIA_LOTE;
        if (!responsavel.temPermissao(funCodigo)) {
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
            return Response.status(Response.Status.FORBIDDEN).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Trata o periodo enviadoR
        Date periodo = null;

        // Função que valida xml e salva arquivo de lote
        try {
            if(dados.periodo != null) {
                try {
                    if (dados.periodo.toString().matches("([0-9]{2})/([0-9]{4})")) {
                        periodo = DateHelper.parsePeriodString(dados.periodo);
                    } else {
                        responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.invalido", responsavel);
                        return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                    }
                } catch (final ParseException ex) {
                    responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.invalido", responsavel);
                    return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }
            } else {
                periodo =  PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
            }

            validaArquivosXml(pathConfLote, pathConfLoteDefault, dados.leiaute, responsavel);
            dados.arqNome = validaArquivoLote(dados.conteudo, dados.arqNome, pathLote, dados.estabelecimento, dados.orgao, periodo, responsavel);
        } catch (ZetraException | IOException ex) {
            responseLote.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final String pathArquivoLote = pathLote + dados.arqNome;

        if (!dados.validaLote) {
            dados.arqNome += ".prc";
        }

        final LoteController loteController = ApplicationContextProvider.getApplicationContext().getBean(LoteController.class);
        final ControleProcessamentoLote controleProcessamentoLote = loteController.findProcessamentoByArquivoeConsig(pathLote + dados.arqNome);

        if ((controleProcessamentoLote != null) && (controleProcessamentoLote.getCplStatus() != CodedValues.CPL_PENDENTE_UPLOAD) && (controleProcessamentoLote.getCplStatus() != CodedValues.CPL_UPLOAD_FALHA)) {
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.presente.servidor", responsavel, dados.arqNome));
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.presente.servidor", responsavel, dados.arqNome);
            responseLote.nomeArquivo = dados.arqNome;
            return Response.status(Response.Status.OK).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Verifica se existe algum processo de lote executando para a consignatária, independente do arquivo ou usuário executando
        final String chave1 = "LOTE" + "|" + responsavel.getCodigoEntidade();
        final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, null);

        if (!temProcessoRodando) {
            final ProcessaLoteRest processaLoteRest = new ProcessaLoteRest(pathArquivoLote, dados.leiaute, dados.validaLote, responsavel);
            ControladorProcessos.getInstance().incluir(chave1, processaLoteRest);
            processaLoteRest.start();

            responseLote.mensagem = ApplicationResourcesHelper.getMessage(dados.validaLote ? "mensagem.informacao.validacao.iniciada.sucesso" : "mensagem.informacao.processamento.iniciado.sucesso", responsavel);
            responseLote.nomeArquivo = dados.arqNome;
            return Response.status(Response.Status.OK).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

        } else {
            // Se algum arquivo de lote já está em processamento para a consignatária, retorna mensagem de aviso ao usuário para tentar mais tarde
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.upload.concorrente.csa", responsavel);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    private String validaArquivoLote(String conteudo, String arqNome, String pathLote, String estIdentificador, String orgIdentificador, Date periodo, AcessoSistema responsavel) throws ZetraException, IOException {
        final byte[] decodedBytes = Base64.getDecoder().decode(conteudo);
        final String[] extensoesArquivoPermitidas = { "TXT", "ZIP" };
        String extensao = ".txt";

        // Gera o nome do arquivo de lote caso o campo nome seja nulo
        if (TextHelper.isNull(arqNome)) {
            arqNome = "LOTE_" + (estIdentificador != null ? estIdentificador : "") + (orgIdentificador != null ? orgIdentificador : "") + periodo.toString().substring(0, 7) + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "ddMMyyyy-HHmmss");
        }

        // Verifica se o caminho para a gravação existe
        final File dir = new java.io.File(pathLote);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new UploadControllerException(MENSAGEM_ERRO_DIRETORIO_INEXISTENTE, responsavel);
        }

        // Verifica se a extensão está de acordo com o arquivo
        final File tempFile = new File(pathLote + "lote_tmp_" + UUID.randomUUID().toString());
        FileUtils.writeByteArrayToFile(tempFile, decodedBytes);
        final Set<MimeType> contentType = FileHelper.detectContentType(tempFile);
        if (contentType.contains(new MimeType("application/zip"))) {
            extensao = ".zip";
        }
        tempFile.delete();

        // Caso o nome do arquivo esteja sem extensão é inserida uma de acordo com o content-type lido
        if (arqNome.indexOf(".") == -1) {
            arqNome += extensao;
        }

        // Caso a extensão do arqNome seja diferente da extensão identificada pelo detectContentType
        if (!arqNome.endsWith(extensao)) {
            throw new UploadControllerException("mensagem.erro.upload.arquivo.lote.extensao.permitida", responsavel);
        }

        try {
            arqNome = FileHelper.prepararNomeArquivo(TextHelper.removeAccentCharsetArbitrario(arqNome));
            final File arquivoEntrada = new File(pathLote + arqNome);

            // Valida se o arquivo existe, para não sobrescrever
            if (arquivoEntrada.exists()) {
                throw new UploadControllerException("mensagem.informacao.arquivo.em.processamento.aguarde.termino.processamento", responsavel);
            }

            // Escreve o conteúdo no arquivo
            FileUtils.writeByteArrayToFile(arquivoEntrada, decodedBytes);

            // Valida se há mais de um arquivo dentro do zip
            if (".zip".equals(extensao) && (FileHelper.contaArquivosZip(arquivoEntrada.toString()) > 1)) {
                throw new UploadControllerException("mensagem.erro.upload.arquivo.lote.zip.quantidade.arquivos", responsavel);
            }

            // Valida extensão do arquivo
            if (!FileHelper.validaExtensaoRecursivamente(arquivoEntrada.toString(), extensoesArquivoPermitidas)) {
                arquivoEntrada.delete();
                throw new UploadControllerException("mensagem.erro.upload.arquivo.lote.extensao.permitida", responsavel);
            }

            // Verifica tamanho do arquivo
            final int maxSize = (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel).toString()) : 1) * 1024 * 1024;
            if (!arquivoEntrada.exists() || !arquivoEntrada.isFile()) {
                throw new UploadControllerException("mensagem.erro.upload.arquivo.lote.arquivo.nao.existe", responsavel);
            }
            if (arquivoEntrada.length() > maxSize) {
                arquivoEntrada.delete();
                throw new UploadControllerException("mensagem.erro.upload.arquivo.lote.tamanho.arquivo", responsavel, String.valueOf(maxSize));
            }
        } catch (final IOException e) {
            throw new UploadControllerException("mensagem.erro.upload.arquivo.salvar", responsavel);
        }
        return arqNome;
    }

    private void validaArquivosXml(String pathConfLote, String pathConfLoteDefault, String leiaute, AcessoSistema responsavel) throws ViewHelperException {
        if (!TextHelper.isNull(responsavel.getCodigoEntidade())) {
            pathConfLote += responsavel.getCodigoEntidade();
            final File xmlEntradaCsa = new File(pathConfLote + File.separatorChar + leiaute + "_entrada.xml");
            final File xmlTradutorCsa = new File(pathConfLote + File.separatorChar + leiaute + "_tradutor.xml");
            if (!xmlEntradaCsa.exists() || !xmlTradutorCsa.exists()) {
                final File xmlEntradaPadrao = new File(pathConfLoteDefault + leiaute + "_entrada.xml");
                final File xmlTradutorPadrao = new File(pathConfLoteDefault + leiaute + "_tradutor.xml");
                if (!xmlEntradaPadrao.exists() || !xmlTradutorPadrao.exists()) {
                    throw new ViewHelperException("mensagem.erro.lote.arquivos.configuracao.importacao.ausentes", responsavel);
                }
            }
        }
    }

    private class ProcessaLoteRest extends Processo {
        private String arquivo;
        private final String leiaute;
        private final boolean validaLote;
        private final AcessoSistema responsavel;

        public ProcessaLoteRest(String arquivo, String leiaute, boolean validaLote, AcessoSistema responsavel) {
            this.arquivo = arquivo;
            this.leiaute = leiaute;
            this.validaLote = validaLote;
            this.responsavel = responsavel;
        }

        @Override
        protected void executar() {
            try {
                // Renomeia o arquivo que será processado para que não ocorra duplicação do processamento
                if (!validaLote) {
                    FileHelper.rename(arquivo, arquivo + ".prc");
                    arquivo += ".prc";
                }

                final File arquivoEntrada = new File(arquivo);
                final LoteHelper loteHelper = new LoteHelper(responsavel.getCsaCodigo(), responsavel.getCorCodigo(), validaLote, true, true, false, false, null, responsavel);
                loteHelper.importarLote(leiaute + "_entrada.xml", leiaute + "_tradutor.xml", arquivoEntrada.getName());

                // Caso seja validação não renomeia o arquivo
                if (!validaLote) {
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
                    FileHelper.rename(arquivo, arquivo + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");
                } else {
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
                }
            } catch (final ViewHelperException ex) {
                LOG.error(ex.getMessage(), ex);
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.rest.processamento", responsavel);
            }
        }
    }

    @POST
    @Secured
    @Path("/verificaProcessamentoLote")
    public Response verificaProcessamentoLote(UploadLoteRestRequest dados, @Context HttpServletRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
         final UploadVerificaProcessamentoResponse responseLote = new UploadVerificaProcessamentoResponse();

        if ((dados.arqNome == null) || dados.arqNome.isEmpty()) {
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.rest.obrigatorio", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Valida se o usuário é CSA
        if (!responsavel.isCsa()) {
              responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel);
            return Response.status(Response.Status.FORBIDDEN).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        // Verifica se usuário tem permissão de validação ou importação de lote
        if (!responsavel.temPermissao(CodedValues.FUN_IMPORTACAO_VIA_LOTE)) {
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
            return Response.status(Response.Status.FORBIDDEN).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final LoteController loteController = ApplicationContextProvider.getApplicationContext().getBean(LoteController.class);
        final String rootPath = ParamSist.getDiretorioRaizArquivos();
        final String pathLote = rootPath + File.separatorChar + "lote" + File.separatorChar + responsavel.getTipoEntidade().toLowerCase() + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
        final ControleProcessamentoLote controle = loteController.findProcessamentoByArquivoeConsig(pathLote + dados.arqNome);
        if (controle == null) {
            responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.nao.encontrado.arquivo.lote.rest", responsavel, dados.arqNome);
            return Response.status(Response.Status.NOT_FOUND).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } else if (controle.getCplStatus().equals(CodedValues.CPL_PROCESSADO_SUCESSO)) {
            if (!TextHelper.isNull(controle.getCplArquivoCritica())) {
                final File arquivoEntrada = new File(controle.getCplArquivoCritica());
                if (!arquivoEntrada.exists()) {
                    responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.arquivo.critica.nao.existe.validacao", responsavel, dados.arqNome);
                    controle.setCplStatus(CodedValues.CPL_FALHA);
                    try {
                        loteController.alterarProcessamento(controle, responsavel);
                    } catch (final ZetraException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                    return Response.status(Response.Status.NOT_FOUND).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }
                try {
                    final byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(arquivoEntrada));
                    responseLote.criticaBase64 = new String(encoded);
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.arquivo.critica.encoder", responsavel);
                    return Response.status(Response.Status.CONFLICT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }
                responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.arquivo.lote.rest", responsavel, dados.arqNome);
            } else {
                responseLote.mensagem = ApplicationResourcesHelper.getMessage("mensagem.arquivo.critica.nao.existe", responsavel, dados.arqNome);
            }

            try {
                // Marca que o resultado foi entregue
                controle.setCplStatus(CodedValues.CPL_RESULTADO_ENTREGUE);
                loteController.alterarProcessamento(controle, responsavel);
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            return Response.status(Response.Status.OK).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } else {
            return Response.status(jakarta.ws.rs.core.Response.Status.NO_CONTENT).entity(responseLote).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }
}
