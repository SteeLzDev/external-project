package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarServicoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar serviço</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarServicoCommand extends RequisicaoExternaFolhaCommand {

	public ConsultarServicoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
	    super(parametros, responsavel);
    }

	@Override
	protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
		String svcIdentificador = (String) parametros.get(SVC_IDENTIFICADOR);
        String nseCodigo = (String) parametros.get(NSE_CODIGO);

		CustomTransferObject criterio = new CustomTransferObject();
		if (!TextHelper.isNull(svcIdentificador)) {
			criterio.setAttribute(Columns.SVC_IDENTIFICADOR, svcIdentificador);
		}
        if (!TextHelper.isNull(nseCodigo)) {
            criterio.setAttribute(Columns.SVC_NSE_CODIGO, nseCodigo);
        }

        ConvenioDelegate delegate = new ConvenioDelegate();
		List<TransferObject> svcList = delegate.lstServicos(criterio, responsavel);
		parametros.put(CONSULTA_SERVICOS, svcList);
	}
}
