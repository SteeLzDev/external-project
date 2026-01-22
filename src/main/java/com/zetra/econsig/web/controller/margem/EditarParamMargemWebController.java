package com.zetra.econsig.web.controller.margem;

import java.math.BigDecimal;
import java.text.ParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.consignante.ManterParamSistConsignanteWebController;

/**
 * <p>Title: EditarParamMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso de Manutenção de Margem</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: frederico.penido $
 * $Revision: 27612 $
 * $Date: 2019-08-19 15:40:20-0300 (seg, 19 ago 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarParamMargem" })
public class EditarParamMargemWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterParamSistConsignanteWebController.class);

    @Autowired
    private MargemController margemController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        return viewRedirect("jsp/editarConsignante/editarParamMargem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) {
            try {
                String[] marCodigosUpd = TextHelper.split(JspHelper.verificaVarQryStr(request, "MAR_CODIGOS"), ",");
                for (String element : marCodigosUpd) {
                    Short marCodigo = Short.valueOf(element);
                    MargemTO margem = new MargemTO(marCodigo);
                    String strMarPorcentagem = request.getParameter("mar_porcentagem_" + element);
                    BigDecimal marPorcentagem = !TextHelper.isNull(strMarPorcentagem) ? NumberHelper.parseDecimal(strMarPorcentagem) : null;
                    margem.setMarDescricao(request.getParameter("mar_descricao_" + element));
                    margem.setMarPorcentagem(marPorcentagem);
                    margem.setMarExibeCse(Character.valueOf(request.getParameter("mar_exibe_cse_" + element).charAt(0)));
                    margem.setMarExibeOrg(Character.valueOf(request.getParameter("mar_exibe_org_" + element).charAt(0)));
                    margem.setMarExibeSer(Character.valueOf(request.getParameter("mar_exibe_ser_" + element).charAt(0)));
                    margem.setMarExibeCsa(Character.valueOf(request.getParameter("mar_exibe_csa_" + element).charAt(0)));
                    margem.setMarExibeCor(Character.valueOf(request.getParameter("mar_exibe_cor_" + element).charAt(0)));
                    margem.setMarExibeSup(Character.valueOf(request.getParameter("mar_exibe_sup_" + element).charAt(0)));

                    margemController.updateMargem(margem, responsavel);
                }
                // Reinicializa os caches de dados
                JspHelper.limparCacheParametros();

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.margem.exibicao.sucesso", responsavel));
            } catch (MargemControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            }
        }

        return viewRedirect("jsp/editarConsignante/editarParamMargem", request, session, model, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("lstMargemRaiz", margemController.lstMargemRaiz(responsavel));
        } catch (MargemControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }
}
