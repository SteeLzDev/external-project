package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ConfirmarSolicitacao;
import com.zetra.econsig.soap.ConfirmarSolicitacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ConfirmarSolicitacaoClient extends WebServiceGatewaySupport {

	public ConfirmarSolicitacaoResponse getResponse(String usuario, String senha, String loginServidor,
			String senhaServidor, Long adeNumero, Boolean alteraValorLiberado, String adeIdentificador,
			Double coeficiente, String codigoMotivo) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConfirmarSolicitacao confirmarSolicitacao = new ConfirmarSolicitacao();
		confirmarSolicitacao.setCliente(objectFactory.createInserirSolicitacaoCliente(""));
		confirmarSolicitacao.setConvenio(objectFactory.createInserirSolicitacaoConvenio("LOCAL"));
		confirmarSolicitacao.setUsuario(usuario);
		confirmarSolicitacao.setSenha(senha);
		confirmarSolicitacao.setAdeNumero(objectFactory.createConfirmarSolicitacaoAdeNumero(adeNumero));
		confirmarSolicitacao
				.setAdeIdentificador(objectFactory.createConfirmarSolicitacaoAdeIdentificador(adeIdentificador));
		confirmarSolicitacao.setNovoAdeIdentificador(objectFactory.createConfirmarSolicitacaoNovoAdeIdentificador(""));
		confirmarSolicitacao.setSenhaServidor(senhaServidor);
		confirmarSolicitacao.setLoginServidor(loginServidor);
		confirmarSolicitacao.setTokenAutServidor("");
		confirmarSolicitacao.setCodigoAutorizacao("");
		confirmarSolicitacao.setCoeficiente(coeficiente);
		confirmarSolicitacao.setAlteraValorLiberado(alteraValorLiberado);
		confirmarSolicitacao.setBanco(objectFactory.createConfirmarSolicitacaoBanco("123"));
		confirmarSolicitacao.setAgencia(objectFactory.createConfirmarSolicitacaoAgencia("1452"));
		confirmarSolicitacao.setConta(objectFactory.createConfirmarSolicitacaoConta("00025639"));
		confirmarSolicitacao
				.setCodigoMotivoOperacao(objectFactory.createConfirmarSolicitacaoCodigoMotivoOperacao(codigoMotivo));
		confirmarSolicitacao
				.setObsMotivoOperacao(objectFactory.createConfirmarSolicitacaoObsMotivoOperacao("Automacao"));

		return (ConfirmarSolicitacaoResponse) getWebServiceTemplate().marshalSendAndReceive(confirmarSolicitacao);
	}

	public ConfirmarSolicitacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			Boolean alteraValorLiberado) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConfirmarSolicitacao confirmarSolicitacao = new ConfirmarSolicitacao();
		confirmarSolicitacao.setUsuario(usuario);
		confirmarSolicitacao.setSenha(senha);
		confirmarSolicitacao.setAdeNumero(objectFactory.createConfirmarSolicitacaoAdeNumero(adeNumero));
		confirmarSolicitacao.setNovoAdeIdentificador(objectFactory.createConfirmarSolicitacaoNovoAdeIdentificador(""));
		confirmarSolicitacao.setAlteraValorLiberado(alteraValorLiberado);

		return (ConfirmarSolicitacaoResponse) getWebServiceTemplate().marshalSendAndReceive(confirmarSolicitacao);
	}
}
