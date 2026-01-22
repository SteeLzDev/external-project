package com.zetra.econsig.web.controller.totem;

import java.util.Arrays;
import java.util.Iterator;
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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.TotemControllerException;
import com.zetra.econsig.exception.TotemParametroConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.totem.TotemHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.totem.TotemParametroConsignanteController;
import com.zetra.econsig.toten.util.Constants;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: TotemManterParametroConsignanteWebController</p>
 * <p>Description: Controlador Web para o caso de uso de Manutenção de Parâmetros de Consingante do Totem.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author:
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/configurarTotem" })
public class TotemManterParametroConsignanteWebController extends ControlePaginacaoWebController {
//    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TotemManterParametroConsignanteWebController.class);

    private static final List<Integer> TPA_CODIGOS = Arrays.asList(Constants.TPA_FILTRO_CATEGORIA, Constants.TPA_FILTRO_CODIGO_ORGAO);

    @Autowired
    private TotemParametroConsignanteController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        return buscarDadosParaTela(request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String cseCodigoTotemParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOTEM_CODIGO_CONSIGNANTE, responsavel);
        if (TextHelper.isNull(cseCodigoTotemParam)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        Integer cseCodigoTotem = Integer.valueOf(cseCodigoTotemParam);

        List<TransferObject> totemParamCse = null;
        try {
            totemParamCse = parametroController.selectParametroConsignanteTotem(TPA_CODIGOS, cseCodigoTotem, responsavel);
        } catch (TotemParametroConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        try {
            if (totemParamCse != null && !totemParamCse.isEmpty()) {
                Iterator<TransferObject> it = totemParamCse.iterator();
                while (it.hasNext()) {
                    CustomTransferObject next = (CustomTransferObject) it.next();
                    Integer tpaCodigo = (Integer)next.getAttribute("TPA_CODIGO");
                    String pceValor = JspHelper.verificaVarQryStr(request, String.valueOf(tpaCodigo));
                    String oldPceValor = next.getAttribute("PCE_VALOR") != null ? next.getAttribute("PCE_VALOR").toString() : "";

                    if (!oldPceValor.equals(pceValor)) {
                        // Altera o parâmetro no banco
                        parametroController.updateParametroConsignante(tpaCodigo, cseCodigoTotem, null, pceValor, responsavel);

                        // Altera o parâmetro na lista
                        next.setAttribute("PCE_VALOR", pceValor);
                    }
                }
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.totem.alterar.parametro.consignante.sucesso", responsavel));
            try {
                new TotemHelper(responsavel).limparCache();
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

        } catch (TotemControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return buscarDadosParaTela(request, session, model, responsavel);
    }

    private String buscarDadosParaTela(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws InstantiationException, IllegalAccessException {
        String cseCodigoTotemParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOTEM_CODIGO_CONSIGNANTE, responsavel);
        if (TextHelper.isNull(cseCodigoTotemParam)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        Integer cseCodigoTotem = Integer.valueOf(cseCodigoTotemParam);

        List<TransferObject> totemParamCse = null;
        try {
            totemParamCse = parametroController.selectParametroConsignanteTotem(TPA_CODIGOS, cseCodigoTotem, responsavel);
        } catch (TotemParametroConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("totemParamCse", totemParamCse);

        return viewRedirect("jsp/totem/editarParametroConsignante", request, session, model, responsavel);
    }

}
