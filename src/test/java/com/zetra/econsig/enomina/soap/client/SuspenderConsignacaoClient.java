package com.zetra.econsig.enomina.soap.client;

import java.io.IOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.SuspenderConsignacao;
import com.zetra.econsig.soap.SuspenderConsignacaoResponse;

public class SuspenderConsignacaoClient extends WebServiceGatewaySupport {

	public SuspenderConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String codigoMotivoOperacao, String obsMotivoOperacao) throws IOException {
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		SuspenderConsignacao suspenderConsignacao = new SuspenderConsignacao();
		suspenderConsignacao.setUsuario(usuario);
		suspenderConsignacao.setSenha(senha);
		suspenderConsignacao.setAdeNumero(adeNumero);
		suspenderConsignacao.setCodigoMotivoOperacao(objectFactory.createCancelarRenegociacaoCodigoMotivoOperacao(codigoMotivoOperacao));
		suspenderConsignacao.setObsMotivoOperacao(objectFactory.createCancelarRenegociacaoObsMotivoOperacao(obsMotivoOperacao));
		
		return (SuspenderConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(suspenderConsignacao);
	}
	
}
