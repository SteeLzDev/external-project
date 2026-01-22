package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.RetirarContratoDaCompraClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.soap.compra.RetirarContratoDaCompraResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RetirarContratoDaCompraTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private Long adeNumero = (long) 60974;
	private final String codigoMotivo = "01";

	@Autowired
    private RetirarContratoDaCompraClient retirarContratoDaCompraClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Test
	public void retirarContratoDaCompraComRelacionamentoComStatusAguardandoLiquidacao() {
		log.info("Retirar contrato da compra com relacionamento com status aguardando liquidacao");

		// ade com status aguardando liquidacao
		adeNumero = (long) 60976;
		// ade com status aguardando confirmacao
		final String adeNova = "60991";
		final BigDecimal valorAdeNova = autDescontoService.getAde(adeNova).getAdeVlr();

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("000", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertTrue(retirarContratoDaCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para deferida
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
		// verificar valor ade_vlr foi alterado
		assertTrue(valorAdeNova.compareTo(autDescontoService.getAde(adeNova).getAdeVlr()) >= 1);
		// verifica se excluiu o status do relacionamento
		assertNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_INFORMACAO,
				autDesconto.getAdeCodigo(),
				"RETIRADO DA NEGOCIAÇÃO DO CONTRATO " + adeNova + ": Automacao. Automacao"));
	}

	@Test
	public void retirarContratoDaCompraComRelacionamentoComStatusAguardandoPagamentoSaldoDevedor() {
		log.info("Retirar contrato da compra com relacionamento com status aguardando pagamento saldo devedor");

		// ade com status aguardando pagamento saldo devedor
		adeNumero = (long) 60978;
		// ade com status aguardando confirmacao
		final String adeNova = "60987";
		// verificar valor ade_vlr antes
		assertEquals("180.00", autDescontoService.getAde(adeNova).getAdeVlr().toString());

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("000", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertTrue(retirarContratoDaCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para deferida
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
		// verificar valor ade_vlr foi alterado
		assertEquals("80.00", autDescontoService.getAde(adeNova).getAdeVlr().toString());
		// verifica se excluiu o status do relacionamento
		assertNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_INFORMACAO,
				autDesconto.getAdeCodigo(),
				"RETIRADO DA NEGOCIAÇÃO DO CONTRATO " + adeNova + ": Automacao. Automacao"));
	}

	@Test
	public void retirarContratoDaCompraComRelacionamentoComStatusAguardandoAprovacaoSaldoDevedor() {
		log.info("Retirar contrato da compra com relacionamento com status aguardando aprovacao saldo devedor");

		// ade com status aguardando aprovacao saldo devedor
		adeNumero = (long) 60975;
		// ade com status aguardando confirmacao
		final String adeNova = "60991";
		final BigDecimal valorAdeNova = autDescontoService.getAde(adeNova).getAdeVlr();

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("000", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertTrue(retirarContratoDaCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para deferida
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
		// verificar valor ade_vlr foi alterado
		assertTrue(valorAdeNova.compareTo(autDescontoService.getAde(adeNova).getAdeVlr()) >= 1);
		// verifica se excluiu o status do relacionamento
		assertNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_INFORMACAO,
				autDesconto.getAdeCodigo(),
				"RETIRADO DA NEGOCIAÇÃO DO CONTRATO " + adeNova + ": Automacao. Automacao"));
	}

	@Test
	public void retirarContratoDaCompraComRelacionamentoComStatusAguardandoSaldoDevedor() {
		log.info("Retirar contrato da compra com relacionamento com status aguardando saldo devedor");

		// ade com status aguardando aguardando saldo devedor
		adeNumero = (long) 60980;
		// ade com status aguardando confirmacao
		final String adeNova = "60988";
		// verificar valor ade_vlr antes
		assertEquals("200.00", autDescontoService.getAde(adeNova).getAdeVlr().toString());

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("000", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertTrue(retirarContratoDaCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para deferida
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
		// verificar valor ade_vlr foi alterado
		assertEquals("90.00", autDescontoService.getAde(adeNova).getAdeVlr().toString());
		// verifica se excluiu o status do relacionamento
		assertNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_INFORMACAO,
				autDesconto.getAdeCodigo(),
				"RETIRADO DA NEGOCIAÇÃO DO CONTRATO " + adeNova + ": Automacao. Automacao"));
	}

	@Test
	public void retirarContratoDaCompraComConsignacaoComParcelasJaPagas() {
		log.info("Retirar contrato da compra com consignacao com parcelas ja pagas");

		// ade com parcelas pagas
		adeNumero = (long) 60993;
		// ade com status aguardando confirmacao
		final String adeNova = "60994";
		// verificar valor ade_vlr antes
		assertEquals("190.00", autDescontoService.getAde(adeNova).getAdeVlr().toString());

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("000", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertTrue(retirarContratoDaCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para deferida
		assertEquals(CodedValues.SAD_EMANDAMENTO, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
		// verificar valor ade_vlr foi alterado
		assertEquals("100.00", autDescontoService.getAde(adeNova).getAdeVlr().toString());
		// verifica se excluiu o status do relacionamento
		assertNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_INFORMACAO,
				autDesconto.getAdeCodigo(),
				"RETIRADO DA NEGOCIAÇÃO DO CONTRATO " + adeNova + ": Automacao. Automacao"));
	}

	@Test
	public void tentarRetirarContratoDaCompraComRelacionamentoComStatusCancelado() {
		log.info("Tentar retirar contrato da compra com relacionamento com status cancelado");

		// ade com status cancelado
		adeNumero = (long) 60982;
		// ade com status aguardando confirmacao
		final String adeNova = "60989";

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("294", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_CANCELADA, autDescontoService.getAde(adeNova).getSadCodigo());
		// verifica se excluiu o status do relacionamento
		assertEquals(CodedValues.STC_CANCELADO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraComRelacionamentoComStatusFinalizado() {
		log.info("Tentar retirar contrato da compra com relacionamento com status finalizado");

		// ade com status finalizado
		adeNumero = (long) 60984;
		// ade com status aguardando confirmacao
		final String adeNova = "60990";

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("294", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_DEFER, autDescontoService.getAde(adeNova).getSadCodigo());
		// verifica se excluiu o status do relacionamento
		assertEquals(CodedValues.STC_FINALIZADO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());

	}

	@Test
	public void tentarRetirarContratoDaCompraComValorContratoMaiorCompra() {
		log.info("Tentar retirar contrato da compra com valor contrato maior compra");

		adeNumero = (long) 60992;
		// ade com status aguardando confirmacao
		final String adeNova = "60994";

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals(
				"Não é possível realizar a operação. O valor do contrato é maior ou igual ao valor do contrato criado pela portabilidade.",
				retirarContratoDaCompraResponse.getMensagem());
		assertEquals(
				"Não é possível realizar a operação. O valor do contrato é maior ou igual ao valor do contrato criado pela portabilidade.",
				retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDesconto.getSadCodigo());
		// ade nova não alterou o status
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraParaAdeComMaisDeUmRelacionamento() {
		log.info("Tentar retirar contrato da compra para ade com mais de um relacionamento");

		// ade com dois relacionamentos
		adeNumero = (long) 60962;

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("MAIS DE UM RELACIONAMENTO DE PORTABILIDADE PARA A CONSIGNAÇÃO ENCONTRADA.",
				retirarContratoDaCompraResponse.getMensagem());
		assertEquals("423", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());
	}

	@Test
	public void tentarRetirarContratoDaCompraComUmContratoPortabilidade() {
		log.info("Tentar retirar contrato da compra com um contrato portabilidade");

		adeNumero = (long) 60960;

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação permitida apenas quando no mínimo dois contratos participam da portabilidade.",
				retirarContratoDaCompraResponse.getMensagem());
		assertEquals("461", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertNotNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
	}

	@Test
	public void tentarRetirarContratoDaCompraComMotivoDaOperacaoNaoExistente() {
		log.info("Tentar retirar contrato da compra com motivo da operação inválido");

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "001");

		assertEquals("Tipo de motivo da operação não encontrado.", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("356", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertNotNull(
				relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero));
	}

	@Test
	public void tentarRetirarContratoDaCompraComStatusSuspensa() {
		log.info("Tentar retirar contrato da compra com status Suspensa");

		// ade com status Suspensa
		adeNumero = (long) 32;

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("294", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_SUSPENSA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraDeConsignacaoNaoExistente() {
		log.info("Tentar retirar contrato da compra de consignação não existente");

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 60800, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("294", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());
	}

	@Test
	public void tentarRetirarContratoDaCompraSemInformarAdeNumero() {
		log.info("Tentar retirar contrato da compra sem informar ade numero");

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 0, codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				retirarContratoDaCompraResponse.getMensagem());
		assertEquals("322", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());
	}

	@Test
	public void tentarRetirarContratoDaCompraComUsuarioInvalido() {
		log.info("Tentar retirar contrato da compra com usuário inválido");

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse("csa1", loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("358", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraComSenhaInvalida() {
		log.info("Tentar retirar contrato da compra com senha inválida");

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa2.getLogin(), "ser12345", adeNumero, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("358", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraComIPDeAcessoInvalido() {
		log.info("Tentar retirar contrato da compra com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("IP de acesso inválido", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("362", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraSemInformarUsuario() {
		log.info("Tentar retirar contrato da compra sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            retirarContratoDaCompraClient.getResponse("", loginCsa2.getSenha(), adeNumero, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraSemInformarSenha() {
		log.info("Tentar retirar contrato da compra sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            retirarContratoDaCompraClient.getResponse(loginCsa2.getLogin(), "", adeNumero, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRetirarContratoDaCompraComUsuarioSemPermissao() {
		log.info("Tentar retirar contrato da compra com usuário sem permissão");

		final RetirarContratoDaCompraResponse retirarContratoDaCompraResponse = retirarContratoDaCompraClient.getResponse("cse", "cse12345", adeNumero, codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação", retirarContratoDaCompraResponse.getMensagem());
		assertEquals("329", retirarContratoDaCompraResponse.getCodRetorno().getValue());
		assertFalse(retirarContratoDaCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}
}
