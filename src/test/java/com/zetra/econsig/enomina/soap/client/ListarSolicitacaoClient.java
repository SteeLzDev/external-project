package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ListarSolicitacao;
import com.zetra.econsig.soap.ListarSolicitacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ListarSolicitacaoClient extends WebServiceGatewaySupport {

	public ListarSolicitacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String matricula, String cpf, String codOrgao, String codEstabelecimento) {

		ObjectFactory objectFactory = new ObjectFactory();

		ListarSolicitacao listarSolicitacao = new ListarSolicitacao();
		listarSolicitacao.setCliente(objectFactory.createListarSolicitacaoCliente(""));
		listarSolicitacao.setConvenio(objectFactory.createListarSolicitacaoConvenio("LOCAL"));
		listarSolicitacao.setUsuario(usuario);
		listarSolicitacao.setSenha(senha);
		listarSolicitacao.setAdeNumero(objectFactory.createListarSolicitacaoAdeNumero(adeNumero));
		listarSolicitacao.setAdeIdentificador(objectFactory.createListarSolicitacaoAdeIdentificador(adeIdentificador));
		listarSolicitacao.setMatricula(matricula);
		listarSolicitacao.setCpf(objectFactory.createListarSolicitacaoCpf(cpf));
		listarSolicitacao.setOrgaoCodigo(objectFactory.createListarSolicitacaoOrgaoCodigo(codOrgao));
		listarSolicitacao.setEstabelecimentoCodigo(
				objectFactory.createListarSolicitacaoEstabelecimentoCodigo(codEstabelecimento));

		return (ListarSolicitacaoResponse) getWebServiceTemplate().marshalSendAndReceive(listarSolicitacao);
	}

}
