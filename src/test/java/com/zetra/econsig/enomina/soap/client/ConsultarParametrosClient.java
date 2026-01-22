package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ConsultarParametros;
import com.zetra.econsig.soap.ConsultarParametrosResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ConsultarParametrosClient extends WebServiceGatewaySupport {

	public ConsultarParametrosResponse getResponse(String usuario, String senha, String codVerba, String codServico,
			String codOrgao, String codEstabelecimento) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConsultarParametros consultarParametros = new ConsultarParametros();
		consultarParametros.setCliente(objectFactory.createConsultarParametrosCliente(""));
		consultarParametros.setConvenio(objectFactory.createConsultarParametrosConvenio("LOCAL"));
		consultarParametros.setUsuario(usuario);
		consultarParametros.setSenha(senha);
		consultarParametros.setCodVerba(objectFactory.createConsultarParametrosCodVerba(codVerba));
		consultarParametros.setServicoCodigo(objectFactory.createConsultarParametrosServicoCodigo(codServico));
		consultarParametros.setOrgaoCodigo(codOrgao);
		consultarParametros.setEstabelecimentoCodigo(codEstabelecimento);

		return (ConsultarParametrosResponse) getWebServiceTemplate().marshalSendAndReceive(consultarParametros);
	}
}
