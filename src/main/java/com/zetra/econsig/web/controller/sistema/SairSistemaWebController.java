package com.zetra.econsig.web.controller.sistema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.webclient.sso.SSOClient;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SairSistemaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Sair do Sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class SairSistemaWebController extends AbstractWebController {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SairSistemaWebController.class);

	@Autowired
	private SSOClient ssoClient;

    @RequestMapping(method = { RequestMethod.POST}, value = { "/v3/sairSistema" }, params = { "acao=sair" })
    public String sair(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String telaLogin = LoginHelper.getPaginaLoginPeloPapel(request, responsavel);
            if (telaLogin.contains("/v3/")) {
                telaLogin = "redirect:" + telaLogin.replace("..", "").replace(".jsp", "");
            } else {
                telaLogin = "redirect:" + telaLogin;
            }

            final String urlExternaSairSistemaSer = LoginHelper.getPaginaExpiracaoServidor(request, responsavel);
            if (!TextHelper.isNull(urlExternaSairSistemaSer)) {
                telaLogin = "redirect:" + urlExternaSairSistemaSer;
            }

            final Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (final Cookie cookie : cookies) {
                    if (cookie.getName().equals("urlCentralizadorAcesso")) {
                        // Remove o cookie
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);

                        // Invalida a sessão do usuário e redireciona para página de login
                        new LogDelegate(responsavel, Log.SISTEMA, Log.LOGOUT, Log.LOG_LOGOUT).write();
                        try {
                            session.invalidate();
                        } catch (final IllegalStateException ex) {
                            // Trata erro caso a sessão já esteja invalidada
                        }
                        // Renova a sessão, evitando erro de "IllegalStateException: UT000010: Session not found"
                        request.getSession(true);

                        return "redirect:" + cookie.getValue() + "/logout?expired=true";
                    }
                }
            }

            // Se autenticação foi realizada pelo SSO invalida o token no SSO após logout no sistema
            final boolean autenticacaoSSO = !TextHelper.isNull(responsavel.getSsoToken());
			if (autenticacaoSSO) {
				try {
					ssoClient.logout(responsavel.getSsoToken().access_token);
		        } catch (final SSOException ex) {
					LOG.error(ex.getMessage(), ex);
		        }
			}

            // Invalida a sessão do usuário e redireciona para página de login
            new LogDelegate(responsavel, Log.SISTEMA, Log.LOGOUT, Log.LOG_LOGOUT).write();
            try {
                session.invalidate();
            } catch (final IllegalStateException ex) {
                // Trata erro caso a sessão já esteja invalidada
            }
            // Renova a sessão, evitando erro de "IllegalStateException: UT000010: Session not found"
            session = request.getSession(true);

            return telaLogin;
        } catch (final LogControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
