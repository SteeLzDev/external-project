package com.zetra.econsig.enomina.soap.client;

import java.io.File;
import java.io.IOException;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.enomina.soap.utils.Base64Utils;
import com.zetra.econsig.soap.Anexo;
import com.zetra.econsig.soap.IncluirAnexoConsignacao;
import com.zetra.econsig.soap.IncluirAnexoConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class IncluirAnexoConsignacaoClient extends WebServiceGatewaySupport {

	public IncluirAnexoConsignacaoResponse getResponse(String usuario, String senha, String adeNumero,
			String adeIdentificador, String nomeArquivo, String arquivo, String periodo) throws IOException {

		Anexo anexo = new Anexo();
		anexo.setNomeArquivo(nomeArquivo);
		anexo.setArquivo(Base64Utils.encodeFileToBase64Binary(new File(arquivo)));

		ObjectFactory objectFactory = new ObjectFactory();

		IncluirAnexoConsignacao incluirAnexoConsignacao = new IncluirAnexoConsignacao();
		incluirAnexoConsignacao.setCliente(objectFactory.createIncluirAnexoConsignacaoCliente(""));
		incluirAnexoConsignacao.setConvenio(objectFactory.createIncluirAnexoConsignacaoConvenio("cnv_213464140_001_17167412007983"));
		incluirAnexoConsignacao.setUsuario(usuario);
		incluirAnexoConsignacao.setSenha(senha);
		incluirAnexoConsignacao.setAdeNumero(objectFactory.createIncluirAnexoConsignacaoAdeNumero(adeNumero));
		incluirAnexoConsignacao.setAdeIdentificador(objectFactory.createIncluirAnexoConsignacaoAdeIdentificador(adeIdentificador));
		incluirAnexoConsignacao.setAnexo(anexo);
		incluirAnexoConsignacao.setDescricaoAnexo(objectFactory.createIncluirAnexoConsignacaoDescricaoAnexo("Anexo Automacao"));
		incluirAnexoConsignacao.setPeriodo(objectFactory.createIncluirAnexoConsignacaoPeriodo(periodo));

		return (IncluirAnexoConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(incluirAnexoConsignacao);
	}	
}
