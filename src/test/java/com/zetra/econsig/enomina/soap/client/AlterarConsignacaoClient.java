package com.zetra.econsig.enomina.soap.client;

import java.io.File;
import java.io.IOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.enomina.soap.utils.Base64Utils;
import com.zetra.econsig.soap.AlterarConsignacao;
import com.zetra.econsig.soap.AlterarConsignacaoResponse;
import com.zetra.econsig.soap.Anexo;
import com.zetra.econsig.soap.ObjectFactory;

public class AlterarConsignacaoClient extends WebServiceGatewaySupport {

	public AlterarConsignacaoResponse getResponse(String usuario, String senha, Long adenumero, 
			String valorParcela, String prazo, String nomeArquivo, String arquivo) throws IOException {
		
		ObjectFactory objectFactory = new ObjectFactory();
		Anexo anexo = new Anexo();
		anexo.setNomeArquivo(nomeArquivo);
		anexo.setArquivo(Base64Utils.encodeFileToBase64Binary(new File(arquivo)));
		
		AlterarConsignacao alterarConsignacao = new AlterarConsignacao();
		alterarConsignacao.setUsuario(usuario);
		alterarConsignacao.setSenha(senha);
		alterarConsignacao.setAdeNumero(adenumero);
		alterarConsignacao.setValorParcela(valorParcela);
		alterarConsignacao.setValorLiberado(objectFactory.createAlterarConsignacaoValorLiberado(String.valueOf(500)));
		alterarConsignacao.setPrazo(objectFactory.createAlterarConsignacaoPrazo(prazo));
		alterarConsignacao.setAnexo(anexo);

		return (AlterarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(alterarConsignacao);
	}

	
	public AlterarConsignacaoResponse getResponse(String usuario, String senha, Long adenumero, 
			String valorParcela, String prazo) throws IOException {
		
		ObjectFactory objectFactory = new ObjectFactory();
				
		AlterarConsignacao alterarConsignacao = new AlterarConsignacao();
		alterarConsignacao.setUsuario(usuario);
		alterarConsignacao.setSenha(senha);
		alterarConsignacao.setAdeNumero(adenumero);
		alterarConsignacao.setValorParcela(valorParcela);
		alterarConsignacao.setValorLiberado(objectFactory.createAlterarConsignacaoValorLiberado(String.valueOf(500)));
		alterarConsignacao.setPrazo(objectFactory.createAlterarConsignacaoPrazo(prazo));

		return (AlterarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(alterarConsignacao);
	}

	
}
