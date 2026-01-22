package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.SolicitarRecalculoSaldoDevedorClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.RelacionamentoServicoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.compra.SolicitarRecalculoSaldoDevedorResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SolicitarRecalculoSaldoDevedorTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private Long adeNumero = (long) 60938;
	private final String observacao = "Automação";
	private String svcCodigoEmprestimo;

	@Autowired
    private SolicitarRecalculoSaldoDevedorClient solicitarRecalculoSaldoDevedorClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Autowired
	private RelacionamentoServicoService relacionamentoServicoService;

	@Autowired
	private ServicoService servicoService;

	@BeforeEach
	public void setUp() {
		svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");

		relacionamentoServicoService.excluirRelacionamentoServico(svcCodigoEmprestimo, svcCodigoEmprestimo,
				CodedValues.TNT_FINANCIAMENTO_DIVIDA);
	}

	@Test
	public void solicitarRecalculoSaldoDevedorComStatusAguardandoPagamentoSaldo() {
		log.info("Solicitar recalculo saldo devedor com status aguardando pagamento saldo");

		// ade com status aguardando pagamento saldo
		adeNumero = (long) 60957;

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("Operação realizada com sucesso.",
				solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("000", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica se alterou o status do relacionamento para aguardando saldo
		assertEquals("1", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar ocorrências para registro de recalculo de saldo
		assertNotNull(
				ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_SALDO_DEVEDOR_RECALCULADO,
						autDescontoService.getAde(adeNumero.toString()).getAdeCodigo()));
	}

	@Test
	public void solicitarRecalculoSaldoDevedorComStatusAguardandoAprovacaoSaldo() {
		log.info("Solicitar recalculo saldo devedor com status aguardando aprovacao saldo");

		// ade com status aguardando aprovacao saldo
		adeNumero = (long) 60956;

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("Operação realizada com sucesso.",
				solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("000", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica se alterou o status do relacionamento para aguardando saldo
		assertEquals("7", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar ocorrências para registro de recalculo de saldo
		assertNotNull(
				ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_SALDO_DEVEDOR_RECALCULADO,
						autDescontoService.getAde(adeNumero.toString()).getAdeCodigo()));
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComServicoComRelacionamentoDeFinanciamentoDeDivida() {
		log.info("Tentar solicitar recalculo saldo devedor com serviço com relacionamento de financiamento de dívida");

		relacionamentoServicoService.incluirRelacionamentoServico(svcCodigoEmprestimo, svcCodigoEmprestimo,
				CodedValues.TNT_FINANCIAMENTO_DIVIDA);

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao);

		assertEquals(
				"As informações do saldo devedor desta consignação não podem ser alteradas, pois o saldo devedor já foi aprovado.",
				solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals(
				"As informações do saldo devedor desta consignação não podem ser alteradas, pois o saldo devedor já foi aprovado.",
				solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorParaAdeComMaisDeUmRelacionamento() {
		log.info("Tentar solicitar recalculo saldo devedor para ade com mais de um relacionamento");

		// ade com dois relacionamentos
		adeNumero = (long) 29;

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("MAIS DE UM RELACIONAMENTO DE PORTABILIDADE PARA A CONSIGNAÇÃO ENCONTRADA.",
				solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("423", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComStatusAguardandoSaldoDevedor() {
		log.info("Tentar solicitar recalculo saldo devedor com status aguardando saldo devedor");

		// ade com status aguardando saldo devedor
		adeNumero = (long) 60915;

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("294", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("1", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComConsignatariaSemPermissao() {
		log.info("Tentar solicitar recalculo saldo devedor com consignataria sem permissao");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse("csa", loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("294", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorDeConsignacaoLiquidada() {
		log.info("Tentar solicitar recalculo saldo devedor de consignação liquidada");

		// ade liquidada
		adeNumero = (long) 60925;

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("294", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorDeConsignacaoNaoExistente() {
		log.info("Tentar solicitar recalculo saldo devedor de consignação não existente");

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 60800, observacao);

		assertEquals("Nenhuma consignação encontrada", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("294", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorSemInformarAdeNumero() {
		log.info("Tentar solicitar recalculo saldo devedor sem informar ade numero");

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, observacao);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("322", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComUsuarioInvalido() {
		log.info("Tentar solicitar recalculo saldo devedor com usuário inválido");

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse("csa1", loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("Usuário ou senha inválidos", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("358", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComSenhaInvalida() {
		log.info("Tentar solicitar recalculo saldo devedor com senha inválida");

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), "ser12345", adeNumero, observacao);

		assertEquals("Usuário ou senha inválidos", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("358", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComIPDeAcessoInvalido() {
		log.info("Tentar solicitar recalculo saldo devedor com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse("csa", loginCsa.getSenha(), adeNumero, observacao);

		assertEquals("IP de acesso inválido", solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("362", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorSemInformarUsuario() {
		log.info("Tentar solicitar recalculo saldo devedor sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            solicitarRecalculoSaldoDevedorClient.getResponse("", loginCsa.getSenha(), adeNumero, observacao);
        });

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorSemInformarSenha() {
		log.info("Tentar solicitar recalculo saldo devedor sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            solicitarRecalculoSaldoDevedorClient.getResponse(loginCsa.getLogin(), "", adeNumero, observacao);
        });

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarSolicitarRecalculoSaldoDevedorComUsuarioSemPermissao() {
		log.info("Tentar solicitar recalculo saldo devedor com usuário sem permissão");

		final SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedorResponse = solicitarRecalculoSaldoDevedorClient
				.getResponse("cse", "cse12345", adeNumero, observacao);

		assertEquals("O usuário não tem permissão para executar esta operação",
				solicitarRecalculoSaldoDevedorResponse.getMensagem());
		assertEquals("329", solicitarRecalculoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(solicitarRecalculoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

}
