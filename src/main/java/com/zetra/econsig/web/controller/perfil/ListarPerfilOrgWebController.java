package com.zetra.econsig.web.controller.perfil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarPerfilOrg" })
public class ListarPerfilOrgWebController extends AbstractManterPerfilWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarPerfilOrgWebController.class);

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {

        String codigo = (!JspHelper.verificaVarQryStr(request, "codigo").equals("")) ? JspHelper.verificaVarQryStr(request, "codigo") : (String) responsavel.getCodigoEntidade();
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");

        String opConsultar = "consultarPerfilOrg";
        String opInserir = "inserirPerfilOrg";
        String opEditar = "editarPerfilOrg";
        String opManter = "manterPerfilOrg";
        String opListarUsuario = "listarUsuarioOrg";
        boolean podeEditarPerfil = false;
        boolean podeConsultarPerfil = false;
        boolean podeConsultarUsu = false;
        boolean podeEditarUsu = false;

        podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG);
        podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG);
        podeConsultarPerfil = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_ORG);
        podeEditarPerfil = responsavel.temPermissao(CodedValues.FUN_EDT_PERFIL_ORG);

        String parametros = "?codigo=" + codigo + "&titulo=" + titulo + "&" + SynchronizerToken.generateToken4URL(request);
        String linkAction = "../v3/listarPerfilOrg?acao=listar";

        String linkVoltar = "";
        try {
            linkVoltar = getLinkVoltarListagem(request, session);
        } catch (InstantiationException | IllegalAccessException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("linkVoltar", linkVoltar);
        model.addAttribute("opConsultar", opConsultar);
        model.addAttribute("opInserir", opInserir);
        model.addAttribute("opEditar", opEditar);
        model.addAttribute("opManter", opManter);
        model.addAttribute("opListarUsuario", opListarUsuario);
        model.addAttribute("podeConsultarPerfil", podeConsultarPerfil);
        model.addAttribute("podeConsultarUsu", podeConsultarUsu);
        model.addAttribute("podeEditarUsu", podeEditarUsu);
        model.addAttribute("podeEditarPerfil", podeEditarPerfil);
        model.addAttribute("titulo", titulo);
        model.addAttribute("parametros", parametros);
        model.addAttribute("linkAction", linkAction);

    }

    @Override
    protected String getColunaAtivo(HttpServletRequest request) {
        return Columns.POR_ATIVO;
    }

    @Override
    protected String getTipo(HttpServletRequest request) {
        return AcessoSistema.ENTIDADE_ORG;
    }

    @Override
    protected String getOperacao(HttpServletRequest request) {
        return null;
    }

}

