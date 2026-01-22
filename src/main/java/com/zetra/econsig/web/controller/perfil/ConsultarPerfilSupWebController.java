package com.zetra.econsig.web.controller.perfil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ConsultarPerfilSupWebController</p>
 * <p>Description: Web Controller para consulta de perfil de SUP</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarPerfilSup" })
public class ConsultarPerfilSupWebController extends EditarPerfilSupWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        super.configurarPagina(request, session, model, responsavel);

        String linkAction = "../v3/consultarPerfilSup?acao=iniciar";
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.perfil.de", responsavel) + " " + JspHelper.verificaVarQryStr(request, "titulo");

        model.addAttribute("titulo", titulo);
        model.addAttribute("linkAction", linkAction);
    }

    @Override
    protected String getColunaAtivo(HttpServletRequest request) {
        return "";
    }

    @Override
    protected String getTipo(HttpServletRequest request) {
        return AcessoSistema.ENTIDADE_SUP;
    }

    @Override
    protected String getOperacao(HttpServletRequest request) {
        return "consultar";
    }

    @Override
    protected String getCodigo(HttpServletRequest request) {
        return CodedValues.CSE_CODIGO_SISTEMA;
    }

}
