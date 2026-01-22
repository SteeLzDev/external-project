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
 * <p>Title: ListarUsuarioCorrespondenteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Usuário Correspondente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuarioCor" })
public class ListarUsuarioCorrespondenteWebController extends AbstractListarUsuarioPapelWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("podeCriarUsu", responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_COR));
        model.addAttribute("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR));
        model.addAttribute("podeConsultarUsu", responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR));
        model.addAttribute("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_COR));
        model.addAttribute("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_COR));
        model.addAttribute("podeReiniciarSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_COR));

        model.addAttribute("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_COR, responsavel));
        model.addAttribute("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_COR, responsavel));
        model.addAttribute("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR, responsavel));

        String consultarOp = "consultar_cor";
        String editarOp = "editar_cor";
        model.addAttribute("consultarOp", consultarOp);
        model.addAttribute("editarOp", editarOp);
    }

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_COR;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/listarUsuarioCor?acao=listar";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "../v3/consultarUsuarioCor";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "../v3/inserirUsuarioCor";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "../v3/editarUsuarioCor";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "../v3/bloquearUsuarioCor";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "../v3/reinicializarSenhaUsuarioCor";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "../v3/detalharUsuarioCor";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "../v3/excluirUsuarioCor";
    }
}
