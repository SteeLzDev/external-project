package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.ReativarConsignacao;
import com.zetra.econsig.soap.ReativarConsignacaoResponse;

public class ReativarConsignacaoClient extends WebServiceGatewaySupport {

	public ReativarConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		ReativarConsignacao reativarConsignacao = new ReativarConsignacao();
		reativarConsignacao.setCliente(objectFactory.createReativarConsignacaoCliente(""));
		reativarConsignacao.setConvenio(objectFactory.createReativarConsignacaoConvenio(""));
		reativarConsignacao.setUsuario(usuario);
		reativarConsignacao.setSenha(senha);
		reativarConsignacao.setAdeNumero(objectFactory.createReativarConsignacaoAdeNumero(adeNumero));
		reativarConsignacao
				.setAdeIdentificador(objectFactory.createReativarConsignacaoAdeIdentificador(adeIdentificador));
		reativarConsignacao
				.setCodigoMotivoOperacao(objectFactory.createReativarConsignacaoCodigoMotivoOperacao(codigoMotivo));
		reativarConsignacao.setObsMotivoOperacao(objectFactory.createReativarConsignacaoObsMotivoOperacao("Automacao"));

		return (ReativarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(reativarConsignacao);
	}
}
