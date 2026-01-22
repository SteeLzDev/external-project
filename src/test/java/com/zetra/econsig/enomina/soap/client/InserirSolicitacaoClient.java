package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.InserirSolicitacao;
import com.zetra.econsig.soap.InserirSolicitacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class InserirSolicitacaoClient extends WebServiceGatewaySupport {

	public InserirSolicitacaoResponse getResponse(String usuario, String senha, String matricula, String senhaServidor,
			String codVerba, String codServico, String cpf, int prazo, String valorParcela, String codOrgao,
			String codEstabelecimento, String telefone) {

		final ObjectFactory objectFactory = new ObjectFactory();

		final InserirSolicitacao inserirSolicitacao = new InserirSolicitacao();
		inserirSolicitacao.setCliente(objectFactory.createInserirSolicitacaoCliente(""));
		inserirSolicitacao.setConvenio(objectFactory.createInserirSolicitacaoConvenio("LOCAL"));
		inserirSolicitacao.setUsuario(usuario);
		inserirSolicitacao.setSenha(senha);
		inserirSolicitacao.setMatricula(matricula);
		inserirSolicitacao.setCpf(objectFactory.createInserirSolicitacaoCpf(cpf));
		inserirSolicitacao.setOrgaoCodigo(objectFactory.createInserirSolicitacaoOrgaoCodigo(codOrgao));
		inserirSolicitacao.setEstabelecimentoCodigo(objectFactory.createInserirSolicitacaoEstabelecimentoCodigo(codEstabelecimento));
		inserirSolicitacao.setSenhaServidor(objectFactory.createInserirSolicitacaoSenhaServidor(senhaServidor));
		inserirSolicitacao.setLoginServidor(matricula);
		inserirSolicitacao.setTokenAutServidor("");
		inserirSolicitacao.setServicoCodigo(objectFactory.createInserirSolicitacaoServicoCodigo(codServico));
		inserirSolicitacao.setValorParcela(valorParcela);
		inserirSolicitacao.setPrazo(objectFactory.createInserirSolicitacaoPrazo(prazo));
		inserirSolicitacao.setValorLiberado(objectFactory.createInserirSolicitacaoValorLiberado((double) 3000));
		inserirSolicitacao.setCodVerba(objectFactory.createInserirSolicitacaoCodVerba(codVerba));
		inserirSolicitacao.setEndereco("Avenida Portugal");
		inserirSolicitacao.setBairro("Itapoa");
		inserirSolicitacao.setCidade("Belo Horizonte");
		inserirSolicitacao.setUf("MG");
		inserirSolicitacao.setCep("31710400");
		inserirSolicitacao.setTelefone(telefone);

		return (InserirSolicitacaoResponse) getWebServiceTemplate().marshalSendAndReceive(inserirSolicitacao);
	}

	public InserirSolicitacaoResponse getResponse(String usuario, String senha, String matricula, String senhaServidor,
			String codVerba, int prazo, String valorParcela, String codOrgao, String codEstabelecimento) {

		final ObjectFactory objectFactory = new ObjectFactory();

		final InserirSolicitacao inserirSolicitacao = new InserirSolicitacao();
		inserirSolicitacao.setUsuario(usuario);
		inserirSolicitacao.setSenha(senha);
		inserirSolicitacao.setMatricula(matricula);
		inserirSolicitacao.setOrgaoCodigo(objectFactory.createInserirSolicitacaoOrgaoCodigo(codOrgao));
		inserirSolicitacao.setEstabelecimentoCodigo(objectFactory.createInserirSolicitacaoEstabelecimentoCodigo(codEstabelecimento));
		inserirSolicitacao.setSenhaServidor(objectFactory.createInserirSolicitacaoSenhaServidor(senhaServidor));
		inserirSolicitacao.setValorParcela(valorParcela);
		inserirSolicitacao.setPrazo(objectFactory.createInserirSolicitacaoPrazo(prazo));
		inserirSolicitacao.setCodVerba(objectFactory.createInserirSolicitacaoCodVerba(codVerba));
        inserirSolicitacao.setEndereco("Avenida Portugal");
        inserirSolicitacao.setBairro("Itapoa");
        inserirSolicitacao.setCidade("Belo Horizonte");
        inserirSolicitacao.setUf("MG");
        inserirSolicitacao.setCep("31710400");
		inserirSolicitacao.setTelefone("3269587463");

		return (InserirSolicitacaoResponse) getWebServiceTemplate().marshalSendAndReceive(inserirSolicitacao);
	}
}
