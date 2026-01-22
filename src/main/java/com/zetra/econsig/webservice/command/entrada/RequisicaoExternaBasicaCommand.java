package com.zetra.econsig.webservice.command.entrada;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RequisicaoExternaBasicaCommand</p>
 * <p>Description: Implementação básica de RequisicaoExternaCommand</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RequisicaoExternaBasicaCommand extends RequisicaoExternaCommand {

    public RequisicaoExternaBasicaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
    }
}
