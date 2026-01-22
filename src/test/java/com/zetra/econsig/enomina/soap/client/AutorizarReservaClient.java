package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.AutorizarReserva;
import com.zetra.econsig.soap.AutorizarReservaResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class AutorizarReservaClient extends WebServiceGatewaySupport {

	public AutorizarReservaResponse getResponse(String usuario, String senha, String loginServidor,
			String senhaServidor, Long adeNumero, String adeIdentificador, String codigoMotivo) {

		final ObjectFactory objectFactory = new ObjectFactory();

		final AutorizarReserva autorizarReserva = new AutorizarReserva();
		autorizarReserva.setCliente(objectFactory.createAutorizarReservaCliente(""));
		autorizarReserva.setConvenio(objectFactory.createAutorizarReservaConvenio("LOCAL"));
		autorizarReserva.setUsuario(usuario);
		autorizarReserva.setSenha(senha);
		autorizarReserva.setAdeNumero(objectFactory.createAutorizarReservaAdeNumero(adeNumero));
		autorizarReserva.setAdeIdentificador(objectFactory.createAutorizarReservaAdeIdentificador(adeIdentificador));
		autorizarReserva.setSenhaServidor(senhaServidor);
		autorizarReserva.setLoginServidor(loginServidor);
		autorizarReserva.setTokenAutServidor("");
		autorizarReserva.setCodigoMotivoOperacao(objectFactory.createAutorizarReservaCodigoMotivoOperacao(codigoMotivo));
		autorizarReserva.setObsMotivoOperacao(objectFactory.createAutorizarReservaObsMotivoOperacao("Automacao"));

		return (AutorizarReservaResponse) getWebServiceTemplate().marshalSendAndReceive(autorizarReserva);
	}

	public AutorizarReservaResponse getResponse(String usuario, String senha, String loginServidor,
			String senhaServidor, String adeIdentificador, String codigoMotivo) {

		final ObjectFactory objectFactory = new ObjectFactory();

		final AutorizarReserva autorizarReserva = new AutorizarReserva();
		autorizarReserva.setCliente(objectFactory.createAutorizarReservaCliente(""));
		autorizarReserva.setConvenio(objectFactory.createAutorizarReservaConvenio("cnv_213464140_001_17167412007983"));
		autorizarReserva.setUsuario(usuario);
		autorizarReserva.setSenha(senha);
		autorizarReserva.setAdeIdentificador(objectFactory.createAutorizarReservaAdeIdentificador(adeIdentificador));
		autorizarReserva.setSenhaServidor(senhaServidor);
		autorizarReserva.setLoginServidor(loginServidor);
		autorizarReserva.setTokenAutServidor("");
		autorizarReserva.setCodigoMotivoOperacao(objectFactory.createAutorizarReservaCodigoMotivoOperacao(codigoMotivo));
		autorizarReserva.setObsMotivoOperacao(objectFactory.createAutorizarReservaObsMotivoOperacao("Automacao"));

		return (AutorizarReservaResponse) getWebServiceTemplate().marshalSendAndReceive(autorizarReserva);
	}
}
