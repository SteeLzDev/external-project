package com.zetra.econsig.web.controller.usuario;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DetalharUsuarioConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Detalhar Usuário Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/detalharUsuarioCsa" })
public class DetalharUsuarioConsignatariaWebController extends AbstractDetalharUsuarioWebController {

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_CSA;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/detalharUsuarioCsa";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "/v3/consultarUsuarioCsa";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "/v3/inserirUsuarioCsa";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "/v3/editarUsuarioCsa";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "/v3/bloquearUsuarioCsa";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "/v3/reinicializarUsuarioCsa";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "/v3/detalharUsuarioCsa";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "/v3/excluirUsuarioCsa";
    }
}
