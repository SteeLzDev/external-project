package com.zetra.econsig.service.consignataria;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.*;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.AnexoConsignataria;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.arquivo.DeleteWebController;
import com.zetra.econsig.web.controller.arquivo.upload.UploadAnexosEmLoteWebController;
import com.zetra.econsig.web.controller.consignataria.ManterConsignatariaWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/uploadAnexosConsignataria"})
public class UploadAnexosConsignatariaWebController extends ManterConsignatariaWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadAnexosEmLoteWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsignatariaController consignatariaController;
    @RequestMapping(method = {RequestMethod.POST}, params = {"acao=upload"})
    public String uploadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException, ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        String tipo = "consignataria";

        ParamSist ps = ParamSist.getInstance();
        int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAMANHO_MAX_ARQUIVO_ANEXADO_CSA, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAMANHO_MAX_ARQUIVO_ANEXADO_CSA, responsavel).toString()) : 20;
        maxSize = maxSize * 1024 * 1024;
        // Parametros de captcha
        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaDeficiente = false;
        UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
        File arq = null;

        if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
            exibeCaptcha = false;
            exibeCaptchaAvancado = false;
            exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        } else if (!exibeCaptcha && !exibeCaptchaAvancado && !responsavel.getFunCodigo().equals(CodedValues.FUN_EDITAR_MENSAGEM)) {
            // Caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples,
            // se assim estiver definido pelo método abaixo.
            exibeCaptcha = true;
        }

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

        try {
            String outputPath = tipo + File.separatorChar + csaCodigo;

            if (uploadHelper.hasArquivosCarregados()) {
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
                } else if (exibeCaptchaDeficiente) {
                    String captchaAnswer = uploadHelper.getValorCampoFormulario("captcha");
                    String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                    if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                    session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                }
            }
            arq = uploadHelper.salvarArquivo(outputPath, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_CONSIGNATARIA, null, session);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.upload.csa.salvo.sucesso", responsavel));
        } catch (ZetraException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        try {
            AnexoConsignataria anexoRegistro = new AnexoConsignataria();
            anexoRegistro.setAxcNome(arq.getName());
            anexoRegistro.setAxcIpAcesso(InetAddress.getLocalHost().getHostAddress());
            anexoRegistro.setUsuCodigo(responsavel.getUsuCodigo());
            anexoRegistro.setAxcData(DateHelper.getSystemDatetime());
            anexoRegistro.setCsaCodigo(csaCodigo);

            consignatariaController.createRegistroAnexoCsa(anexoRegistro, responsavel);
        } catch (ConsignatariaControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException(e);
        }

        paramSession.halfBack();
        return editarAnexos(request, response, session, model);
    }

    @RequestMapping(params = {"acao=excluirArquivo"})
    public String excluirArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, HQueryException, ConsignatariaControllerException, UpdateException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            String csaCodigo = request.getParameter("csaCodigo");
            String nomeArquivo = request.getParameter("arquivo_nome");
            boolean exclusao = consignatariaController.excluiAnexoConsignataria(responsavel, csaCodigo, nomeArquivo);
            if(!exclusao){
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.exclusao.logica.falhou", responsavel));
            }
        } catch (ConsignatariaControllerException e) {
            throw new ConsignatariaControllerException(e);
        } catch (HQueryException e) {
            throw new HQueryException(e);
        } catch (UpdateException e) {
            throw new UpdateException(e);
        }
        new DeleteWebController().excluirArquivo(request, response, session, model);

        paramSession.halfBack();
        return editarAnexos(request, response, session, model);
    }

}
