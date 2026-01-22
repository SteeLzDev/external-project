package com.zetra.econsig.enomina.soap.client;

import java.io.IOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.CancelarRenegociacao;
import com.zetra.econsig.soap.CancelarRenegociacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class CancelarRenegociacaoClient extends WebServiceGatewaySupport {

	public CancelarRenegociacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String codigoMotivoOperacao, String obsMotivoOperacao) throws IOException {
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		CancelarRenegociacao cancelarRenegocicao = new CancelarRenegociacao();
		cancelarRenegocicao.setUsuario(usuario);
		cancelarRenegocicao.setSenha(senha);
		cancelarRenegocicao.setAdeNumero(adeNumero);
		cancelarRenegocicao.setCodigoMotivoOperacao(objectFactory.createCancelarRenegociacaoCodigoMotivoOperacao(codigoMotivoOperacao));
		cancelarRenegocicao.setObsMotivoOperacao(objectFactory.createCancelarRenegociacaoObsMotivoOperacao(obsMotivoOperacao));
		
		return (CancelarRenegociacaoResponse) getWebServiceTemplate().marshalSendAndReceive(cancelarRenegocicao);
	}
	
}
