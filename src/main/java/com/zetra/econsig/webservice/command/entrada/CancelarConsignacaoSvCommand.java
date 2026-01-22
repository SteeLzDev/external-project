package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;

import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CancelarConsignacaoSvCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cancelar consignação pelo servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CancelarConsignacaoSvCommand extends RequisicaoExternaCommand {

    public CancelarConsignacaoSvCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        CustomTransferObject autorizacao = (CustomTransferObject) parametros.get(CONSIGNACAO);

        if (autorizacao != null) {
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

            consigDelegate.cancelarConsignacao(adeCodigo, false, responsavel);
        }
    }
}
