package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTOS;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarEstabelecimentoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar estabelecimento</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarEstabelecimentoCommand extends RequisicaoExternaFolhaCommand {

	public ConsultarEstabelecimentoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
	    super(parametros, responsavel);
    }

	@Override
	protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
		ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
		String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);

		CustomTransferObject criterio = null;
		if (!TextHelper.isNull(estIdentificador)) {
			criterio = new CustomTransferObject();
			criterio.setAttribute(Columns.EST_IDENTIFICADOR, estIdentificador);
		}

		List<TransferObject> estList = cseDelegate.lstEstabelecimentos(criterio, responsavel);
		parametros.put(ESTABELECIMENTOS, estList);
	}
}
