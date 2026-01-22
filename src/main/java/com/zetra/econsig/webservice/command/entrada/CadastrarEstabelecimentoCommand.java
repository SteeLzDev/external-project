package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;

import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarEstabelecimentoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cadastrar estabelecimento</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarEstabelecimentoCommand extends RequisicaoExternaFolhaCommand {

	public CadastrarEstabelecimentoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
	    super(parametros, responsavel);
	}

	@Override
	protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
		EstabelecimentoTransferObject estTO = (EstabelecimentoTransferObject) parametros.get(ESTABELECIMENTO);
		estTO.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
		estTO.setEstAtivo(CodedValues.STS_ATIVO);
		ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
		cseDelegate.createEstabelecimento(estTO, responsavel);
	}
}
