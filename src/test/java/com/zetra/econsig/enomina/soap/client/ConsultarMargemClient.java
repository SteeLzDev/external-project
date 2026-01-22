package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ConsultarMargem;
import com.zetra.econsig.soap.ConsultarMargemResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class ConsultarMargemClient extends WebServiceGatewaySupport {

	public ConsultarMargemResponse getResponse(String usuario, String senha, String matricula, String loginServidor, String senhaServidor,
			String codVerba, String codServico, String cpf, String valorParcela, String codOrgao,
			String codEstabelecimento) {

		ObjectFactory objectFactory = new ObjectFactory();

		ConsultarMargem consultarMargem = new ConsultarMargem();
		consultarMargem.setCliente(objectFactory.createConsultarMargemCliente(""));
		consultarMargem.setConvenio(objectFactory.createConsultarMargemConvenio("LOCAL"));
		consultarMargem.setUsuario(usuario);
		consultarMargem.setSenha(senha);
		consultarMargem.setMatricula(matricula);
		consultarMargem.setCpf(objectFactory.createConsultarMargemCpf(cpf));
		consultarMargem.setOrgaoCodigo(objectFactory.createConsultarMargemOrgaoCodigo(codOrgao));
		consultarMargem.setEstabelecimentoCodigo(objectFactory.createConsultarMargemEstabelecimentoCodigo(codEstabelecimento));
		consultarMargem.setValorParcela(valorParcela);
		consultarMargem.setSenhaServidor(objectFactory.createConsultarMargemSenhaServidor(senhaServidor));
		consultarMargem.setTokenAutServidor("");
		consultarMargem.setLoginServidor(matricula);
		consultarMargem.setCodVerba(objectFactory.createConsultarMargemCodVerba(codVerba));
		consultarMargem.setServicoCodigo(objectFactory.createConsultarMargemServicoCodigo(codServico));
		consultarMargem.setMatriculaMultipla(objectFactory.createConsultarMargemMatriculaMultipla(null));

		return (ConsultarMargemResponse) getWebServiceTemplate().marshalSendAndReceive(consultarMargem);
	}

	
}
