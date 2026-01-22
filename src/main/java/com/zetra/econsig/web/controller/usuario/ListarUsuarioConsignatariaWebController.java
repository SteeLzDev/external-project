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
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListarUsuarioConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Usuário Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuarioCsa" })
public class ListarUsuarioConsignatariaWebController extends AbstractListarUsuarioPapelWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("podeCriarUsu", responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_CSA));
        model.addAttribute("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA));
        model.addAttribute("podeConsultarUsu", responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA));
        model.addAttribute("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSA));
        model.addAttribute("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_CSA));
        model.addAttribute("podeReiniciarSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_CSA));

        model.addAttribute("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_CSA, responsavel));
        model.addAttribute("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_CSA, responsavel));
        model.addAttribute("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA, responsavel));

        String consultarOp = "consultar_csa";
        String editarOp = "editar_csa";
        model.addAttribute("consultarOp", consultarOp);
        model.addAttribute("editarOp", editarOp);
    }

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_CSA;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/listarUsuarioCsa?acao=listar";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "../v3/consultarUsuarioCsa";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "../v3/inserirUsuarioCsa";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "../v3/editarUsuarioCsa";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "../v3/bloquearUsuarioCsa";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "../v3/reinicializarSenhaUsuarioCsa";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "../v3/detalharUsuarioCsa";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "../v3/excluirUsuarioCsa";
    }
}
