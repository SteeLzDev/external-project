package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ConsultarConsignacao;
import com.zetra.econsig.soap.ConsultarConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ConsultarConsignacaoClient extends WebServiceGatewaySupport {

	public ConsultarConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String matricula, String cpf, String codOrgao, String codEstabelecimento) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConsultarConsignacao consultarConsignacao = new ConsultarConsignacao();
		consultarConsignacao.setCliente(objectFactory.createConsultarConsignacaoCliente(""));
		consultarConsignacao.setConvenio(objectFactory.createConsultarConsignacaoConvenio("LOCAL"));
		consultarConsignacao.setUsuario(usuario);
		consultarConsignacao.setSenha(senha);
		consultarConsignacao.setAdeNumero(objectFactory.createConsultarConsignacaoAdeNumero(adeNumero));
		consultarConsignacao.setAdeIdentificador(objectFactory.createConsultarConsignacaoAdeIdentificador(adeIdentificador));
		consultarConsignacao.setMatricula(matricula);
		consultarConsignacao.setCpf(objectFactory.createConsultarConsignacaoCpf(cpf));
		consultarConsignacao.setOrgaoCodigo(objectFactory.createConsultarConsignacaoOrgaoCodigo(codOrgao));
		consultarConsignacao.setEstabelecimentoCodigo(
				objectFactory.createConsultarConsignacaoEstabelecimentoCodigo(codEstabelecimento));

		return (ConsultarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(consultarConsignacao);
	}

}
