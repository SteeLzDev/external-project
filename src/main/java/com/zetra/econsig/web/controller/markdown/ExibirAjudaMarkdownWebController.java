package com.zetra.econsig.web.controller.markdown;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterMensagemWebController</p>
 * <p>Description: Controlador Web responsável por gerenciar todo fluxo de mensagem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/exibirAjudaMarkdown" })
public class ExibirAjudaMarkdownWebController extends ControlePaginacaoWebController {

    @RequestMapping(params = { "acao=ajudar" })
    public String ajuda(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        return viewRedirect("jsp/markdown/exibirAjudaMarkdown", request, session, model, responsavel);

    }

}
