package com.zetra.econsig.web.controller.usuario;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DetalharUsuarioSuporteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Detalhar Usuário Suporte.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/detalharUsuarioSup" })
public class DetalharUsuarioSuporteWebController extends AbstractDetalharUsuarioWebController {

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_SUP;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/detalharUsuarioSup";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "/v3/consultarUsuarioSup";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "/v3/inserirUsuarioSup";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "/v3/editarUsuarioSup";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "/v3/bloquearUsuarioSup";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "/v3/reinicializarUsuarioSup";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "/v3/detalharUsuarioSup";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "/v3/excluirUsuarioSup";
    }
}
