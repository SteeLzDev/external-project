package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.ConsultarConsignacaoParaCompra;
import com.zetra.econsig.soap.compra.ConsultarConsignacaoParaCompraResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class ConsultarConsignacaoParaCompraClient extends WebServiceGatewaySupport {

	public ConsultarConsignacaoParaCompraResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String loginServidor, String senhaServidor, String cpf, String servicoCodigo,
			String orgaoCodigo, String estabelecimentoCodigo) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConsultarConsignacaoParaCompra consultarConsignacao = new ConsultarConsignacaoParaCompra();
		consultarConsignacao.setCliente(objectFactory.createConsultarConsignacaoParaCompraCliente(""));
		consultarConsignacao.setConvenio(objectFactory.createConsultarConsignacaoParaCompraConvenio(""));
		consultarConsignacao.setUsuario(usuario);
		consultarConsignacao.setSenha(senha);
		consultarConsignacao.setAdeNumero(objectFactory.createConsultarConsignacaoParaCompraAdeNumero(adeNumero));
		consultarConsignacao.setAdeIdentificador(
				objectFactory.createConsultarConsignacaoParaCompraAdeIdentificador(adeIdentificador));
		consultarConsignacao.setMatricula(loginServidor);
		consultarConsignacao.setCpf(objectFactory.createConsultarConsignacaoParaCompraCpf(cpf));
		consultarConsignacao.setServicoCodigo(servicoCodigo);
		consultarConsignacao
				.setSenhaServidor(objectFactory.createConsultarConsignacaoParaCompraSenhaServidor(senhaServidor));
		consultarConsignacao.setBanco(objectFactory.createConsultarConsignacaoParaCompraBanco("1"));
		consultarConsignacao.setAgencia(objectFactory.createConsultarConsignacaoParaCompraAgencia("1111"));
		consultarConsignacao.setConta(objectFactory.createConsultarConsignacaoParaCompraConta("111111"));
		consultarConsignacao.setOrgaoCodigo(objectFactory.createConsultarConsignacaoParaCompraOrgaoCodigo(orgaoCodigo));
		consultarConsignacao.setEstabelecimentoCodigo(
				objectFactory.createConsultarConsignacaoParaCompraEstabelecimentoCodigo(estabelecimentoCodigo));

		return (ConsultarConsignacaoParaCompraResponse) getWebServiceTemplate()
				.marshalSendAndReceive(consultarConsignacao);
	}
}
