package com.zetra.econsig.web.controller.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.menu.MenuController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: EditarMenuWebController</p>
 * <p>Description: Controlador Web para caso de uso editar menu</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25329 $
 * $Date: 2018-08-28 14:15:21 -0300 (Ter, 28 ago 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarMenu" })
public class EditarMenuWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarMenuWebController.class);

    @Autowired
    private MenuController menuController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> menus = new ArrayList<>();
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            menus = menuController.lstMenu(criterio, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            menus = new ArrayList<>();
        }

        model.addAttribute("menus", menus);

        return viewRedirect("jsp/editarMenu/listarMenu", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=listarItemMenu" })
    public String listarItemMenu(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String mnu_codigo = request.getParameter("MNU_CODIGO");
        String mnu_descricao = request.getParameter("MNU_DESCRICAO");

        List<TransferObject> itens = null;
        Map<Object, Object> submenus = new HashMap<>();
        String listaSubItens = "";

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.MNU_CODIGO, mnu_codigo);
            itens = menuController.lstItemMenu(criterio, responsavel);

            Iterator<TransferObject> itSubmenus = itens.iterator();
            while (itSubmenus.hasNext()) {
                TransferObject to = itSubmenus.next();
                if (to.getAttribute(Columns.ITM_CODIGO_PAI) != null) {
                    @SuppressWarnings("unchecked")
                    List<Object> lista = (List<Object>) submenus.get(to.getAttribute(Columns.ITM_CODIGO_PAI).toString());
                    if (lista == null) {
                        lista = new ArrayList<>();
                    }
                    lista.add(to.getAttribute(Columns.ITM_CODIGO).toString());
                    submenus.put(to.getAttribute(Columns.ITM_CODIGO_PAI).toString(), lista);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            itens = new ArrayList<>();
        }

        model.addAttribute("mnu_codigo", mnu_codigo);
        model.addAttribute("mnu_descricao", mnu_descricao);
        model.addAttribute("itens", itens);
        model.addAttribute("submenus", submenus);
        model.addAttribute("listaSubItens", listaSubItens);

        return viewRedirect("jsp/editarMenu/listarItemMenu", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvarItemMenu" })
    public String salvarItemMenu(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String[] codigo = request.getParameterValues("ITM_CODIGO");
            String[] codigoPai = request.getParameterValues("ITM_CODIGO_PAI");
            String[] descricao = request.getParameterValues("ITM_DESCRICAO");
            String[] descricaoOld = request.getParameterValues("ITM_DESCRICAO_OLD");
            String[] separador = request.getParameterValues("ITM_SEPARADOR");
            String[] ativo = request.getParameterValues("ITM_ATIVO");
            List<String> separadores = separador != null ? Arrays.asList(separador) : new ArrayList<>();
            List<String> ativos = ativo != null ? Arrays.asList(ativo) : new ArrayList<>();
            String pai = "";

            for (int i = 0; i < codigo.length; i++) {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ITM_CODIGO, codigo[i]);
                String inicio = (!codigoPai[i].equals("")) ? (pai + " -->") : "";
                criterio.setAttribute(Columns.ITM_DESCRICAO, descricao[i].substring(inicio.length()));
                criterio.setAttribute(Columns.ITM_SEPARADOR, separadores.contains(String.valueOf(i)) ? CodedValues.TPC_SIM : CodedValues.TPC_NAO);
                criterio.setAttribute(Columns.ITM_ATIVO, ativos.contains(String.valueOf(i)) ? "1" : "0");
                criterio.setAttribute(Columns.ITM_SEQUENCIA, Integer.valueOf(i + 1));
                menuController.updateItemMenu(criterio, responsavel);

                pai = codigoPai[i].equals("") ? descricaoOld[i] : pai;
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.itens.menu.sucesso", responsavel));
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return listarItemMenu(request, response, session, model);
    }

}
