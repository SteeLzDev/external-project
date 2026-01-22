package com.zetra.econsig.web.controller.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.menu.MenuController;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: FavoritarMenuWebController</p>
 * <p>Description: Controlador Web para caso de uso favoritar menu</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class FavoritarMenuWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FavoritarMenuWebController.class);

    @Autowired
    private MenuController menuController;

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/favoritarMenu" })
    public String iniciar(@RequestParam(value = "itmCodigo", required = true, defaultValue = "") String itmCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FindException, UpdateException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if (!TextHelper.isNull(itmCodigo)) {
                menuController.favoritarMenu(itmCodigo, responsavel);
                model.addAttribute("sucesso", Boolean.TRUE);
            }
        } catch (MenuControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return viewRedirect("jsp/favoritarMenu/favorito", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/favoritarMenu" }, params = { "acao=recarregarMenu" })
    public String recarregarMenu(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            responsavel.setMenu(menuController.obterMenu(responsavel));
            model.addAttribute("recarregarMenu", Boolean.TRUE);
        } catch (MenuControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return viewRedirect("jsp/favoritarMenu/favorito", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/favoritarMenu" }, params = { "acao=recarregarDashboard" })
    public String recarregarDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        responsavel.limparMenuFavoritos();
        model.addAttribute("recarregarDashboard", Boolean.TRUE);
        return viewRedirect("jsp/favoritarMenu/favorito", request, session, model, responsavel);
    }
    
    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/favoritarMenu" }, params = { "acao=recarregarCardsDashboard" })
    public String recarregarCardsDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        
        String itmsCodigo = JspHelper.verificaVarQryStr(request, "itmsCodigo");
        
        List<String> lstItmsCodigo = new ArrayList<String>(Arrays.asList(itmsCodigo.split(",")));
        Short imfSequencia = 1;
        
        try {
            for (String itmCodigo : lstItmsCodigo) {
				menuController.updateFavoritosDashBoard(responsavel.getUsuCodigo(), itmCodigo, imfSequencia, responsavel);
				imfSequencia++;
            }
            responsavel.setMenu(menuController.obterMenu(responsavel));
            responsavel.limparMenuFavoritos();
        } catch (MenuControllerException | FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return viewRedirect("jsp/favoritarMenu/favorito", request, session, model, responsavel);
    }
    
    
}
