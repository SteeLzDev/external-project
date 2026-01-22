package com.zetra.econsig.enomina.soap.client;

import java.io.File;
import java.io.IOException;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.enomina.soap.utils.Base64Utils;
import com.zetra.econsig.soap.Anexo;
import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.ReservarMargem;
import com.zetra.econsig.soap.ReservarMargemResponse;

public class ReservarMargemClient extends WebServiceGatewaySupport {

	public ReservarMargemResponse getResponse(String usuario, String senha, String matricula, String cpf, String codOrgao, String codEstabelecimento,
			String senhaServidor, String loginServidor, String codServico, String valorParcela, Integer prazo, String valorLiberado,
			String codVerba, String nomeArquivo, String arquivo) throws IOException {

		final ObjectFactory objectFactory = new ObjectFactory();
		final Anexo anexo = new Anexo();
		anexo.setNomeArquivo(nomeArquivo);
		anexo.setArquivo(Base64Utils.encodeFileToBase64Binary(new File(arquivo)));

		final ReservarMargem reservarMargem = new ReservarMargem();
		reservarMargem.setCliente(objectFactory.createReservarMargemCliente(""));
		reservarMargem.setConvenio(objectFactory.createReservarMargemConvenio("LOCAL"));
		reservarMargem.setUsuario(usuario);
		reservarMargem.setSenha(senha);
		reservarMargem.setMatricula(matricula);
		reservarMargem.setCpf(objectFactory.createReservarMargemCpf(cpf));
		reservarMargem.setOrgaoCodigo(objectFactory.createReservarMargemOrgaoCodigo(codOrgao));
		reservarMargem.setEstabelecimentoCodigo(objectFactory.createReservarMargemEstabelecimentoCodigo(codEstabelecimento));
		reservarMargem.setSenhaServidor(senhaServidor);
		reservarMargem.setLoginServidor(loginServidor);
		reservarMargem.setTokenAutServidor("");
		reservarMargem.setServicoCodigo(objectFactory.createReservarMargemServicoCodigo(codServico));
		reservarMargem.setValorParcela(valorParcela);
		reservarMargem.setPrazo(objectFactory.createReservarMargemPrazo(prazo));
		reservarMargem.setValorLiberado(objectFactory.createReservarMargemValorLiberado((double) 500));
		reservarMargem.setCodVerba(objectFactory.createReservarMargemCodVerba(codVerba));
		reservarMargem.setAnexo(anexo);

		return (ReservarMargemResponse) getWebServiceTemplate().marshalSendAndReceive(reservarMargem);
	}

}
