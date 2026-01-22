package com.zetra.econsig.enomina.soap.client;

import java.io.IOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.SimularConsignacao;
import com.zetra.econsig.soap.SimularConsignacaoResponse;

public class SimularConsignacaoClient extends WebServiceGatewaySupport {

	public SimularConsignacaoResponse getResponse(String usuario, String senha, String matricula, String cpf, String codOrgao, String codEstabelecimento, 
			String codServico, String valorParcela, Integer prazo, String valorLiberado,
			String codVerba) throws IOException {

		ObjectFactory objectFactory = new ObjectFactory();
		
		SimularConsignacao simularConsignacao = new SimularConsignacao();
		simularConsignacao.setUsuario(usuario);
		simularConsignacao.setSenha(senha);
		simularConsignacao.setMatricula(matricula);
		simularConsignacao.setCpf(objectFactory.createSimularConsignacaoCpf(cpf));
		simularConsignacao.setOrgaoCodigo(objectFactory.createSimularConsignacaoOrgaoCodigo(codOrgao));
		simularConsignacao.setEstabelecimentoCodigo(objectFactory.createSimularConsignacaoEstabelecimentoCodigo(codEstabelecimento));
		simularConsignacao.setServicoCodigo(objectFactory.createSimularConsignacaoServicoCodigo(codServico));
		simularConsignacao.setValorParcela(valorParcela);
		simularConsignacao.setPrazo(objectFactory.createSimularConsignacaoPrazo(prazo));
		simularConsignacao.setValorLiberado(objectFactory.createSimularConsignacaoValorLiberado(Double.valueOf(500)));
		simularConsignacao.setCodVerba(objectFactory.createSimularConsignacaoCodVerba(codVerba));

		return (SimularConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(simularConsignacao);
	}

}
