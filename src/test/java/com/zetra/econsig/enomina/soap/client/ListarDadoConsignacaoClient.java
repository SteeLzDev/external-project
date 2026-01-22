package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ListarDadoConsignacao;
import com.zetra.econsig.soap.ListarDadoConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ListarDadoConsignacaoClient extends WebServiceGatewaySupport {

	public ListarDadoConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String dadoCodigo) {

		final ObjectFactory objectFactory = new ObjectFactory();

		final ListarDadoConsignacao listarDadoConsignacao = new ListarDadoConsignacao();
		listarDadoConsignacao.setCliente(objectFactory.createListarDadoConsignacaoCliente(""));
		listarDadoConsignacao.setConvenio(objectFactory.createListarDadoConsignacaoConvenio("cnv_213464140_001_17167412007983"));
		listarDadoConsignacao.setUsuario(usuario);
		listarDadoConsignacao.setSenha(senha);
		listarDadoConsignacao.setAdeNumero(objectFactory.createListarDadoConsignacaoAdeNumero(adeNumero));
		listarDadoConsignacao.setAdeIdentificador(objectFactory.createListarDadoConsignacaoAdeIdentificador(adeIdentificador));
		listarDadoConsignacao.setDadoCodigo(dadoCodigo);

		return (ListarDadoConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(listarDadoConsignacao);
	}
}
