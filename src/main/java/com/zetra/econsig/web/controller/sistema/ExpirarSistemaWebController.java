package com.zetra.econsig.web.controller.sistema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ExpirarSistemaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Sair do Sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/expirarSistema" })
public class ExpirarSistemaWebController extends AbstractWebController {

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=expirar" })
    public String expirar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlExternaSairSistemaSer = LoginHelper.getPaginaExpiracaoServidor(request, responsavel);
        if (!TextHelper.isNull(urlExternaSairSistemaSer)) {
            return "redirect:" + urlExternaSairSistemaSer;
        }

        String urlPortal = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_EUCONSIGOMAIS, responsavel);
        if (!TextHelper.isNull(urlPortal)) {
            return "redirect:" + urlPortal;
        }

        String telaLogin = LoginHelper.getPaginaLoginPeloPapel(request, responsavel);
        if (telaLogin.contains("/v3/")) {
            telaLogin = "redirect:" + telaLogin.replace("..", "").replace(".jsp", "");
        } else {
            telaLogin = telaLogin.replace("../", "").replace(".jsp", "") ;
        }

        // Sessão expirada, redireciona o usuário para página de login
        return telaLogin;
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("mensagem.informacao.sessao.expirada", responsavel));

        String mensagem = (request.getAttribute("mensagemSessaoExpirada") != null ? request.getAttribute("mensagemSessaoExpirada").toString() : JspHelper.verificaVarQryStr(request, "msg"));
        if (TextHelper.isNull(mensagem)) {
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.sessao.expirada", responsavel);
        }
        model.addAttribute("mensagemSessaoExpirada", mensagem);

        String paginaSessaoExpirada = "expirarSistemaUsu";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("LOGIN") && cookie.getValue().equals("SERVIDOR")) {
                    paginaSessaoExpirada = "expirarSistemaSer";
                    break;
                } else if (cookie.getName().equals("urlCentralizadorAcesso")) {
                    // Remove o cookie
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);

                    return "redirect:" + cookie.getValue() + "/logout?expired=true";
                }
            }
        }

        return viewRedirect("jsp/expirarSistema/" + paginaSessaoExpirada, request, session, model, responsavel);
    }

    /**
     * Chamada simples de invalidação de sessão, mas que não leva imediatamente para a tela de login.
     * @param request
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "/v3/expirarSistemaAjax")
    @ResponseBody
    public ResponseEntity<String> atualizarExtratoDiaAjax(HttpServletRequest request, HttpSession session, Model model) {
        if (session != null) {
            session.invalidate();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
