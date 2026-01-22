package com.zetra.econsig.enomina.soap.client;

import java.io.IOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.RenegociarConsignacao;
import com.zetra.econsig.soap.RenegociarConsignacaoResponse;

public class RenegociarConsignacaoClient extends WebServiceGatewaySupport {

	public RenegociarConsignacaoResponse getResponse(String usuario, String senha, Long adeNumeros, 
			Double valorParcela, String valorLiberado, String codVerba, String prazo, String senhaServidor, String matricula) throws IOException {
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		RenegociarConsignacao renegociarConsignacao = new RenegociarConsignacao();
		renegociarConsignacao.setUsuario(usuario);
		renegociarConsignacao.setSenha(senha);
		renegociarConsignacao.setAdeNumeros(adeNumeros);
		renegociarConsignacao.setValorParcela(valorParcela);
		renegociarConsignacao.setValorLiberado(objectFactory.createRenegociarConsignacaoValorLiberado(String.valueOf(500)));
		renegociarConsignacao.setCodVerba(objectFactory.createRenegociarConsignacaoCodVerba(codVerba));
		renegociarConsignacao.setPrazo(objectFactory.createRenegociarConsignacaoPrazo(prazo));
		renegociarConsignacao.setSenhaServidor(senhaServidor);
		renegociarConsignacao.setMatricula(matricula);

		return (RenegociarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(renegociarConsignacao);
	}
	
}
