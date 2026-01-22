package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListarUsuarioSuporteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Usuário Suporte.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuarioSup" })
public class ListarUsuarioSuporteWebController extends AbstractListarUsuarioPapelWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        // Valores especiais para página de usuários de suporte.
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel).toLowerCase();
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.titulo", responsavel, titulo));

        model.addAttribute("podeCriarUsu", responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_SUP));
        model.addAttribute("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_SUP));
        model.addAttribute("podeConsultarUsu", responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP));
        model.addAttribute("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP));
        model.addAttribute("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_SUP));
        model.addAttribute("podeReiniciarSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_SUP));

        model.addAttribute("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_SUP, responsavel));
        model.addAttribute("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_SUP, responsavel));
        model.addAttribute("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_SUP, responsavel));

        String consultarOp = "consultar_sup";
        String editarOp = "editar_sup";
        model.addAttribute("consultarOp", consultarOp);
        model.addAttribute("editarOp", editarOp);
    }

    @Override
    protected String getCodigoEntidade(HttpServletRequest request) {
        return CodedValues.CSE_CODIGO_SISTEMA;
    }

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_SUP;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/listarUsuarioSup?acao=listar";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "../v3/consultarUsuarioSup";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "../v3/inserirUsuarioSup";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "../v3/editarUsuarioSup";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "../v3/bloquearUsuarioSup";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "../v3/reinicializarSenhaUsuarioSup";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "../v3/detalharUsuarioSup";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "../v3/excluirUsuarioSup";
    }
}
