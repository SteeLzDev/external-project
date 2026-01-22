package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarUsuarioOrgaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Usuário Órgão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuarioOrg" })
public class ListarUsuarioOrgaoWebController extends AbstractListarUsuarioPapelWebController {

    @Autowired
    UsuarioController usuarioController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("podeCriarUsu", responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_ORG));
        model.addAttribute("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_ORG));
        model.addAttribute("podeConsultarUsu", responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG));
        model.addAttribute("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG));
        model.addAttribute("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_ORG));
        model.addAttribute("podeReiniciarSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_ORG));
        model.addAttribute("podePermitirEdtEmailUnidadesOrg", responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDT_EMAILS_SER_UNIDADES));

        model.addAttribute("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_ORG, responsavel));
        model.addAttribute("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_ORG, responsavel));
        model.addAttribute("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_ORG, responsavel));

        String consultarOp = "consultar_org";
        String editarOp = "editar_org";
        model.addAttribute("consultarOp", consultarOp);
        model.addAttribute("editarOp", editarOp);
    }

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_ORG;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/listarUsuarioOrg?acao=listar";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "../v3/consultarUsuarioOrg";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "../v3/inserirUsuarioOrg";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "../v3/editarUsuarioOrg";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "../v3/bloquearUsuarioOrg";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "../v3/reinicializarSenhaUsuarioOrg";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "../v3/detalharUsuarioOrg";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "../v3/excluirUsuarioOrg";
    }

    @RequestMapping(params = { "acao=unidades" })
    public String unidadesSubOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String orgCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ORG_CODIGO));

        request.setAttribute(Columns.ORG_CODIGO, orgCodigo);

        if(!super.unidades(request, response, session, model)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.sub.orgao.nao.existe.unidade", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("usuarioOrgao", Boolean.TRUE);
        model.addAttribute("orgCodigo", orgCodigo);
        return viewRedirect("jsp/manterUsuario/listarUsuarioUnidades", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarUnidades(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if(!super.salvarUnidadesSubOrgao(request, response, session, model)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.sub.orgao.nao.existe.unidade", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return unidadesSubOrgao(request, response, session, model);
    }
}
