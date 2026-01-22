package com.zetra.econsig.web.controller.perfil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.parametro.ParamSist;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/inserirPerfilSup" })
public class InserirPerfilSupWebController extends AbstractManterPerfilWebController {
    
    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        boolean podeEditarPerfil = responsavel.temPermissao(CodedValues.FUN_EDT_PERFIL_SUP);
        String linkAction = "../v3/manterPerfilSup?acao=inserir";
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.perfil.edicao.perfil.de", responsavel) + " " + JspHelper.verificaVarQryStr (request, "titulo");

        // Exibe Botao que leva ao rodapé
        boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("podeEditarPerfil",podeEditarPerfil);
        model.addAttribute("titulo",titulo); 
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
        return "inserir";
    }
    
    @Override
    protected String getCodigo(HttpServletRequest request) {
        return CodedValues.CSE_CODIGO_SISTEMA;
    }
    
}

