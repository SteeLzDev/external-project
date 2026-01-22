package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.IncluirDadoConsignacao;
import com.zetra.econsig.soap.IncluirDadoConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class IncluirDadoConsignacaoClient extends WebServiceGatewaySupport {

	public IncluirDadoConsignacaoResponse getResponse(String usuario, String senha, String adeNumero,
			String adeIdentificador, String dadoCodigo, String dadoValor) {

		ObjectFactory objectFactory = new ObjectFactory();

		IncluirDadoConsignacao incluirDadoConsignacao = new IncluirDadoConsignacao();
		incluirDadoConsignacao.setCliente(objectFactory.createIncluirDadoConsignacaoCliente(""));
		incluirDadoConsignacao
				.setConvenio(objectFactory.createIncluirDadoConsignacaoConvenio("cnv_213464140_001_17167412007983"));
		incluirDadoConsignacao.setUsuario(usuario);
		incluirDadoConsignacao.setSenha(senha);
		incluirDadoConsignacao.setAdeNumero(objectFactory.createIncluirDadoConsignacaoAdeNumero(adeNumero));
		incluirDadoConsignacao
				.setAdeIdentificador(objectFactory.createIncluirDadoConsignacaoAdeIdentificador(adeIdentificador));
		incluirDadoConsignacao.setDadoCodigo(dadoCodigo);
		incluirDadoConsignacao.setDadoValor(objectFactory.createIncluirDadoConsignacaoDadoValor(dadoValor));

		return (IncluirDadoConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(incluirDadoConsignacao);
	}
}
