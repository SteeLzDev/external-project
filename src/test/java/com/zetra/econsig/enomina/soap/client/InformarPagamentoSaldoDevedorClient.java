package com.zetra.econsig.enomina.soap.client;

import java.io.File;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.enomina.soap.utils.Base64Utils;
import com.zetra.econsig.soap.Anexo;
import com.zetra.econsig.soap.compra.InformarPagamentoSaldoDevedor;
import com.zetra.econsig.soap.compra.InformarPagamentoSaldoDevedorResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class InformarPagamentoSaldoDevedorClient extends WebServiceGatewaySupport {

	public InformarPagamentoSaldoDevedorResponse getResponse(String usuario, String senha, Long adeNumero,
			String observacao, String nomeArquivo, String arquivo) throws Exception {

		Anexo anexo = new Anexo();
		anexo.setNomeArquivo(nomeArquivo);
		anexo.setArquivo(Base64Utils.encodeFileToBase64Binary(new File(arquivo)));

		ObjectFactory objectFactory = new ObjectFactory();

		InformarPagamentoSaldoDevedor cancelarCompra = new InformarPagamentoSaldoDevedor();
		cancelarCompra.setCliente(objectFactory.createInformarPagamentoSaldoDevedorCliente(""));
		cancelarCompra.setConvenio(
				objectFactory.createInformarPagamentoSaldoDevedorConvenio("cnv_213464140_001_17167412007983"));
		cancelarCompra.setUsuario(usuario);
		cancelarCompra.setSenha(senha);
		cancelarCompra.setAdeNumero(adeNumero);
		cancelarCompra.setObservacao(objectFactory.createInformarPagamentoSaldoDevedorObservacao(observacao));
		cancelarCompra.setAnexo(objectFactory.createInformarPagamentoSaldoDevedorAnexo(anexo));

		return (InformarPagamentoSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(cancelarCompra);
	}

	public InformarPagamentoSaldoDevedorResponse getResponse(String usuario, String senha, Long adeNumero) {

		ObjectFactory objectFactory = new ObjectFactory();

		InformarPagamentoSaldoDevedor cancelarCompra = new InformarPagamentoSaldoDevedor();
		cancelarCompra.setCliente(objectFactory.createInformarPagamentoSaldoDevedorCliente(""));
		cancelarCompra.setConvenio(objectFactory.createInformarPagamentoSaldoDevedorConvenio(""));
		cancelarCompra.setUsuario(usuario);
		cancelarCompra.setSenha(senha);
		cancelarCompra.setAdeNumero(adeNumero);
		cancelarCompra.setObservacao(objectFactory.createInformarPagamentoSaldoDevedorObservacao(""));

		return (InformarPagamentoSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(cancelarCompra);
	}
}
