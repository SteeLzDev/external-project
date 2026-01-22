package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.CancelarReserva;
import com.zetra.econsig.soap.CancelarReservaResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class CancelarReservaClient extends WebServiceGatewaySupport {

	public CancelarReservaResponse getResponse(String usuario, String senha, Long adeNumero, String adeIdentificador,
			String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		CancelarReserva cancelarReserva = new CancelarReserva();
		cancelarReserva.setCliente(objectFactory.createCancelarReservaCliente(""));
		cancelarReserva.setConvenio(objectFactory.createCancelarReservaConvenio("cnv_213464140_001_17167412007983"));
		cancelarReserva.setUsuario(usuario);
		cancelarReserva.setSenha(senha);
		cancelarReserva.setAdeNumero(objectFactory.createCancelarReservaAdeNumero(adeNumero));
		cancelarReserva.setAdeIdentificador(objectFactory.createCancelarReservaAdeIdentificador(adeIdentificador));
		cancelarReserva
				.setCodigoMotivoOperacao(objectFactory.createCancelarReservaCodigoMotivoOperacao(codigoMotivo));
		cancelarReserva.setObsMotivoOperacao(objectFactory.createCancelarReservaObsMotivoOperacao("Automacao"));

		return (CancelarReservaResponse) getWebServiceTemplate().marshalSendAndReceive(cancelarReserva);
	}
}
