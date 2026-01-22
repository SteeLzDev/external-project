package com.zetra.econsig.web.controller.validadorpdfmargem;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping(method = {RequestMethod.GET}, value = {"/v3/validaPdf"})
public class ValidaPdfMargemWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaPdfMargemWebController.class);

    @Autowired
    private MargemController margemController;

    @RequestMapping(params = {"acao=consultar"})
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ServicoControllerException, ParametroControllerException, InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaAvancado = !exibeCaptcha && ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        if (exibeCaptchaDeficiente) {
            exibeCaptchaAvancado = false;
            exibeCaptcha = false;
        }

        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);

        return viewRedirect("jsp/validaPdfMargem/validaPdfMargem", request, session, model, responsavel);
    }

    @RequestMapping(method = {RequestMethod.POST}, params = {"acao=validar"})
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ServicoControllerException, ParametroControllerException, InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String matricula = JspHelper.verificaVarQryStr(request, "matricula");
        String cpf = JspHelper.verificaVarQryStr(request, "cpf");
        String chave = JspHelper.verificaVarQryStr(request, "chave");
        TransferObject controle = null;

        final boolean defVisual = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        if (!defVisual) {
            if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                    model.addAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    return consultar(request, response, session, model);
                }
                session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
            } else if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                final String remoteAddr = request.getRemoteAddr();

                if (!isValidCaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                    model.addAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    return consultar(request, response, session, model);
                }
            }
        } else {
            final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            if (exigeCaptchaDeficiente) {
                final String captchaAnswer = request.getParameter("captcha");

                if (captchaAnswer == null) {
                    model.addAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    return consultar(request, response, session, model);
                }

                final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                    model.addAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    return consultar(request, response, session, model);
                }
                session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
            }
        }

        try {
            controle = margemController.validaDocumentoMargem(matricula, cpf, chave, responsavel);
        } catch (MargemControllerException e) {
            throw new RuntimeException(e);
        }

        if (TextHelper.isNull(controle)) {
            model.addAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.comunicacao.nao.encontrado", responsavel));
            return consultar(request, response, session, model);
        } else {
            model.addAttribute("controle", controle);
        }
        return viewRedirect("jsp/validaPdfMargem/dadosValidacaoPdfMargem", request, session, model, responsavel);
    }


    @RequestMapping(params = {"acao=gerarPdf"}, method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> gerarPdf(@RequestBody(required = true) Map<String,Object> corpo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException, ServidorControllerException, ParametroControllerException, MargemControllerException {

        Path path = Paths.get(String.valueOf(corpo.get("path")));
        UrlResource recurso = new UrlResource(path.toUri());

        if (!recurso.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();

        if ("download".equalsIgnoreCase(String.valueOf(corpo.get("acao")))) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
        } else {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF) // ou detectar dinamicamente
                .body(recurso);
    }
}
