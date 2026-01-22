package com.zetra.econsig.web.controller.usuario;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DetalharUsuarioConsignanteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Detalhar Usuário Consignante.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/detalharUsuarioCse" })
public class DetalharUsuarioConsignanteWebController extends AbstractDetalharUsuarioWebController {

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_CSE;
    }

    @Override
    protected String getLinkAction() {
        return "../v3/detalharUsuarioCse";
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "/v3/consultarUsuarioCse";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "/v3/inserirUsuarioCse";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "/v3/editarUsuarioCse";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "/v3/bloquearUsuarioCse";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "/v3/reinicializarUsuarioCse";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "/v3/detalharUsuarioCse";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "/v3/excluirUsuarioCse";
    }
}
