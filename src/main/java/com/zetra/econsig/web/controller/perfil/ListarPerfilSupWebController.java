package com.zetra.econsig.web.controller.perfil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarPerfilSup" })
public class ListarPerfilSupWebController extends AbstractManterPerfilWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarPerfilSupWebController.class);

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {

        String codigo = (!JspHelper.verificaVarQryStr(request, "codigo").equals("")) ? JspHelper.verificaVarQryStr(request, "codigo") : (String) responsavel.getCodigoEntidade();
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel).toUpperCase();

        String opConsultar = "consultarPerfilSup";
        String opInserir = "inserirPerfilSup";
        String opEditar = "editarPerfilSup";
        String opManter = "manterPerfilSup";
        String opListarUsuario = "listarUsuarioSup";
        boolean podeEditarPerfil = false;
        boolean podeConsultarPerfil = false;
        boolean podeConsultarUsu = false;
        boolean podeEditarUsu = false;

        podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP);
        podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP);
        podeConsultarPerfil = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_SUP);
        podeEditarPerfil = responsavel.temPermissao(CodedValues.FUN_EDT_PERFIL_SUP);

        String parametros = "?codigo=" + codigo + "&titulo=" + titulo + "&" + SynchronizerToken.generateToken4URL(request);
        String linkAction = "../v3/listarPerfilSup?acao=listar";

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
        return Columns.PSU_ATIVO;
    }

    @Override
    protected String getTipo(HttpServletRequest request) {
        return AcessoSistema.ENTIDADE_SUP;
    }

    @Override
    protected String getOperacao(HttpServletRequest request) {
        return null;
    }

    @Override
    protected String getCodigo(HttpServletRequest request) {
        return CodedValues.CSE_CODIGO_SISTEMA;
    }

    @Override
    public String getLinkVoltarListagem(HttpServletRequest request, HttpSession session) throws InstantiationException, IllegalAccessException {
        return "../v3/carregarPrincipal";
    }

}

