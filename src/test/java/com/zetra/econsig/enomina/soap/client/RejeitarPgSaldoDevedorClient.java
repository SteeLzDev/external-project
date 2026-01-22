package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.ObjectFactory;
import com.zetra.econsig.soap.compra.RejeitarPgSaldoDevedor;
import com.zetra.econsig.soap.compra.RejeitarPgSaldoDevedorResponse;

public class RejeitarPgSaldoDevedorClient extends WebServiceGatewaySupport {

	public RejeitarPgSaldoDevedorResponse getResponse(String usuario, String senha, Long adeNumero, String observacao) {

		ObjectFactory objectFactory = new ObjectFactory();

		RejeitarPgSaldoDevedor rejeitarPgSaldoDevedor = new RejeitarPgSaldoDevedor();
		rejeitarPgSaldoDevedor.setCliente(objectFactory.createRejeitarPgSaldoDevedorCliente(""));
		rejeitarPgSaldoDevedor.setConvenio(objectFactory.createRejeitarPgSaldoDevedorConvenio(""));
		rejeitarPgSaldoDevedor.setUsuario(usuario);
		rejeitarPgSaldoDevedor.setSenha(senha);
		rejeitarPgSaldoDevedor.setAdeNumero(adeNumero);
		rejeitarPgSaldoDevedor.setObservacao(objectFactory.createRejeitarPgSaldoDevedorObservacao(observacao));

		return (RejeitarPgSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(rejeitarPgSaldoDevedor);
	}
}
