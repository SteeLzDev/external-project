package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.ObjectFactory;
import com.zetra.econsig.soap.compra.RetirarContratoDaCompra;
import com.zetra.econsig.soap.compra.RetirarContratoDaCompraResponse;

public class RetirarContratoDaCompraClient extends WebServiceGatewaySupport {

	public RetirarContratoDaCompraResponse getResponse(String usuario, String senha, Long adeNumero,
			String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		RetirarContratoDaCompra retirarContratoDaCompra = new RetirarContratoDaCompra();
		retirarContratoDaCompra.setCliente(objectFactory.createRetirarContratoDaCompraCliente(""));
		retirarContratoDaCompra.setConvenio(objectFactory.createRetirarContratoDaCompraConvenio(""));
		retirarContratoDaCompra.setUsuario(usuario);
		retirarContratoDaCompra.setSenha(senha);
		retirarContratoDaCompra.setAdeNumero(adeNumero);
		retirarContratoDaCompra.setObservacao(objectFactory.createRetirarContratoDaCompraObservacao("Automacao"));
		retirarContratoDaCompra
				.setCodigoMotivoOperacao(objectFactory.createRetirarContratoDaCompraCodigoMotivoOperacao(codigoMotivo));
		retirarContratoDaCompra
				.setObsMotivoOperacao(objectFactory.createRetirarContratoDaCompraObsMotivoOperacao("Automacao"));

		return (RetirarContratoDaCompraResponse) getWebServiceTemplate().marshalSendAndReceive(retirarContratoDaCompra);
	}
}
