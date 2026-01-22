package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.LiquidarConsignacao;
import com.zetra.econsig.soap.LiquidarConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class LiquidarConsignacaoClient extends WebServiceGatewaySupport {

	public LiquidarConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String codigoMotivo, String periodo) {

		ObjectFactory objectFactory = new ObjectFactory();

		LiquidarConsignacao liquidarConsignacao = new LiquidarConsignacao();
		liquidarConsignacao.setCliente(objectFactory.createLiquidarConsignacaoCliente(""));
		liquidarConsignacao.setConvenio(objectFactory.createLiquidarConsignacaoConvenio(""));
		liquidarConsignacao.setUsuario(usuario);
		liquidarConsignacao.setSenha(senha);
		liquidarConsignacao.setAdeNumero(objectFactory.createLiquidarConsignacaoAdeNumero(adeNumero));
		liquidarConsignacao
				.setAdeIdentificador(objectFactory.createLiquidarConsignacaoAdeIdentificador(adeIdentificador));
		liquidarConsignacao
				.setCodigoMotivoOperacao(objectFactory.createLiquidarConsignacaoCodigoMotivoOperacao(codigoMotivo));
		liquidarConsignacao.setObsMotivoOperacao(objectFactory.createLiquidarConsignacaoObsMotivoOperacao("Automacao"));
		liquidarConsignacao.setPeriodo(objectFactory.createLiquidarConsignacaoPeriodo(periodo));

		return (LiquidarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(liquidarConsignacao);
	}
}
