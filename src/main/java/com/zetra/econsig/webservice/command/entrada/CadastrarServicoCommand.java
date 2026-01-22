package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.SERVICO;

import java.util.Map;

import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarServicoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cadastrar serviço</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarServicoCommand extends RequisicaoExternaFolhaCommand {

	public CadastrarServicoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
	    super(parametros, responsavel);
	}

	@Override
	protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
		ServicoTransferObject svcTO = (ServicoTransferObject) parametros.get(SERVICO);
		ConvenioDelegate delegate = new ConvenioDelegate();
		delegate.createServico(svcTO, responsavel);
	}
}
