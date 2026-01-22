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

/**
 * <p>Title: ListarUsuarioConsignanteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Usuário Consignante.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuarioCse" })
public class ListarUsuarioConsignanteWebController extends AbstractListarUsuarioPapelWebController {

    @Autowired
    UsuarioController usuarioController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("podeCriarUsu", responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_CSE));
        model.addAttribute("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSE));
        model.addAttribute("podeConsultarUsu", responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE));
        model.addAttribute("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE));
        model.addAttribute("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_CSE));
        model.addAttribute("podeReiniciarSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_CSE));
        model.addAttribute("podePermitirEdtEmailUnidades", responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDT_EMAILS_SER_UNIDADES));

        model.addAttribute("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_CSE, responsavel));
        model.addAttribute("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_CSE, responsavel));
        model.addAttribute("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSE, responsavel));
    }

    @Override
    protected String getLinkAction() {
        return "../v3/listarUsuarioCse?acao=listar";
    }

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_CSE;
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "../v3/consultarUsuarioCse";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "../v3/inserirUsuarioCse";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "../v3/editarUsuarioCse";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "../v3/bloquearUsuarioCse";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "../v3/reinicializarSenhaUsuarioCse";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "../v3/detalharUsuarioCse";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "../v3/excluirUsuarioCse";
    }

    @RequestMapping(params = { "acao=unidades" })
    public String unidadesSubOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if(!super.unidades(request, response, session, model)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.sub.orgao.nao.existe.unidade", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
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
