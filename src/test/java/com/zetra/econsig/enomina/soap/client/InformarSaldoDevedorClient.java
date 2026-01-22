package com.zetra.econsig.enomina.soap.client;

import java.io.File;
import java.io.IOException;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.enomina.soap.utils.Base64Utils;
import com.zetra.econsig.soap.Anexo;
import com.zetra.econsig.soap.compra.InformarSaldoDevedor;
import com.zetra.econsig.soap.compra.InformarSaldoDevedorResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class InformarSaldoDevedorClient extends WebServiceGatewaySupport {

	public InformarSaldoDevedorResponse getResponse(String usuario, String senha, Long adeNumero, String saldoDevedor,
			Long numeroContrato, String nomeArquivoBoletoDsdSaldo, String arquivoBoletoDsdSaldo,
			String nomeArquivoDsdSaldoCompra, String arquivoDsdSaldoCompra, String banco, String agencia, String conta,
			String nomeFavorecido, String cnpj) throws IOException {

		final Anexo anexoBoletoDsdSaldo = new Anexo();
		anexoBoletoDsdSaldo.setNomeArquivo(nomeArquivoBoletoDsdSaldo);
		anexoBoletoDsdSaldo.setArquivo(Base64Utils.encodeFileToBase64Binary(new File(arquivoBoletoDsdSaldo)));

		final Anexo anexoDsdSaldoCompra = new Anexo();
		anexoDsdSaldoCompra.setNomeArquivo(nomeArquivoDsdSaldoCompra);
		anexoDsdSaldoCompra.setArquivo(Base64Utils.encodeFileToBase64Binary(new File(arquivoDsdSaldoCompra)));

		final ObjectFactory objectFactory = new ObjectFactory();

		final InformarSaldoDevedor informarSaldoDevedor = new InformarSaldoDevedor();
		informarSaldoDevedor.setCliente(objectFactory.createComprarContratoCliente(""));
		informarSaldoDevedor.setConvenio(objectFactory.createComprarContratoConvenio("LOCAL"));
		informarSaldoDevedor.setUsuario(usuario);
		informarSaldoDevedor.setSenha(senha);
		informarSaldoDevedor.setAdeNumero(adeNumero);
		informarSaldoDevedor.setValorSaldoDevedor(saldoDevedor);
		informarSaldoDevedor.setDataVencimento(objectFactory.createInformarSaldoDevedorDataVencimento("2019-12-20"));
		informarSaldoDevedor.setValorSaldoDevedor2(objectFactory.createInformarSaldoDevedorValorSaldoDevedor2(""));
		informarSaldoDevedor.setDataVencimento2(objectFactory.createInformarSaldoDevedorDataVencimento2(""));
		informarSaldoDevedor.setValorSaldoDevedor3(objectFactory.createInformarSaldoDevedorValorSaldoDevedor3(""));
		informarSaldoDevedor.setDataVencimento3(objectFactory.createInformarSaldoDevedorDataVencimento3(""));
		informarSaldoDevedor.setNumeroPrestacoes(objectFactory.createInformarSaldoDevedorNumeroPrestacoes("0"));
		informarSaldoDevedor.setBanco(objectFactory.createInformarSaldoDevedorBanco(banco));
		informarSaldoDevedor.setAgencia(objectFactory.createInformarSaldoDevedorAgencia(agencia));
		informarSaldoDevedor.setConta(objectFactory.createInformarSaldoDevedorConta(conta));
		informarSaldoDevedor.setNomeFavorecido(objectFactory.createInformarSaldoDevedorNomeFavorecido(nomeFavorecido));
		informarSaldoDevedor.setCnpjFavorecido(objectFactory.createInformarSaldoDevedorCnpjFavorecido(cnpj));
		informarSaldoDevedor.setNumeroContrato(objectFactory.createInformarSaldoDevedorNumeroContrato(numeroContrato));
		informarSaldoDevedor.setLinkBoleto(objectFactory.createInformarSaldoDevedorLinkBoleto(""));
		informarSaldoDevedor.setObservacao(objectFactory.createInformarSaldoDevedorObservacao("Automação"));
		informarSaldoDevedor.setDetalheSaldoDevedor(objectFactory.createInformarSaldoDevedorDetalheSaldoDevedor("Informar Saldo devedor"));
		informarSaldoDevedor.setAnexoBoletoDsdSaldo(objectFactory.createInformarSaldoDevedorAnexoBoletoDsdSaldo(anexoBoletoDsdSaldo));
		informarSaldoDevedor.setAnexoDsdSaldoCompra(objectFactory.createInformarSaldoDevedorAnexoDsdSaldoCompra(anexoDsdSaldoCompra));

		return (InformarSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(informarSaldoDevedor);
	}

	public InformarSaldoDevedorResponse getResponse(String usuario, String senha, Long adeNumero, String saldoDevedor,
			String saldoDevedor2, String dataVencimento2, String saldoDevedor3, String dataVencimento3) {

		final ObjectFactory objectFactory = new ObjectFactory();

		final InformarSaldoDevedor comprarContrato = new InformarSaldoDevedor();
		comprarContrato.setCliente(objectFactory.createComprarContratoCliente(""));
		comprarContrato.setConvenio(objectFactory.createComprarContratoConvenio("LOCAL"));
		comprarContrato.setUsuario(usuario);
		comprarContrato.setSenha(senha);
		comprarContrato.setAdeNumero(adeNumero);
		comprarContrato.setValorSaldoDevedor(saldoDevedor);
		comprarContrato.setDataVencimento(objectFactory.createInformarSaldoDevedorDataVencimento("2019-08-20"));
		comprarContrato
				.setValorSaldoDevedor2(objectFactory.createInformarSaldoDevedorValorSaldoDevedor2(saldoDevedor2));
		comprarContrato.setDataVencimento2(objectFactory.createInformarSaldoDevedorDataVencimento2(dataVencimento2));
		comprarContrato
				.setValorSaldoDevedor3(objectFactory.createInformarSaldoDevedorValorSaldoDevedor3(saldoDevedor3));
		comprarContrato.setDataVencimento3(objectFactory.createInformarSaldoDevedorDataVencimento3(dataVencimento3));
		comprarContrato.setNumeroPrestacoes(objectFactory.createInformarSaldoDevedorNumeroPrestacoes("0"));
		comprarContrato.setBanco(objectFactory.createInformarSaldoDevedorBanco("1"));
		comprarContrato.setAgencia(objectFactory.createInformarSaldoDevedorAgencia("1234"));
		comprarContrato.setConta(objectFactory.createInformarSaldoDevedorConta("123456"));
		comprarContrato.setNomeFavorecido(objectFactory.createInformarSaldoDevedorNomeFavorecido("Antonio Carlos"));
		comprarContrato.setCnpjFavorecido(objectFactory.createInformarSaldoDevedorCnpjFavorecido("17128840000137"));
		comprarContrato.setLinkBoleto(objectFactory.createInformarSaldoDevedorLinkBoleto(""));
		comprarContrato.setObservacao(objectFactory.createInformarSaldoDevedorObservacao(""));
		comprarContrato.setDetalheSaldoDevedor(objectFactory.createInformarSaldoDevedorDetalheSaldoDevedor(""));

		return (InformarSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(comprarContrato);
	}

	public InformarSaldoDevedorResponse getResponseSemDsdSaldoCompra(String usuario, String senha, Long adeNumero)
			throws IOException {

		final Anexo anexoBoletoDsdSaldo = new Anexo();
		anexoBoletoDsdSaldo.setNomeArquivo("arquivo_boleto.pdf");
		anexoBoletoDsdSaldo.setArquivo(
				Base64Utils.encodeFileToBase64Binary(new File("src/test/resources/files/arquivo_boleto.pdf")));

		final ObjectFactory objectFactory = new ObjectFactory();

		final InformarSaldoDevedor comprarContrato = new InformarSaldoDevedor();
		comprarContrato.setCliente(objectFactory.createComprarContratoCliente(""));
		comprarContrato.setConvenio(objectFactory.createComprarContratoConvenio("LOCAL"));
		comprarContrato.setUsuario(usuario);
		comprarContrato.setSenha(senha);
		comprarContrato.setAdeNumero(adeNumero);
		comprarContrato.setValorSaldoDevedor("50");
		comprarContrato.setDataVencimento(objectFactory.createInformarSaldoDevedorDataVencimento("2019-12-20"));
		comprarContrato.setValorSaldoDevedor2(objectFactory.createInformarSaldoDevedorValorSaldoDevedor2(""));
		comprarContrato.setDataVencimento2(objectFactory.createInformarSaldoDevedorDataVencimento2(""));
		comprarContrato.setValorSaldoDevedor3(objectFactory.createInformarSaldoDevedorValorSaldoDevedor3(""));
		comprarContrato.setDataVencimento3(objectFactory.createInformarSaldoDevedorDataVencimento3(""));
		comprarContrato.setNumeroPrestacoes(objectFactory.createInformarSaldoDevedorNumeroPrestacoes("0"));
		comprarContrato.setBanco(objectFactory.createInformarSaldoDevedorBanco("1"));
		comprarContrato.setAgencia(objectFactory.createInformarSaldoDevedorAgencia("1234"));
		comprarContrato.setConta(objectFactory.createInformarSaldoDevedorConta("123456"));
		comprarContrato.setNomeFavorecido(objectFactory.createInformarSaldoDevedorNomeFavorecido("Antonio"));
		comprarContrato.setCnpjFavorecido(objectFactory.createInformarSaldoDevedorCnpjFavorecido("12345698"));
		comprarContrato.setLinkBoleto(objectFactory.createInformarSaldoDevedorLinkBoleto(""));
		comprarContrato.setObservacao(objectFactory.createInformarSaldoDevedorObservacao("Automação"));
		comprarContrato.setDetalheSaldoDevedor(
				objectFactory.createInformarSaldoDevedorDetalheSaldoDevedor("Informar Saldo devedor"));
		comprarContrato.setAnexoBoletoDsdSaldo(
				objectFactory.createInformarSaldoDevedorAnexoBoletoDsdSaldo(anexoBoletoDsdSaldo));

		return (InformarSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(comprarContrato);
	}

	public InformarSaldoDevedorResponse getResponseSemAnexoBoletoDsdSaldo(String usuario, String senha, Long adeNumero)
			throws IOException {

		final Anexo anexoDsdSaldoCompra = new Anexo();
		anexoDsdSaldoCompra.setNomeArquivo("arquivo_boleto.pdf");
		anexoDsdSaldoCompra.setArquivo(
				Base64Utils.encodeFileToBase64Binary(new File("src/test/resources/files/arquivo_boleto.pdf")));

		final ObjectFactory objectFactory = new ObjectFactory();

		final InformarSaldoDevedor comprarContrato = new InformarSaldoDevedor();
		comprarContrato.setCliente(objectFactory.createComprarContratoCliente(""));
		comprarContrato.setConvenio(objectFactory.createComprarContratoConvenio("LOCAL"));
		comprarContrato.setUsuario(usuario);
		comprarContrato.setSenha(senha);
		comprarContrato.setAdeNumero(adeNumero);
		comprarContrato.setValorSaldoDevedor("50");
		comprarContrato.setDataVencimento(objectFactory.createInformarSaldoDevedorDataVencimento("2019-12-20"));
		comprarContrato.setValorSaldoDevedor2(objectFactory.createInformarSaldoDevedorValorSaldoDevedor2(""));
		comprarContrato.setDataVencimento2(objectFactory.createInformarSaldoDevedorDataVencimento2(""));
		comprarContrato.setValorSaldoDevedor3(objectFactory.createInformarSaldoDevedorValorSaldoDevedor3(""));
		comprarContrato.setDataVencimento3(objectFactory.createInformarSaldoDevedorDataVencimento3(""));
		comprarContrato.setNumeroPrestacoes(objectFactory.createInformarSaldoDevedorNumeroPrestacoes("0"));
		comprarContrato.setBanco(objectFactory.createInformarSaldoDevedorBanco("1"));
		comprarContrato.setAgencia(objectFactory.createInformarSaldoDevedorAgencia("1234"));
		comprarContrato.setConta(objectFactory.createInformarSaldoDevedorConta("123456"));
		comprarContrato.setNomeFavorecido(objectFactory.createInformarSaldoDevedorNomeFavorecido("Antonio"));
		comprarContrato.setCnpjFavorecido(objectFactory.createInformarSaldoDevedorCnpjFavorecido("12345698"));
		comprarContrato.setLinkBoleto(objectFactory.createInformarSaldoDevedorLinkBoleto(""));
		comprarContrato.setObservacao(objectFactory.createInformarSaldoDevedorObservacao("Automação"));
		comprarContrato.setDetalheSaldoDevedor(
				objectFactory.createInformarSaldoDevedorDetalheSaldoDevedor("Informar Saldo devedor"));
		comprarContrato.setAnexoDsdSaldoCompra(
				objectFactory.createInformarSaldoDevedorAnexoDsdSaldoCompra(anexoDsdSaldoCompra));

		return (InformarSaldoDevedorResponse) getWebServiceTemplate().marshalSendAndReceive(comprarContrato);
	}
}
