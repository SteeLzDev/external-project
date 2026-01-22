package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.InformarSaldoDevedorClient;
import com.zetra.econsig.persistence.entity.SaldoDevedor;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.compra.InformarSaldoDevedorResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class InformarSaldoDevedorTest extends ENominaContextSpringConfiguration {

    private final LoginInfo loginCsa = LoginValues.csa1;
	private Long adeNumero = (long) 60915;
	private final String saldoDevedor = "400";
	private final Long numeroContrato = (long) 8596;
	private final String saldoDevedor2 = "95";
	private final String dataVencimento2 = "2019-12-20";
	private final String saldoDevedor3 = "85";
	private final String dataVencimento3 = "2021-01-20";
	private final String banco = "1";
	private final String agencia = "1234";
	private final String conta = "123456";
	private final String nomeFavorecido = "Antonio Carlos";
	private final String cnpj = "17128840000137";
	private final String nomeArquivoBoleto = "arquivo_boleto.pdf";
	private final String arquivoBoleto = "src/test/resources/files/arquivo_boleto.pdf";

	@Autowired
    private InformarSaldoDevedorClient informarSaldoDevedorClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@BeforeEach
	public void setUp() {
		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa",
				CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
		parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("001"),
				CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR, "0");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA,
				"N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA,
				"N");
		ENominaInitializer.limparCache();
	}

	@Test
	public void informarSaldoDevedorComSucesso() throws Exception {
		log.info("Informar saldo devedor com sucesso");

		adeNumero = (long) 60930;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Operação realizada com sucesso.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarSaldoDevedorResponse.isSucesso());

		final SaldoDevedor tbSaldoDevedor = autDescontoService.getSaldoDevedor(adeNumero.toString());

		// verifica se salvou no banco de dados
		assertEquals(saldoDevedor + ".00", tbSaldoDevedor.getSdvValor().toString());
		assertEquals(agencia, tbSaldoDevedor.getSdvAgencia());
		assertEquals(conta, tbSaldoDevedor.getSdvConta());
		assertEquals(nomeFavorecido, tbSaldoDevedor.getSdvNomeFavorecido());
		assertEquals("17.128.840/0001-37", tbSaldoDevedor.getSdvCnpj());
	}

	@Test
	public void informarSaldoDevedorComPermissaoDeSaldoDevedorForaFaixaLimite() throws Exception {
		log.info("Informar saldo devedor com permissao de saldo devedor fora faixa limite");

		parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("001"),
				CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO, "0");
		ENominaInitializer.limparCache();

		adeNumero = (long) 60930;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "5000", numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Operação realizada com sucesso.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarSaldoDevedorResponse.isSucesso());

		final SaldoDevedor tbSaldoDevedor = autDescontoService.getSaldoDevedor(adeNumero.toString());

		// verifica se salvou no banco de dados
		assertEquals("5000.00", tbSaldoDevedor.getSdvValor().toString());
	}

	@Test
	public void editarSaldoDevedorComSucesso() throws Exception {
		log.info("Editar saldo devedor com sucesso");

		// ade com saldo já informado
		adeNumero = (long) 60931;

		// verifica valor atual no banco antes de alterar
		SaldoDevedor tbSaldoDevedor = autDescontoService.getSaldoDevedor(adeNumero.toString());
		assertEquals("50.00", tbSaldoDevedor.getSdvValor().toString());
		assertEquals("1111", tbSaldoDevedor.getSdvAgencia());
		assertEquals("111111", tbSaldoDevedor.getSdvConta());
		assertEquals("Alexandre", tbSaldoDevedor.getSdvNomeFavorecido());
		assertEquals("11.111.111/1111-11", tbSaldoDevedor.getSdvCnpj());

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "98.50", numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Operação realizada com sucesso.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarSaldoDevedorResponse.isSucesso());

		// verifica que alterou no banco de dados
		tbSaldoDevedor = autDescontoService.getSaldoDevedor(adeNumero.toString());
		assertEquals("98.50", tbSaldoDevedor.getSdvValor().toString());
		assertEquals(agencia, tbSaldoDevedor.getSdvAgencia());
		assertEquals(conta, tbSaldoDevedor.getSdvConta());
		assertEquals(nomeFavorecido, tbSaldoDevedor.getSdvNomeFavorecido());
		assertEquals("17.128.840/0001-37", tbSaldoDevedor.getSdvCnpj());
	}

	@Test
	public void informarSaldoDevedorSemDadosBancarios() throws Exception {
		log.info("Tentar informar saldo devedor sem dados bancarios");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, "1");
		ENominaInitializer.limparCache();

		adeNumero = (long) 60932;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, "", "", "", "", "");

		assertEquals("Operação realizada com sucesso.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarSaldoDevedorResponse.isSucesso());

		final SaldoDevedor tbSaldoDevedor = autDescontoService.getSaldoDevedor(adeNumero.toString());

		// verifica se salvou no banco de dados
		assertEquals(saldoDevedor + ".00", tbSaldoDevedor.getSdvValor().toString());
		assertTrue(tbSaldoDevedor.getSdvAgencia().isEmpty());
		assertTrue(tbSaldoDevedor.getSdvConta().isEmpty());
		assertTrue(tbSaldoDevedor.getSdvNomeFavorecido().isEmpty());
		assertTrue(tbSaldoDevedor.getSdvCnpj().isEmpty());
	}

	@Test
	public void informarSaldoDevedorComMultiplosSaldoDevedores() throws Exception {
		log.info("nformar saldo devedor com multiplos saldo devedores");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		adeNumero = (long) 60932;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, saldoDevedor2, dataVencimento2,
				saldoDevedor3, dataVencimento3);

		assertEquals("Operação realizada com sucesso.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarSaldoDevedorResponse.isSucesso());

		// verifica que alterou no banco de dados
		assertEquals(saldoDevedor + ".00",
				autDescontoService.getSaldoDevedor(adeNumero.toString()).getSdvValor().toString());
	}

	@Test
	public void tentarInformarSaldoDevedorComValoresIguais() throws Exception {
		log.info("nformar saldo devedor com valores iguais");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		adeNumero = (long) 60933;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, "95", dataVencimento2, "95",
				dataVencimento3);

		assertEquals("Os valores de saldo não podem ser iguais.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("Os valores de saldo não podem ser iguais.",
				informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComDataVencimentoIguais() throws Exception {
		log.info("nformar saldo devedor com data vencimento iguais");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		adeNumero = (long) 60933;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, saldoDevedor2, "2019-12-20",
				saldoDevedor3, "2019-12-20");

		assertEquals("As datas de vencimento não podem ser iguais.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("As datas de vencimento não podem ser iguais.",
				informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComCSACompradora() throws IOException {
		log.info("Tentar informar saldo devedor com CSA compradora");

		// ade com pagamento saldo devedor
		adeNumero = (long) 60921;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse("csa2",
				loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto, arquivoBoleto,
				nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Nenhuma consignação encontrada", informarSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));

	}

	@Test
	public void tentarInformarSaldoDevedorComStatusFinalizado() throws IOException {
		log.info("Tentar informar saldo devedor com status finalizado");

		// ade com status finalizado
		adeNumero = (long) 60925;

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Nenhuma consignação encontrada", informarSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

	}

	@Test
	public void tentarInformarSaldoDevedorDeConsignacaoNaoExistente() throws IOException {
		log.info("Tentar informar saldo devedor de consignação não existente");

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), (long) 60800, saldoDevedor, numeroContrato,
				nomeArquivoBoleto, arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta,
				nomeFavorecido, cnpj);

		assertEquals("Nenhuma consignação encontrada", informarSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarAdeNumero() throws IOException {
		log.info("Tentar informar saldo devedor sem informar ade numero");

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, saldoDevedor, numeroContrato,
				nomeArquivoBoleto, arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta,
				nomeFavorecido, cnpj);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("322", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarValorDoSaldoDevedor() throws IOException {
		log.info("Tentar informar saldo devedor sem informar valor do saldo devedor");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            informarSaldoDevedorClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "", numeroContrato, nomeArquivoBoleto,
                                                   arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);
        });

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComValorSaldoDevedorZero() throws IOException {
		log.info("Tentar informar saldo devedor com valor saldo devedor zero");

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "0", numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("O saldo devedor deve ser maior que zero.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("428", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComUsuarioInvalido() throws IOException {
		log.info("Tentar informar saldo devedor com usuário inválido");

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse("csa9",
				loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto, arquivoBoleto,
				nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Usuário ou senha inválidos", informarSaldoDevedorResponse.getMensagem());
		assertEquals("358", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComSenhaInvalida() throws IOException {
		log.info("Tentar informar saldo devedor com senha inválida");

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), "cse1452", adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("Usuário ou senha inválidos", informarSaldoDevedorResponse.getMensagem());
		assertEquals("358", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComIPDeAcessoInvalido() throws IOException {
		log.info("Tentar informar saldo devedor com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("IP de acesso inválido", informarSaldoDevedorResponse.getMensagem());
		assertEquals("362", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarUsuario() throws IOException {
		log.info("Tentar informar saldo devedor sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            informarSaldoDevedorClient.getResponse("", loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto, arquivoBoleto,
                                                   nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);
        });

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarSenha() throws IOException {
		log.info("Tentar informar saldo devedor sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            informarSaldoDevedorClient.getResponse(loginCsa.getLogin(), "", adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto, arquivoBoleto,
                                                   nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);
        });

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComUsuarioSemPermissao() throws IOException {
		log.info("Tentar informar saldo devedor com usuário sem permissão");

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse("cse",
				"cse123456", adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto, arquivoBoleto,
				nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals("O usuário não tem permissão para executar esta operação",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("329", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarAnexoBoletoSaldoDevedorCompra() throws IOException {
		log.info("Tentar informar saldo devedor sem informar anexo boleto saldo devedor compra");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA,
				"S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient
				.getResponseSemAnexoBoletoDsdSaldo(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero);

		assertEquals("O arquivo do boleto bancário deve ser anexado.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("432", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarAnexoDSDSaldoDevedorCompra() throws IOException {
		log.info("Tentar informar saldo devedor sem informar anexo DSD saldo devedor compra");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA,
				"S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient
				.getResponseSemDsdSaldoCompra(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero);

		assertEquals("O arquivo do demonstrativo de cálculo de saldo devedor deve ser anexado.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("433", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComArquivoInvalido() throws IOException {
		log.info("Tentar informar saldo devedor com arquivo invalido");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA,
				"S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, "anexoBoleto.msg",
				arquivoBoleto, "anexoBoleto.msg", arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals(
				"O arquivo anexoBoleto.msg possui extensão não permitida.<br><br>Lista de extensões permitidas: .gif, .jpg, .jpeg, .jpe, .jfif, .jfi, .pdf",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("430", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarNomeArquivo() throws IOException {
		log.info("Tentar informar saldo devedor sem informar nome arquivo");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, "S");
		ENominaInitializer.limparCache();

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            informarSaldoDevedorClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, "", arquivoBoleto,
                                                   "", arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);
        });

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarAgencia() throws Exception {
		log.info("Tentar informar saldo devedor sem informar agencia");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, "2");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, "", conta, nomeFavorecido, cnpj);

		assertEquals("O código da agência para depósito deve ser informado.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("476", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarConta() throws Exception {
		log.info("Tentar informar saldo devedor sem informar conta");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, "2");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, "", nomeFavorecido, cnpj);

		assertEquals("O código da conta para depósito deve ser informado.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("477", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarBanco() throws Exception {
		log.info("Tentar informar saldo devedor sem informar banco");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, "2");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, "", agencia, conta, nomeFavorecido, cnpj);

		assertEquals("O banco para depósito deve ser informado.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("475", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarNomeFavorecido() throws Exception {
		log.info("Tentar informar saldo devedor sem informar nome favorecido");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, "2");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, "", cnpj);

		assertEquals("O nome do favorecido para depósito deve ser informado.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("478", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarCNPJ() throws Exception {
		log.info("Tentar informar saldo devedor sem informar CNPJ");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, "2");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, "");

		assertEquals("O CNPJ para depósito deve ser informado.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("479", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarSaldoDevedor2ParaMultiplosSaldosDevedores() throws Exception {
		log.info("Tentar informar saldo devedor sem informar saldo devedor 2 para multiplos saldos devedores");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, "", dataVencimento2, saldoDevedor3,
				dataVencimento3);

		assertEquals("A data e o valor do segundo saldo devedor devem ser informados para múltiplos saldos devedores.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("397", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarDataVencimento2ParaMultiplosSaldosDevedores() throws Exception {
		log.info("Tentar informar saldo devedor sem informar data vencimento 2 para multiplos saldos devedores");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, saldoDevedor2, "", saldoDevedor3,
				dataVencimento3);

		assertEquals("A data e o valor do segundo saldo devedor devem ser informados para múltiplos saldos devedores.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("397", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarSaldoDevedor3ParaMultiplosSaldosDevedores() throws Exception {
		log.info("Tentar informar saldo devedor sem informar saldo devedor 3 para multiplos saldos devedores");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, saldoDevedor2, dataVencimento2, "",
				dataVencimento3);

		assertEquals("A data e o valor do terceiro saldo devedor devem ser informados para múltiplos saldos devedores.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("398", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarDataVencimento3ParaMultiplosSaldosDevedores() throws Exception {
		log.info("Tentar informar saldo devedor sem informar data vencimento 3 para multiplos saldos devedores");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, "S");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, saldoDevedor2, dataVencimento2,
				saldoDevedor3, "");

		assertEquals("A data e o valor do terceiro saldo devedor devem ser informados para múltiplos saldos devedores.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("398", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorComSaldoMaiorCadastrado() throws Exception {
		log.info("Tentar informar saldo devedor com saldo maior que o cadastrado");

		parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("001"),
				CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO, "1");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "1500", numeroContrato, nomeArquivoBoleto,
				arquivoBoleto, nomeArquivoBoleto, arquivoBoleto, banco, agencia, conta, nomeFavorecido, cnpj);

		assertEquals(
				"O saldo devedor informado não pode ser maior que a faixa limite permitida para esta consignação: R$ 990,00.",
				informarSaldoDevedorResponse.getMensagem());
		assertEquals("427", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}

	@Test
	public void tentarInformarSaldoDevedorSemInformarNumeroContrato() throws Exception {
		log.info("Tentar informar saldo devedor sem informar numero contrato");

		parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("001"),
				CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR, "1");
		ENominaInitializer.limparCache();

		final InformarSaldoDevedorResponse informarSaldoDevedorResponse = informarSaldoDevedorClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, saldoDevedor, saldoDevedor2, dataVencimento2,
				saldoDevedor3, dataVencimento3);

		assertEquals("O número do contrato deve ser informado.", informarSaldoDevedorResponse.getMensagem());
		assertEquals("435", informarSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarSaldoDevedorResponse.isSucesso());

		// verifica que não salvou no banco de dados
		assertNull(autDescontoService.getSaldoDevedor(adeNumero.toString()));
	}
}
