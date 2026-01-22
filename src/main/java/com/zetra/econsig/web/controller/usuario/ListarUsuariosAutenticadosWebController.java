package com.zetra.econsig.web.controller.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.listener.SessionCounterListener;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuariosAutenticados" })
public class ListarUsuariosAutenticadosWebController extends AbstractWebController {

    @Autowired
    private SessionCounterListener sessionCounterListener;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        model.addAttribute("listaUsuarios", sessionCounterListener.getLoggedUsers(responsavel));
        return viewRedirect("jsp/manterUsuario/listarUsuariosAutenticados", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=encerrar" })
    public String encerrar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Navega na lista de usuários logados que o responsável atual pode ver, evitando que passe um código
        // de usuário que ele não tem permissão para encerrar a sessão
        String usuCodigo = request.getParameter("usu_codigo");
        String sessionId = null;
        for (AcessoSistema usuario : sessionCounterListener.getLoggedUsers(responsavel)) {
            if (usuario.getUsuCodigo().equals(usuCodigo)) {
                sessionId = usuario.getSessionId();
                break;
            }
        }

        if (TextHelper.isNull(sessionId)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        sessionCounterListener.logoutSession(sessionId, responsavel);
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.sessao.usuario.encerrada.sucesso", responsavel));

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/listarUsuariosAutenticados?acao=listar", request)));
        return "jsp/redirecionador/redirecionar";
    }
}
