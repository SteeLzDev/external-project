package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.AcompanharCompraContratoClient;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.soap.compra.AcompanharCompraContratoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AcompanharCompraContratoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final String codConsignataria = "17167412007983";
	private final String cpf = "092.459.399-79";
	private final String nomeConsignataria = "BANCO TREINAMENTO";
	private final String nomeServidor = "Sr. BOB da Silva Shawn";
	private Long adeNumero = (long) 60960;
	private final boolean contratosCompradosPelaEntidade = true;
	private final boolean saldoDevedorInformado = true;
	private final Short diasUteisSemInfoSaldoDevedor = 0;
	private final boolean saldoDevedorAprovado = true;
	private final Short diasUteisSemAprovacaoSaldoDevedor = 0;
	private final boolean saldoDevedorPago = true;
	private final Short diasUteisSemPagamentoSaldoDevedor = 0;
	private final boolean contratoLiquidado = true;
	private final Short diasUteisSemLiquidacao = 0;
	private final String dataFimCompra = "2021-06-21";

    @Autowired
	private AcompanharCompraContratoClient acompanharCompraContratoClient;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@BeforeEach
	public void setUp() {
		// alterar parametro consignataria
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();
	}

	@Test
	public void acompanharCompraContratoPorAdeNumero() {
		log.info("Acompanhar compra contrato por ade numero");

		// ade aguardando informacao saldo devedor
		adeNumero = (long) 60977;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, "", "", dataFimCompra);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-21"));
		assertEquals("Aguardando Informação Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void tentarAcompanharCompraContratoPorAdeNumeroCancelado() {
		log.info("Tentar acompanhar compra contrato por ade numero cancelado");

		// ade cancelado
		adeNumero = (long) 60982;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa2.getSenha(), adeNumero, loginServidor.getLogin(), cpf, dataFimCompra);

		assertEquals("Nenhuma consignação encontrada.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("294", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void acompanharCompraContratoPorAdeNumeroAguardandoInformacaoSaldoDevedor() {
		log.info("Acompanhar compra contrato por ade numero aguardando informação saldo devedor");

		// ade aguardando informacao saldo devedor
		adeNumero = (long) 60977;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false, false,
				diasUteisSemInfoSaldoDevedor, false, diasUteisSemAprovacaoSaldoDevedor, false,
				diasUteisSemPagamentoSaldoDevedor, false, diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-21"));
		assertEquals("Aguardando Informação Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorContratosCompradosPelaEntidade() {
		log.info("Acompanhar compra contrato por contratos comprados pela entidade");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), loginServidor.getLogin(), cpf,
				contratosCompradosPelaEntidade, saldoDevedorInformado, saldoDevedorAprovado, saldoDevedorPago,
				contratoLiquidado);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().size() >= 1);
	}

	@Test
	public void acompanharCompraContratoPorContratoFinalizado() {
		log.info("Acompanhar compra contrato por contrato finalizado");

		// ade liquidada
		adeNumero = (long) 60985;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor,
				contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-21"));
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataInfoSaldoDevedor().getValue()
				.toString().contains("2021-06-21"));
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataAprovacaoSaldoDevedor().getValue()
				.toString().contains("2021-06-21"));
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataPagamentoSaldoDevedor().getValue()
				.toString().contains("2021-06-21"));
		assertEquals("Finalizado",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorContratoAguardandoPagamentoSaldoDevedor() {
		log.info("Acompanhar compra contrato por contrato Aguardando pagamento saldo devedor");

		// ade aguardando pagamento saldo devedor
		adeNumero = (long) 60938;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, false, diasUteisSemPagamentoSaldoDevedor, false,
				diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertEquals("Aguardando Pagamento Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorContratoAguardandoLiquidacao() {
		log.info("Acompanhar compra contrato por contrato aguardando liquidacao");

		// ade aguardando liquidacao
		adeNumero = (long) 60960;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor, false,
				diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-17"));
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataInfoSaldoDevedor().getValue()
				.toString().contains("2021-06-17"));
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataAprovacaoSaldoDevedor().getValue()
				.toString().contains("2021-06-17"));
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataPagamentoSaldoDevedor().getValue()
				.toString().contains("2021-06-17"));
		assertEquals("Aguardando Liquidação",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorContratoAguardandoAprovacaoSaldoDevedor() {
		log.info("Acompanhar compra contrato por contrato aguardando aprovacao saldo devedor");

		// ade aguardando aprovacao
		adeNumero = (long) 60995;

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, false, diasUteisSemAprovacaoSaldoDevedor, false,
				diasUteisSemPagamentoSaldoDevedor, false, diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertEquals("Aguardando Aprovação Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorMatricula() {
		log.info("Acompanhar compra contrato por matricula");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), loginServidor.getLogin(), "", dataFimCompra);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().size() >= 1);
	}

	@Test
	public void acompanharCompraContratoPorCPF() {
		log.info("Acompanhar compra contrato por CPF");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), "", cpf, dataFimCompra);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().size() >= 1);
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemInformacaoSaldoDevedor() {
		log.info("Acompanhar compra contrato por dias uteis sem informacao saldo devedor");

		adeNumero = (long) 60915;

		// alterar a data compra
		final Timestamp dataCompra = new Timestamp(DateHelper.getDate(2021, 06, 10, 11, 11).getTime());
		relacionamentoAutorizacaoService.alterarDataCompra(adeNumero, dataCompra);

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false, false,
				Short.valueOf("10"), false, diasUteisSemAprovacaoSaldoDevedor, false, diasUteisSemPagamentoSaldoDevedor,
				false, diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertEquals("Aguardando Informação Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemInformacaoSaldoDevedorSemResultado() {
		log.info("Acompanhar compra contrato por dias uteis sem info saldo devedor");

		adeNumero = (long) 60915;

		// alterar a data compra
		relacionamentoAutorizacaoService.alterarDataCompra(adeNumero,
				new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -30).getTime()));

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false, false,
				Short.valueOf("50"), false, diasUteisSemAprovacaoSaldoDevedor, false, diasUteisSemPagamentoSaldoDevedor,
				false, diasUteisSemLiquidacao);

		assertEquals("Nenhuma consignação encontrada.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("294", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemAprovacaoSaldoDevedor() {
		log.info("Acompanhar compra contrato por dias uteis sem aprovacao saldo devedor");

		adeNumero = (long) 60995;

		// alterar a data informacao saldo
		final Timestamp dataInfoSaldo = new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -30).getTime());
		relacionamentoAutorizacaoService.alterarDataInfoSaldo(adeNumero, dataInfoSaldo);

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, false, Short.valueOf("5"), false,
				diasUteisSemPagamentoSaldoDevedor, false, diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-22"));
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertEquals("Aguardando Aprovação Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemAprovacaoSaldoDevedorSemResultado() {
		log.info("Acompanhar compra contrato por dias uteis sem aprovacao saldo devedor");

		adeNumero = (long) 60995;

		// alterar a data informacao saldo
		relacionamentoAutorizacaoService.alterarDataInfoSaldo(adeNumero,
				new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -30).getTime()));

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado, Short.valueOf("50"), false,
				diasUteisSemPagamentoSaldoDevedor, false, diasUteisSemLiquidacao);

		assertEquals("Nenhuma consignação encontrada.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("294", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemPagamentoSaldoDevedor() {
		log.info("Acompanhar compra contrato por dias uteis sem pagamento saldo devedor");

		adeNumero = (long) 60938;

		// alterar a data aprovacao saldo
		final Timestamp dataAprovacaoSaldo = new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -30).getTime());
		relacionamentoAutorizacaoService.alterarDataAprovacaoSaldo(adeNumero, dataAprovacaoSaldo);

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, false, Short.valueOf("5"), false, diasUteisSemLiquidacao);

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-16"));
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataInfoSaldoDevedor().getValue()
				.toString().contains("2021-06-16"));
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertEquals("Aguardando Pagamento Saldo Devedor",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemPagamentoSaldoDevedorSemResultado() {
		log.info("Acompanhar compra contrato por dias uteis sem pagamento saldo devedor");

		adeNumero = (long) 60938;

		// alterar data aprovacao saldo
		relacionamentoAutorizacaoService.alterarDataAprovacaoSaldo(adeNumero,
				new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -20).getTime()));

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, false, Short.valueOf("30"), false, diasUteisSemLiquidacao);

		assertEquals("Nenhuma consignação encontrada.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("294", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemLiquidacao() {
		log.info("Acompanhar compra contrato por dias uteis sem liquidacao");

		adeNumero = (long) 60974;

		// alterar a data aprovacao saldo
		final Timestamp dataPagamentoSaldo = new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -30).getTime());
		relacionamentoAutorizacaoService.alterarDataPagamentoSaldo(adeNumero, dataPagamentoSaldo);

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor, false,
				Short.valueOf("10"));

		assertEquals("Operação realizada com sucesso.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("000", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getAdeNumero().getValue().toString());
		assertEquals(codConsignataria, acompanharCompraContratoResponse.getInfoCompras().get(0).getCodigoConsignataria()
				.getValue().toString());
		assertEquals(nomeConsignataria,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeConsignataria().getValue().toString());
		assertEquals(nomeServidor,
				acompanharCompraContratoResponse.getInfoCompras().get(0).getNomeServidor().getValue().toString());
		assertEquals(loginServidor.getLogin(),
				acompanharCompraContratoResponse.getInfoCompras().get(0).getMatricula().getValue().toString());
		assertEquals(cpf, acompanharCompraContratoResponse.getInfoCompras().get(0).getCpf().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataCompra().getValue().toString()
				.contains("2021-06-21"));
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataInfoSaldoDevedor().getValue()
				.toString().contains("2021-06-21"));
		assertEquals("90.0",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getValorSaldoDevedor().getValue().toString());
		assertTrue(acompanharCompraContratoResponse.getInfoCompras().get(0).getDataAprovacaoSaldoDevedor().getValue()
				.toString().contains("2021-06-21"));
		assertEquals("Aguardando Liquidação",
				acompanharCompraContratoResponse.getInfoCompras().get(0).getSituacao().getValue().toString());
	}

	@Test
	public void acompanharCompraContratoPorDiasUteisSemLiquidacaoSemResultado() {
		log.info("Acompanhar compra contrato por dias uteis sem liquidacao");

		adeNumero = (long) 60974;

		// alterar a data compra
		relacionamentoAutorizacaoService.alterarDataPagamentoSaldo(adeNumero,
				new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -30).getTime()));

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf, false,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor, false,
				Short.valueOf("50"));

		assertEquals("Nenhuma consignação encontrada.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("294", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoComPeriodoMaiorDoQueTrintaDias() {
		log.info("Tentar acompanhar compra contrato com periodo maior do que trinta dias");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), (long) 0, loginServidor.getLogin(), cpf,
				"2021-07-22");

		assertEquals("O PERIODO INFORMADO PARA DATA DE PORTABILIDADE NAO PODE SER MAIOR DO QUE 30 DIAS.",
				acompanharCompraContratoResponse.getMensagem());
		assertEquals("424", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertFalse(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoComIPDeAcessoInvalido() {
		log.info("Tentar acompanhar compra contrato com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, loginServidor.getLogin(), cpf,
				contratosCompradosPelaEntidade, saldoDevedorInformado, diasUteisSemInfoSaldoDevedor,
				saldoDevedorAprovado, diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago,
				diasUteisSemPagamentoSaldoDevedor, contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("IP de acesso inválido", acompanharCompraContratoResponse.getMensagem());
		assertEquals("362", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertFalse(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoComAdeInexistente() {
		log.info("Tentar acompanhar compra contrato com ade inexistente");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 60800, loginServidor.getLogin(), cpf,
				contratosCompradosPelaEntidade, saldoDevedorInformado, diasUteisSemInfoSaldoDevedor,
				saldoDevedorAprovado, diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago,
				diasUteisSemPagamentoSaldoDevedor, contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("Nenhuma consignação encontrada.", acompanharCompraContratoResponse.getMensagem());
		assertEquals("294", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertTrue(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoComUsuarioInvalido() {
		log.info("Tentar acompanhar compra contrato com usuário inválido");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				"csa1", loginCsa2.getSenha(), adeNumero, loginServidor.getLogin(), cpf, contratosCompradosPelaEntidade,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor,
				contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("Usuário ou senha inválidos", acompanharCompraContratoResponse.getMensagem());
		assertEquals("358", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertFalse(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoComSenhaInvalida() {
		log.info("Tentar acompanhar compra contrato com senha inválida");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa2.getLogin(), "abc1234", adeNumero, loginServidor.getLogin(), cpf,
				contratosCompradosPelaEntidade, saldoDevedorInformado, diasUteisSemInfoSaldoDevedor,
				saldoDevedorAprovado, diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago,
				diasUteisSemPagamentoSaldoDevedor, contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("Usuário ou senha inválidos", acompanharCompraContratoResponse.getMensagem());
		assertEquals("358", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertFalse(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoComUsuarioSemPermissao() {
		log.info("Tentar acompanhar compra contrato com usuário sem permissão");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				"cse", "cse12345", adeNumero, loginServidor.getLogin(), cpf, contratosCompradosPelaEntidade,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor,
				contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("O usuário não tem permissão para executar esta operação",
				acompanharCompraContratoResponse.getMensagem());
		assertEquals("329", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertFalse(acompanharCompraContratoResponse.isSucesso());
	}

	@Test
	public void tentarAcompanharCompraContratoParaServidorNaoCadastrado() {
		log.info("Tentar acompanhar compra contrato para servidor não cadastrado");

		final AcompanharCompraContratoResponse acompanharCompraContratoResponse = acompanharCompraContratoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "125474", cpf, contratosCompradosPelaEntidade,
				saldoDevedorInformado, diasUteisSemInfoSaldoDevedor, saldoDevedorAprovado,
				diasUteisSemAprovacaoSaldoDevedor, saldoDevedorPago, diasUteisSemPagamentoSaldoDevedor,
				contratoLiquidado, diasUteisSemLiquidacao);

		assertEquals("Nenhum servidor encontrado", acompanharCompraContratoResponse.getMensagem());
		assertEquals("293", acompanharCompraContratoResponse.getCodRetorno().getValue());
		assertFalse(acompanharCompraContratoResponse.isSucesso());
	}
}