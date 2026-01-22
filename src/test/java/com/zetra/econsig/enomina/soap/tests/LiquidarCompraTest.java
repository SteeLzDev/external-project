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
import com.zetra.econsig.enomina.soap.client.LiquidarCompraClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.FuncaoSistemaService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.compra.LiquidarCompraResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LiquidarCompraTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private Long adeNumero = (long) 60960;
	private final String codigoMotivo = "01";

	@Autowired
    private LiquidarCompraClient liquidarCompraClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Autowired
	private FuncaoSistemaService funcaoSistemaService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@BeforeEach
	public void setUp() {
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();
	}

	@Test
	public void liquidarCompraEmDuasEtapasEPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar compra em duas etapas e possui permissao confirmar liquidacao");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "S");
		funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa1.getLogin()),
				usuarioService.getUsuario(loginCsa1.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status aguardando liquidacao
		adeNumero = (long) 60961;
		// ade com status aguardando confirmacao
		final String adeNova = "60966";

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", liquidarCompraResponse.getMensagem());
		assertEquals("000", liquidarCompraResponse.getCodRetorno().getValue());
		assertTrue(liquidarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// ade antiga alterou para status da ade nova deferido
		assertEquals(CodedValues.SAD_AGUARD_DEFER, autDescontoService.getAde(adeNova).getSadCodigo());
		// verifica se alterou o status do relacionamento para finalizado
		assertEquals(CodedValues.STC_FINALIZADO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
	}

	@Test
	public void liquidarCompraEmUmaEtapaEPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar compra em uma e possui permissao confirmar liquidacao");

		// altera parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "N");
		funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa1.getLogin()),
				usuarioService.getUsuario(loginCsa1.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status aguardando liquidacao
		adeNumero = (long) 60968;
		// ade com status aguardando confirmacao
		final String adeNova = "60971";

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", liquidarCompraResponse.getMensagem());
		assertEquals("000", liquidarCompraResponse.getCodRetorno().getValue());
		assertTrue(liquidarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// ade antiga alterou para status da ade nova deferido
		assertEquals(CodedValues.SAD_AGUARD_DEFER, autDescontoService.getAde(adeNova).getSadCodigo());
		// verifica se alterou o status do relacionamento para finalizado
		assertEquals(CodedValues.STC_FINALIZADO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
	}

	@Test
	public void liquidarCompraEmUmaEtapaENaoPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar compra em uma etapa e nao possui permissao confirmar liquidacao");

		// altera parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "N");
		funcaoSistemaService.excluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa1.getLogin()),
				usuarioService.getUsuario(loginCsa1.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status aguardando liquidacao
		adeNumero = (long) 60969;
		// ade com status aguardando confirmacao
		final String adeNova = "60972";

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", liquidarCompraResponse.getMensagem());
		assertEquals("000", liquidarCompraResponse.getCodRetorno().getValue());
		assertTrue(liquidarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// ade antiga alterou para status da ade nova deferido
		assertEquals(CodedValues.SAD_AGUARD_DEFER, autDescontoService.getAde(adeNova).getSadCodigo());
		// verifica se alterou o status do relacionamento para finalizado
		assertEquals(CodedValues.STC_FINALIZADO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
	}

	@Test
	public void liquidarCompraEmDuasEtapasENaoPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar compra em duas etapas e nao possui permissao confirmar liquidacao");

		// altera parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "S");
		funcaoSistemaService.excluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa2.getLogin()),
				usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status aguardando liquidacao
		adeNumero = (long) 35;
		// ade com status aguardando confirmacao
		final String adeNova = "60948";

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa2.getLogin(),
				loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", liquidarCompraResponse.getMensagem());
		assertEquals("000", liquidarCompraResponse.getCodRetorno().getValue());
		assertTrue(liquidarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// ade antiga alterou para status da ade nova aguardando confirmacao
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAde(adeNova).getSadCodigo());
		// verifica se alterou o status do relacionamento para finalizado
		assertEquals(CodedValues.STC_LIQUIDADO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
	}

	@Test
	public void tentarLiquidarCompraParaAdeComMaisDeUmRelacionamento() {
		log.info("Tentar liquidar compra para ade com mais de um relacionamento");

		// ade com dois relacionamentos
		adeNumero = (long) 60962;

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

        assertEquals("Operação realizada com sucesso.", liquidarCompraResponse.getMensagem());
        assertEquals("000", liquidarCompraResponse.getCodRetorno().getValue());
        assertTrue(liquidarCompraResponse.isSucesso());

        final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

        // verifica se alterou o status no banco para liquidada
        assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
	}

	@Test
	public void tentarLiquidarCompraComStatusAguardandoSaldoDevedor() {
		log.info("Tentar liquidar compra com status aguardando saldo devedor");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();

		// ade com status aguardando saldo devedor
		adeNumero = (long) 60915;

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Não é possível liquidar o contrato, pois o pagamento de saldo devedor não foi informado.",
				liquidarCompraResponse.getMensagem());
		assertEquals("Não é possível liquidar o contrato, pois o pagamento de saldo devedor não foi informado.",
				liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.STC_AGUARD_INFO_SDV.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraComMotivoDaOperacaoNaoExistente() {
		log.info("Tentar liquidar compra com motivo da operação inválido");

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, "001");

		assertEquals("Tipo de motivo da operação não encontrado.", liquidarCompraResponse.getMensagem());
		assertEquals("356", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraComStatusEmAndamento() {
		log.info("Tentar liquidar compra com status Em Andamento");

		// ade com status Em Andamento
		adeNumero = (long) 13;

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa2.getLogin(),
				loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", liquidarCompraResponse.getMensagem());
		assertEquals("294", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_EMANDAMENTO, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarCompraComStatusSuspensa() {
		log.info("Tentar liquidar compra com status Suspensa");

		// ade com status Suspensa
		adeNumero = (long) 32;

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa2.getLogin(),
				loginCsa2.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", liquidarCompraResponse.getMensagem());
		assertEquals("294", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_SUSPENSA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarCompraDeConsignacaoLiquidada() {
		log.info("Tentar liquidar compra de consignação liquidada");

		// ade liquidada
		adeNumero = (long) 60925;

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", liquidarCompraResponse.getMensagem());
		assertEquals("294", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarCompraDeConsignacaoNaoExistente() {
		log.info("Tentar liquidar compra de consignação não existente");

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), (long) 60800, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", liquidarCompraResponse.getMensagem());
		assertEquals("294", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarCompraSemInformarAdeNumero() {
		log.info("Tentar liquidar compra sem informar ade numero");

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), (long) 0, codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.", liquidarCompraResponse.getMensagem());
		assertEquals("322", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarCompraComUsuarioInvalido() {
		log.info("Tentar liquidar compra com usuário inválido");

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse("csa1", loginCsa1.getSenha(),
				adeNumero, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", liquidarCompraResponse.getMensagem());
		assertEquals("358", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraComSenhaInvalida() {
		log.info("Tentar liquidar compra com senha inválida");

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				"ser12345", adeNumero, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", liquidarCompraResponse.getMensagem());
		assertEquals("358", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraComIPDeAcessoInvalido() {
		log.info("Tentar liquidar compra com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse(loginCsa1.getLogin(),
				loginCsa1.getSenha(), adeNumero, codigoMotivo);

		assertEquals("IP de acesso inválido", liquidarCompraResponse.getMensagem());
		assertEquals("362", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraSemInformarUsuario() {
		log.info("Tentar liquidar compra sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            liquidarCompraClient.getResponse("", loginCsa1.getSenha(), adeNumero, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraSemInformarSenha() {
		log.info("Tentar liquidar compra sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            liquidarCompraClient.getResponse(loginCsa1.getLogin(), "", adeNumero, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarCompraComUsuarioSemPermissao() {
		log.info("Tentar liquidar compra com usuário sem permissão");

		final LiquidarCompraResponse liquidarCompraResponse = liquidarCompraClient.getResponse("cse", "cse12345", adeNumero,
				codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação", liquidarCompraResponse.getMensagem());
		assertEquals("329", liquidarCompraResponse.getCodRetorno().getValue());
		assertFalse(liquidarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
		assertEquals(CodedValues.STC_AGUARD_LIQUIDACAO.toString(), relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}
}
