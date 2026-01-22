package com.zetra.econsig.web.controller.ajuda;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AbrirChatSuporteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Suporte via Chat.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class AbrirChatSuporteWebController extends AbstractWebController {

    @RequestMapping(value = { "/v3/abrirChatSuporte" })
    public void abrirChatSuporte(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String url = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CHAT_SUPORTE, responsavel);
        if (!TextHelper.isNull(url)) {
            response.sendRedirect(url);
        }
    }
}
