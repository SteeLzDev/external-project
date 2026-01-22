package com.zetra.econsig.web.controller.sobre;

import com.zetra.econsig.helper.parametro.ParamSist;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VisualizarSobreWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Sobre.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarSobre" })
public class VisualizarSobreWebController extends AbstractWebController {
    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

            model.addAttribute("versaoLeiaute", versaoLeiaute);
            model.addAttribute("dataUltimaAtualizacaoSistema", consignanteController.dataUltimaAtualizacaoSistema());
            model.addAttribute("omitirSobreZetra", ApplicationResourcesHelper.getMessage("mensagem.sobre.a.zetrasoft", responsavel).isEmpty());
            model.addAttribute("omitirSobreSistema", ApplicationResourcesHelper.getMessage("mensagem.sobre.o.sistema", responsavel).isEmpty());

            return viewRedirect("jsp/visualizarSobre/visualizarSobre", request, session, model, responsavel);
        } catch (ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.menu.sobre", responsavel));
    }

}
