package com.zetra.econsig.web.controller.rodape;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.menu.MenuController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ItemMenuEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VisualizarRodapeWebController</p>
 * <p>Description: Controlador Web para o caso de uso de inserir rodapé</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarRodape" })
public class VisualizarRodapeWebController extends AbstractWebController {

    @Autowired
    private MenuController menuController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)
            throws InstantiationException, IllegalAccessException, MenuControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.ITM_CODIGO, ItemMenuEnum.SOBRE.getCodigo());

        request.setAttribute("sobre_ativo", true);

        List<TransferObject> lstMenu = menuController.lstItemMenu(criterio, responsavel);

        if (lstMenu != null && !lstMenu.isEmpty()) {
            Integer ativo = Integer.valueOf(((CustomTransferObject)lstMenu.get(0)).getAttribute(Columns.ITM_ATIVO).toString());
            if (ativo == 0) {
                request.removeAttribute("sobre_ativo");
            }
        }

        return viewRedirect("jsp/visualizarRodape/visualizarRodape", request, session, model, responsavel);
    }
}
