package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.CancelarCompra;
import com.zetra.econsig.soap.compra.CancelarCompraResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class CancelarCompraClient extends WebServiceGatewaySupport {

	public CancelarCompraResponse getResponse(String usuario, String senha, Long adeNumero, String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		CancelarCompra cancelarCompra = new CancelarCompra();
		cancelarCompra.setCliente(objectFactory.createCancelarCompraCliente(""));
		cancelarCompra.setConvenio(objectFactory.createCancelarCompraConvenio("cnv_213464140_001_17167412007983"));
		cancelarCompra.setUsuario(usuario);
		cancelarCompra.setSenha(senha);
		cancelarCompra.setAdeNumero(adeNumero);
		cancelarCompra.setCodigoMotivoOperacao(objectFactory.createCancelarCompraCodigoMotivoOperacao(codigoMotivo));
		cancelarCompra.setObsMotivoOperacao(objectFactory.createCancelarCompraObsMotivoOperacao("Automacao"));

		return (CancelarCompraResponse) getWebServiceTemplate().marshalSendAndReceive(cancelarCompra);
	}
}
