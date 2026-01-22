package com.zetra.econsig.web.controller.consignacao;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>Title: EditarAnexosSolicitacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar Anexos da Solicitação.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarAnexosSolicitacao" })
public class EditarAnexosSolicitacaoWebController extends EditarAnexosConsignacaoWebController {

    @Override
    protected String getAcaoFormulario() {
        return "../v3/editarAnexosSolicitacao";
    }

    @Override
    protected String getTipoArquivo() {
        return "anexo_solicitacao";
    }
}
