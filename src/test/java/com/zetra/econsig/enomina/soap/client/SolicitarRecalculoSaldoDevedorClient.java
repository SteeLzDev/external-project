package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.ObjectFactory;
import com.zetra.econsig.soap.compra.SolicitarRecalculoSaldoDevedor;
import com.zetra.econsig.soap.compra.SolicitarRecalculoSaldoDevedorResponse;

public class SolicitarRecalculoSaldoDevedorClient extends WebServiceGatewaySupport {

	public SolicitarRecalculoSaldoDevedorResponse getResponse(String usuario, String senha, Long adeNumero,
			String observacao) {

		ObjectFactory objectFactory = new ObjectFactory();

		SolicitarRecalculoSaldoDevedor rejeitarPgSaldoDevedor = new SolicitarRecalculoSaldoDevedor();
		rejeitarPgSaldoDevedor.setCliente(objectFactory.createSolicitarRecalculoSaldoDevedorCliente(""));
		rejeitarPgSaldoDevedor.setConvenio(objectFactory.createSolicitarRecalculoSaldoDevedorConvenio(""));
		rejeitarPgSaldoDevedor.setUsuario(usuario);
		rejeitarPgSaldoDevedor.setSenha(senha);
		rejeitarPgSaldoDevedor.setAdeNumero(adeNumero);
		rejeitarPgSaldoDevedor.setObservacao(objectFactory.createSolicitarRecalculoSaldoDevedorObservacao(observacao));

		return (SolicitarRecalculoSaldoDevedorResponse) getWebServiceTemplate()
				.marshalSendAndReceive(rejeitarPgSaldoDevedor);
	}
}
