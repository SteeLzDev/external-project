package com.zetra.econsig.web.controller.mensagem;

import org.apache.http.HttpStatus;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ExibirMensagemErroController</p>
 * <p>Description: Controlador Web para fluxo de telas de mensagens de erro.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class ExibirMensagemErroWebController extends AbstractWebController implements ErrorController {

    @RequestMapping(value = { "/v3/exibirMensagem" }, params = { "acao=exibirMsg404" })
    public String exibirMsg404(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem404", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/exibirMensagem" }, params = { "acao=exibirMsg500" })
    public String exibirMsg500(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem500", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/exibirMensagem" }, params = { "acao=exibirMsgSessao" })
    public String exibirMsgSessao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/error" })
    public String handleError(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            final Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem404", request, session, model, responsavel);
            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem500", request, session, model, responsavel);
            }
        }

        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }
}
