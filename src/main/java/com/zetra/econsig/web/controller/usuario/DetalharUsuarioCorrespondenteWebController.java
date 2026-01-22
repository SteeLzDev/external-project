package com.zetra.econsig.web.controller.usuario;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DetalharUsuarioCorrespondenteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Detalhar Usuário Correspondente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/detalharUsuarioCor" })
public class DetalharUsuarioCorrespondenteWebController extends AbstractDetalharUsuarioWebController {

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_COR;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/detalharUsuarioCor";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "/v3/consultarUsuarioOrg";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "/v3/inserirUsuarioOrg";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "/v3/editarUsuarioOrg";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "/v3/bloquearUsuarioOrg";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "/v3/reinicializarUsuarioOrg";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "/v3/detalharUsuarioOrg";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "/v3/excluirUsuarioOrg";
    }
}
