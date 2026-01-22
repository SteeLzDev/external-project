package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.LiquidarCompra;
import com.zetra.econsig.soap.compra.LiquidarCompraResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class LiquidarCompraClient extends WebServiceGatewaySupport {

	public LiquidarCompraResponse getResponse(String usuario, String senha, Long adeNumero, String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		LiquidarCompra liquidarCompra = new LiquidarCompra();
		liquidarCompra.setCliente(objectFactory.createLiquidarCompraCliente(""));
		liquidarCompra.setConvenio(objectFactory.createLiquidarCompraConvenio(""));
		liquidarCompra.setUsuario(usuario);
		liquidarCompra.setSenha(senha);
		liquidarCompra.setAdeNumero(adeNumero);
		liquidarCompra.setCodigoMotivoOperacao(objectFactory.createLiquidarCompraCodigoMotivoOperacao(codigoMotivo));
		liquidarCompra.setObsMotivoOperacao(objectFactory.createLiquidarCompraObsMotivoOperacao("Automacao"));

		return (LiquidarCompraResponse) getWebServiceTemplate().marshalSendAndReceive(liquidarCompra);
	}
}
