package com.zetra.econsig.web.controller.saldodevedor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>Title: EditarSaldoDevedorSolicitacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar Saldo Devedor de Solicitação do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: isaac.abreu $
 * $Revision: 23893 $
 * $Date: 2018-03-13 11:42:02 -0300 (ter, 13 mar 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarSaldoDevedorSolicitacao" })
public class EditarSaldoDevedorSolicitacaoWebController extends EditarSaldoDevedorWebController {

    @Override
    protected String getUrlEditarSaldoDevedor() {
    	return "/v3/editarSaldoDevedorSolicitacao";
    }
}
