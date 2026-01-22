package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ConfirmarReserva;
import com.zetra.econsig.soap.ConfirmarReservaResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ConfirmarReservaClient extends WebServiceGatewaySupport {

	public ConfirmarReservaResponse getResponse(String usuario, String senha, Long adeNumero, String adeIdentificador,
			String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConfirmarReserva confirmarReserva = new ConfirmarReserva();
		confirmarReserva.setCliente(objectFactory.createConfirmarReservaCliente(""));
		confirmarReserva.setConvenio(objectFactory.createConfirmarReservaConvenio("cnv_213464140_001_17167412007983"));
		confirmarReserva.setUsuario(usuario);
		confirmarReserva.setSenha(senha);
		confirmarReserva.setAdeNumero(objectFactory.createConfirmarReservaAdeNumero(adeNumero));
		confirmarReserva.setAdeIdentificador(objectFactory.createConfirmarReservaAdeIdentificador(adeIdentificador));
		confirmarReserva
				.setCodigoMotivoOperacao(objectFactory.createConfirmarReservaCodigoMotivoOperacao(codigoMotivo));
		confirmarReserva.setObsMotivoOperacao(objectFactory.createConfirmarReservaObsMotivoOperacao("Automacao"));

		return (ConfirmarReservaResponse) getWebServiceTemplate().marshalSendAndReceive(confirmarReserva);
	}
}
