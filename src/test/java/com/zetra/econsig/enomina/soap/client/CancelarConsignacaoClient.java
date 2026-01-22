package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.CancelarConsignacao;
import com.zetra.econsig.soap.CancelarConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class CancelarConsignacaoClient extends WebServiceGatewaySupport {

	public CancelarConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		CancelarConsignacao cancelarConsignacao = new CancelarConsignacao();
		cancelarConsignacao.setCliente(objectFactory.createCancelarConsignacaoCliente(""));
		cancelarConsignacao
				.setConvenio(objectFactory.createCancelarConsignacaoConvenio("cnv_213464140_001_17167412007983"));
		cancelarConsignacao.setUsuario(usuario);
		cancelarConsignacao.setSenha(senha);
		cancelarConsignacao.setAdeNumero(objectFactory.createCancelarConsignacaoAdeNumero(adeNumero));
		cancelarConsignacao
				.setAdeIdentificador(objectFactory.createCancelarConsignacaoAdeIdentificador(adeIdentificador));
		cancelarConsignacao
				.setCodigoMotivoOperacao(objectFactory.createCancelarConsignacaoCodigoMotivoOperacao(codigoMotivo));
		cancelarConsignacao.setObsMotivoOperacao(objectFactory.createCancelarConsignacaoObsMotivoOperacao("Automacao"));

		return (CancelarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(cancelarConsignacao);
	}
}
