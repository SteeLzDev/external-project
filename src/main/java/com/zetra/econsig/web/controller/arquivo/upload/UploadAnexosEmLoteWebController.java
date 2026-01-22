package com.zetra.econsig.web.controller.arquivo.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.IdentificadorAnexosEmLoteEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

/**
 * <p>Title: UploadAnexosEmLoteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload de Anexos de Consignção em Lote.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadAnexosEmLote" })
public class UploadAnexosEmLoteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadAnexosEmLoteWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {        	
            //parametros de captcha
            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaDeficiente = false;
            UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            } else if (!exibeCaptcha && !exibeCaptchaAvancado)  {
                //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }
            
        	SynchronizerToken.saveToken(request);
            
            List<Object> arquivosPagina = new ArrayList<>();

            // Faz as checagens de diretório
            String pathDownload = ParamSist.getDiretorioRaizArquivos()
                                + File.separatorChar + "anexo"
                                + File.separatorChar + "criticalote"
                                ;
            File dirSaida = new File(pathDownload + File.separatorChar + responsavel.getCsaCodigo());

            ParamSession paramSession = ParamSession.getParamSession(session);
            if ((!dirSaida.exists() && !dirSaida.mkdirs())) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.upload.criacao.diretorio", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            }
            
            if (dirSaida.exists() && dirSaida.canRead()) {
                Object[] lstArqsDownload = dirSaida.listFiles();
                final List<Object> arquivosDownload = new ArrayList<>();
                arquivosDownload.addAll(Arrays.asList(lstArqsDownload));

                // Ordena os arquivos baseado na data de modificação
                Collections.sort(arquivosDownload, (o1, o2) -> {
                    Long d1 = Long.valueOf(((File) o1).lastModified());
                    Long d2 = Long.valueOf(((File) o2).lastModified());
                    return d2.compareTo(d1);
                });

                // Monta a paginação
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                int total = arquivosDownload.size();

                IntStream.range(offset, Math.min(total, offset + size - 1)).forEach(i -> arquivosPagina.add(arquivosDownload.get(i)));

                // Monta lista de parâmetros através dos parâmetros de request
                Set<String> params = new HashSet<>(request.getParameterMap().keySet());

                // Ignora os parâmetros abaixo
                params.remove("offset");
                params.remove("back");
                params.remove("linkRet");
                params.remove("linkRet64");
                params.remove("eConsig.page.token");
                params.remove("_skip_history_");
                params.remove("pager");
                params.remove("acao");

                List<String> requestParams = new ArrayList<>(params);
                String linkListagem = "../v3/uploadAnexosEmLote?acao=iniciar";
                configurarPaginador(linkListagem, "rotulo.paginacao.titulo.download.arq.upload.anexo.lote", total, size, requestParams, false, request, model);
            }

            int qtdColunas = 4;

            model.addAttribute("qtdColunas", qtdColunas);
            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("pathDownload", pathDownload);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
            model.addAttribute("arquivosDownload", arquivosPagina);
        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/uploadArquivo/uploadAnexosEmLote", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        ParamSist ps = ParamSist.getInstance();
        int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO, responsavel).toString()) : 20;
        maxSize = maxSize*1024*1024;

        UploadHelper uploadHelper = new UploadHelper();

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        if (uploadHelper.getValorCampoFormulario("FORM") != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // parametros de captcha
        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaDeficiente = false;
        UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

        if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
            exibeCaptcha = false;
            exibeCaptchaAvancado = false;
            exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        } else if (!exibeCaptcha && !exibeCaptchaAvancado)  {
            //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
            exibeCaptcha = true;
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        if (uploadHelper.getValorCampoFormulario("FORM") != null) {
            //Validação captcha
            if (usuarioResp.getUsuDeficienteVisual() == null || usuarioResp.getUsuDeficienteVisual().equals("N")) {
                if (exibeCaptcha) {
                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), uploadHelper.getValorCampoFormulario("captcha"))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                    session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                } else if (exibeCaptchaAvancado) {
                    String remoteAddr = request.getRemoteAddr();

                    if (!isValidCaptcha(uploadHelper.getValorCampoFormulario("g-recaptcha-response"), remoteAddr, responsavel)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                }
            } else if (exibeCaptchaDeficiente){
                String captchaAnswer = uploadHelper.getValorCampoFormulario("captcha");
                String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }
                session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
            }

            String idntArquivos = JspHelper.verificaVarQryStr(request, uploadHelper, "IDENTIFICADOR_ANEXOS");

            if (TextHelper.isNull(idntArquivos)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.em.lote.identifcador.nao.informado", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            }

            try {
                String outputPath = "anexo" + File.separatorChar + "tmpAnexosEmLote" + File.separatorChar + responsavel.getUsuCodigo();

                String fileName = uploadHelper.getFileName(0);
                if (!fileName.toLowerCase().endsWith(".zip")) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ARQUIVO_DE_LOTE_ANEXO, ", ")));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }
                File zipCarregado = uploadHelper.salvarArquivo(outputPath, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, null, session);

                //testa se é arquivo zip
                String testPath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + outputPath + File.separatorChar + uploadHelper.getFileName(0);
                if (!testPath.toLowerCase().endsWith(".zip") || !FileHelper.isZip(testPath)) {
                    File aApagar = new File(testPath);
                    aApagar.delete();
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ARQUIVO_DE_LOTE_ANEXO, ", ")));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }

                if (zipCarregado != null && zipCarregado.exists() && !TextHelper.isNull(uploadHelper.getFileName(0))) {
                    IdentificadorAnexosEmLoteEnum identificador = idntArquivos.equals("ADE_NUMERO") ? IdentificadorAnexosEmLoteEnum.ADE_NUMERO : IdentificadorAnexosEmLoteEnum.ADE_IDENTIFICADOR;
                    String nomeArqCritica = processarAnexosEmLoteAde(uploadHelper.getFileName(0), identificador, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.generico.sucesso", responsavel, nomeArqCritica));
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.upload.em.lote.editar.anexo.consignacao", responsavel));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                if (!TextHelper.isNull(ex.getMessageKey()) && ex.getMessageKey().equals("mensagem.erro.copia.impossivel.arquivos.permitidos")) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.upload.generico.erro", responsavel) +
                            " " + ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.extensoes.validas.no.zip", responsavel,
                                    TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, ", ")));
                } else if (ex.getMessage() != null) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                }
            }
        }
        return iniciar(request, response, session, model);
    }


    /**
     * anexa arquivos enviados ao sistema em lote os respectivos contratos, se houverem
     * @param nomeArqZip - path para o arquivo Zip que representa os anexos em lote
     * @param idntArquivos - identificador do contrato usado para relacionar o arquivo anexo a aquele
     * @param responsavel
     * @return - nome do arquivo contendo a crítica do processamento
     * @throws ConsignanteControllerException
     */
    private String processarAnexosEmLoteAde(String nomeArqZip, IdentificadorAnexosEmLoteEnum idntArquivos, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            String outputPath = ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "anexo"
                    + File.separatorChar + "tmpAnexosEmLote"
                    + File.separatorChar + responsavel.getUsuCodigo();

            String nomeArqZipFull = new StringBuilder(outputPath).append(File.separatorChar).append(nomeArqZip).toString();
            String sufixoDataHora = "_" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");

            // Lista final dos arquivos que não foram anexados em nenhuma consignação por motivos quaisquer
            Map<String, String> arqsNaoRelacionados = new HashMap<>();
            // Lista de arquivos que foram movidos para a pasta de anexos porém ocorreu erro no registro no banco
            List<File> arqsRemover = new ArrayList<>();

            List<String> lstNomesArqs = FileHelper.unZipAll(nomeArqZipFull.toString(), outputPath, false);
            int totalArquivos = (lstNomesArqs != null && !lstNomesArqs.isEmpty()) ? lstNomesArqs.size() : 0;
            int relacionadosComSucesso = 0;

            for (String pathAnexo : lstNomesArqs) {
                File anexo = new File(pathAnexo);
                String nomeArquivo = anexo.getName();

                boolean extensaoValida = false;
                for (String extensoesArquivoPermitida : UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO) {
                    if (nomeArquivo.toLowerCase().endsWith(extensoesArquivoPermitida.toLowerCase())) {
                        extensaoValida = true;
                        break;
                    }
                }

                if (!extensaoValida) {
                    arqsNaoRelacionados.put(nomeArquivo, ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.extensoes.validas", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, ", ")));
                    continue;
                }

                int inicioParteExtensao = nomeArquivo.lastIndexOf(".");
                String identificador = inicioParteExtensao != -1 ? nomeArquivo.substring(0, inicioParteExtensao) : nomeArquivo;

                List<String> sadCodigos = new ArrayList<>();
                sadCodigos.add(CodedValues.NOT_EQUAL_KEY);
                sadCodigos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

                List<String> adeNumeroList = null;
                List<String> adeIdentificadorList = null;

                if (idntArquivos == IdentificadorAnexosEmLoteEnum.ADE_NUMERO) {
                    adeNumeroList = new ArrayList<>();
                    adeNumeroList.add(identificador);
                } else if (idntArquivos == IdentificadorAnexosEmLoteEnum.ADE_IDENTIFICADOR) {
                    adeIdentificadorList = new ArrayList<>();
                    adeIdentificadorList.add(identificador);
                }

                List<TransferObject> ades;
                TransferObject ade = null;
                try {
                    ades = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, adeNumeroList, adeIdentificadorList, sadCodigos, null, null, responsavel);
                } catch (AutorizacaoControllerException ex) {
                    arqsNaoRelacionados.put(nomeArquivo, ex.getMessage());
                    continue;
                }

                if (ades == null || ades.isEmpty()) {
                    arqsNaoRelacionados.put(nomeArquivo, ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada", responsavel));
                    continue;
                } else if (ades.size() > 1) {
                    arqsNaoRelacionados.put(nomeArquivo, ApplicationResourcesHelper.getMessage("mensagem.maisDeUmaConsignacaoEncontrada", responsavel));
                    continue;
                } else {
                    ade = ades.get(0);
                }

                String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);
                Date adeData = (Date) ade.getAttribute(Columns.ADE_DATA);

                String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
                String kb = ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? (Integer.valueOf(paramTamMaxArqAnexo)).intValue() : 200);
                if (anexo.length() > (tamMaxArqAnexo * 1024)) {
                    arqsNaoRelacionados.put(nomeArquivo, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.tamanho.maximo", responsavel, Integer.valueOf(tamMaxArqAnexo).toString() + kb));
                    continue;
                }


                //salva arquivo no destino final e anexa ao contrato
                String pathData = ParamSist.getDiretorioRaizArquivos()
                        + File.separatorChar + "anexo"
                        + File.separatorChar + DateHelper.format(adeData, "yyyyMMdd");
                File subDirData = new File(pathData);
                if (!subDirData.exists()) {
                    subDirData.mkdir();
                }

                StringBuilder path = new StringBuilder(pathData).append(File.separatorChar).append(adeCodigo);
                File destino = new File(path.toString());
                if (!destino.exists()) {
                    destino.mkdir();
                }

                String nomeArquivoFinal = nomeArquivo.substring(0, inicioParteExtensao) + sufixoDataHora + nomeArquivo.substring(inicioParteExtensao);
                File anexoNoDiretorioFinal = new File(path.toString() + File.separatorChar + nomeArquivoFinal);
                if (!anexoNoDiretorioFinal.exists()) {
                    // Se não existe, move o arquivo para a pasta para que seja relacionado à ADE
                    boolean movidoComSucesso = anexo.renameTo(anexoNoDiretorioFinal);
                    if (movidoComSucesso) {
                        try {
                            java.sql.Date aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(ade.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
                            Date periodoContrato = (Date) ade.getAttribute(Columns.ADE_ANO_MES_INI);
                            java.sql.Date periodoContratoSql = DateHelper.toSQLDate(periodoContrato);

                            if (periodoContratoSql.compareTo(aadPeriodo) > 0 ) {
                                aadPeriodo = periodoContratoSql;
                            }

                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, nomeArquivoFinal,
                                    ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.obs.anexo", responsavel), aadPeriodo,
                                    TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO, responsavel);

                            relacionadosComSucesso++;
                        } catch (AutorizacaoControllerException | PeriodoException ex) {
                            LOG.error(ex.getMessage(), ex);

                            // Adiciona crítica de arquivo não relacionado
                            arqsNaoRelacionados.put(nomeArquivo, ex.getMessage());

                            // Se o arquivo foi movido com sucesso, mas ocorreu erro no registro do banco
                            // inclui na lista para remoção do disco, evitando lixo nas pastas de arquivos
                            arqsRemover.add(anexoNoDiretorioFinal);
                        }
                    } else {
                        // Adiciona crítica de arquivo não relacionado
                        arqsNaoRelacionados.put(nomeArquivo, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                    }
                } else {
                    // Adiciona crítica de arquivo não relacionado pois já existe
                    arqsNaoRelacionados.put(nomeArquivo, ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.ja.existe", responsavel));
                }
            }

            // Cria arquivo de crítica do processamento
            File dirSaida = new File(ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "anexo"
                    + File.separatorChar + "criticalote"
                    + File.separatorChar + responsavel.getCsaCodigo());

            if (!dirSaida.exists()) {
                dirSaida.mkdir();
            }

            String pathArqSaida = ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "anexo"
                    + File.separatorChar + "criticalote"
                    + File.separatorChar + responsavel.getCsaCodigo()
                    + File.separatorChar + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel)
                    + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss") + "_" + nomeArqZip + ".txt";
            File arquivoSaida = new File(pathArqSaida);

            try (PrintWriter saida = new PrintWriter(new BufferedWriter(new FileWriter(arquivoSaida)))) {
                String linhaTotalArqs = ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.critica.linha.total.arqs", responsavel) + " " + totalArquivos;
                saida.println(linhaTotalArqs);

                String linhaTotalSucessoArqs = ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.critica.linha.sucesso", responsavel) + " " + relacionadosComSucesso;
                saida.println(linhaTotalSucessoArqs);

                if (!arqsNaoRelacionados.isEmpty()) {
                    String cabecalhoNaoRelacionados = ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.critica.cabecalho.erros", responsavel);
                    saida.println(cabecalhoNaoRelacionados);
                }

                Set<String> keysNaoRelacionados = arqsNaoRelacionados.keySet();
                for (String naoRelacionado : keysNaoRelacionados) {
                    saida.println(naoRelacionado + " = " + arqsNaoRelacionados.get(naoRelacionado));
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.upload.em.lote.erro.gerar.arq.critica", responsavel, ex);
            }

            // Apaga diretório temporário para processamento
            File tmpPath = new File(ParamSist.getDiretorioRaizArquivos()
                         + File.separatorChar + "anexo"
                         + File.separatorChar + "tmpAnexosEmLote"
                         + File.separatorChar + responsavel.getUsuCodigo());

            File[] tmpFiles = tmpPath.listFiles();
            for (File tmpFile: tmpFiles) {
                tmpFile.delete();
            }
            tmpPath.delete();

            // Apaga arquivos movidos porém não registrados no banco
            for (File tmpFile: arqsRemover) {
                tmpFile.delete();
            }

            return arquivoSaida.getName();

        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
