package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.LiquidarConsignacaoClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.FuncaoSistemaService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.RelacionamentoServicoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.LiquidarConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LiquidarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private final LoginInfo loginSer = LoginValues.servidor2;
	private Long adeNumero = (long) 12;
	private final String adeIdenficador = "";
	private final String codigoMotivo = "01";
	private final String periodo = "";

	@Autowired
    private LiquidarConsignacaoClient liquidarConsignacaoClient;

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

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private RelacionamentoServicoService relacionamentoServicoService;

	@BeforeEach
	public void setUp() {
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();
	}

	@Test
	public void liquidarConsignacaoEmDuasEtapasEPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar consignação em duas etapas e possui permissao confirmar liquidacao");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "S");
		funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa2.getLogin()),
				usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status deferida
		adeNumero = (long) 14;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoEmUmaEtapaEPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar consignação em uma e possui permissao confirmar liquidacao");

		// altera parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "N");
		funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa2.getLogin()),
				usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status deferida
		adeNumero = (long) 15;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoEmUmaEtapaENaoPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar consignação em uma etapa e nao possui permissao confirmar liquidacao");

		// altera parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "N");
		funcaoSistemaService.excluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa2.getLogin()),
				usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status deferida
		adeNumero = (long) 18;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoEmDuasEtapasENaoPossuiPermissaoConfirmarLiquidacao() {
		log.info("Liquidar consignação em duas etapas e nao possui permissao confirmar liquidacao");

		// altera parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, "S");
		funcaoSistemaService.excluirFuncaoCsa(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA,
				usuarioService.getCsaCodigo(loginCsa2.getLogin()),
				usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo());
		ENominaInitializer.limparCache();

		// ade com status deferida
		adeNumero = (long) 19;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void tentarLiquidarConsignacaoComStatusAguardandoSaldoDevedor() {
		log.info("Tentar liquidar consignação com status aguardando saldo devedor");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();

		// ade com status aguardando saldo devedor
		adeNumero = (long) 60915;

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Não é possível liquidar o contrato, pois o pagamento de saldo devedor não foi informado.",
				liquidarConsignacaoResponse.getMensagem());
		assertEquals("Não é possível liquidar o contrato, pois o pagamento de saldo devedor não foi informado.",
				liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.STC_AGUARD_INFO_SDV.toString(), relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoComMotivoDaOperacaoNaoExistente() {
		log.info("Tentar liquidar consignação com motivo da operação inválido");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, "001", periodo);

		assertEquals("O motivo da operação inválido.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("401", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoSemInformarMotivoDaOperacao() {
		log.info("Tentar liquidar consignação sem informar motivo da operação");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, "", periodo);

		assertEquals("O motivo da operação deve ser informado.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("445", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void liquidarConsignacaoComStatusEmAndamento() {
		log.info("Liquidar consignação com status Em Andamento");

		// ade com status Em Andamento
		adeNumero = (long) 13;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		assertEquals("Sr. Antonio da Silva Augusto", liquidarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("004.503.189-40", liquidarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("Solteiro(a)", liquidarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("145985", liquidarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("36", liquidarConsignacaoResponse.getBoleto().getValue().getPrazoServidor().toString());
		assertEquals("Carlota Joaquina 21.346.414/0001-47",
				liquidarConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("213464140", liquidarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", liquidarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", liquidarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", liquidarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("FINANCIAMENTO DE DÍVIDA", liquidarConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals("Liquidada", liquidarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals(CodedValues.SAD_LIQUIDADA, liquidarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("100.0", String.valueOf(liquidarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals("-1.0", liquidarConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("002", liquidarConsignacaoResponse.getBoleto().getValue().getIndice());
		assertEquals("8", String.valueOf(liquidarConsignacaoResponse.getBoleto().getValue().getPrazo()));
		assertEquals("023", liquidarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertTrue(liquidarConsignacaoResponse.getHistoricos().get(0).getDescricao()
				.contains("NOVA SITUAÇÃO: Liquidada Motivo: Cancelamento Observação:  Automacao (Período de referência:"));

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoInformandoPeriodo() {
		log.info("Liquidar consignação informando periodo");

		adeNumero = (long) 22;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, "12/2021");

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		assertEquals("Sr. Antonio da Silva Augusto", liquidarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("004.503.189-40", liquidarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("Solteiro(a)", liquidarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("145985", liquidarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("36", liquidarConsignacaoResponse.getBoleto().getValue().getPrazoServidor().toString());
		assertEquals("Carlota Joaquina 21.346.414/0001-47",
				liquidarConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("213464140", liquidarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", liquidarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", liquidarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", liquidarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("FINANCIAMENTO DE DÍVIDA", liquidarConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals("Liquidada", liquidarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals(CodedValues.SAD_LIQUIDADA, liquidarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("100.0", String.valueOf(liquidarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals("-1.0", liquidarConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("008", liquidarConsignacaoResponse.getBoleto().getValue().getIndice());
		assertEquals("8", String.valueOf(liquidarConsignacaoResponse.getBoleto().getValue().getPrazo()));
		assertEquals("023", liquidarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertTrue(liquidarConsignacaoResponse.getHistoricos().get(0).getDescricao()
				.contains("NOVA SITUAÇÃO: Liquidada Motivo: Cancelamento Observação:  Automacao (Período de referência:"));

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoComStatusEstoque() {
		log.info("Tentar liquidar consignação com status Estoque");

		// ade com status Estoque
		adeNumero = (long) 24;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoComStatusDeferida() {
		log.info("Liquidar consignação com status Deferida");

		// ade com status Deferida
		adeNumero = (long) 20;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void liquidarConsignacaoComStatusSuspensa() {
		log.info("Liquidar consignação com status Suspensa");

		parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("023"),
				CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA, "1");
		ENominaInitializer.limparCache();

		// ade com status Suspensa
		adeNumero = (long) 25;
		assertEquals(CodedValues.SAD_SUSPENSA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de liquidacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void tentarLiquidarConsignacaoComStatusSuspensa() {
		log.info("Tentar liquidar consignação com status Suspensa");

		parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("023"),
				CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA, "0");
		ENominaInitializer.limparCache();

		// ade com status Suspensa
		adeNumero = (long) 32;

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Nenhuma consignação encontrada", liquidarConsignacaoResponse.getMensagem());
		assertEquals("294", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_SUSPENSA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoComRelacionamentoBloqueioOperacao() {
		log.info("Tentar liquidar consignação com relacionamento bloqueio operacao");

		relacionamentoServicoService.incluirRelacionamentoServico(servicoService.retornaSvcCodigo("019"),
				servicoService.retornaSvcCodigo("019"), CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO);
		ENominaInitializer.limparCache();

		// ade com status Deferida
		adeNumero = (long) 61003;

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals(
				"Não é possível liquidar este contrato pois existe um contrato ativo do serviço: CARTAO DE CREDITO - RESERVA",
				liquidarConsignacaoResponse.getMensagem());
		assertEquals(
				"Não é possível liquidar este contrato pois existe um contrato ativo do serviço: CARTAO DE CREDITO - RESERVA",
				liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarConsignacaoComValorReservadoInsuficiente() {
		log.info("Tentar liquidar consignação de consignação com valor reservado insuficiente");

		relacionamentoServicoService.incluirRelacionamentoServico(servicoService.retornaSvcCodigo("019"),
				servicoService.retornaSvcCodigo("001"), CodedValues.TNT_CARTAO);
		relacionamentoServicoService.excluirRelacionamentoServico(servicoService.retornaSvcCodigo("019"),
				servicoService.retornaSvcCodigo("019"), CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO);
		ENominaInitializer.limparCache();

		// ade com status Deferida
		adeNumero = (long) 61001;

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals(
				"Não foi possível realizar a operação, pois não há valor reservado suficiente para os lançamentos.",
				liquidarConsignacaoResponse.getMensagem());
		assertEquals(
				"552",
				liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());
	}

	@Test
	public void liquidarConsignacaoDeReservaCartaoCredito() {
		log.info("Liquidar consignação de reserva de cartão de crédito");

		relacionamentoServicoService.excluirRelacionamentoServico(servicoService.retornaSvcCodigo("019"),
				servicoService.retornaSvcCodigo("001"), CodedValues.TNT_CARTAO);
		relacionamentoServicoService.excluirRelacionamentoServico(servicoService.retornaSvcCodigo("019"),
				servicoService.retornaSvcCodigo("019"), CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO);
		ENominaInitializer.limparCache();

		// ade com status Deferida
		adeNumero = (long) 61002;

		final RegistroServidor registroServidor = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin()));
		final BigDecimal margemDisponivel = registroServidor.getRseMargemRest3();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Operação realizada com sucesso.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("000", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(liquidarConsignacaoResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco para liquidada
		assertEquals(CodedValues.SAD_LIQUIDADA, autDesconto.getSadCodigo());
		// verifica que a margem foi alterada
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginSer.getLogin())).getRseMargemRest3()) <= 0);
	}

	@Test
	public void tentarLiquidarConsignacaoComStatusAguardandoLiquidacaoCompra() {
		log.info("Tentar liquidar consignação com status aguardando consignação compra");

		// ade com status aguardando consignação compra
		adeNumero = (long) 60960;

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Nenhuma consignação encontrada", liquidarConsignacaoResponse.getMensagem());
		assertEquals("294", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA,
				autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoDeConsignacaoLiquidada() {
		log.info("Tentar liquidar consignação de consignação liquidada");

		// ade liquidada
		adeNumero = (long) 60925;

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Nenhuma consignação encontrada", liquidarConsignacaoResponse.getMensagem());
		assertEquals("294", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarConsignacaoComPeriodoInvalido() {
		log.info("Tentar liquidar consignação com periodo invalido");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, "02202");

		assertEquals("O período informado é inválido.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("409", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarConsignacaoDeConsignacaoNaoExistente() {
		log.info("Tentar liquidar consignação de consignação não existente");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 60800, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Nenhuma consignação encontrada", liquidarConsignacaoResponse.getMensagem());
		assertEquals("294", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarConsignacaoSemInformarAdeNumeroEAdeIdentificador() {
		log.info("Tentar liquidar consignação sem informar adeNumero e adeIdentificador");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 0, "", codigoMotivo, periodo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.", liquidarConsignacaoResponse.getMensagem());
		assertEquals("322", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarLiquidarConsignacaoComUsuarioInvalido() {
		log.info("Tentar liquidar consignação com usuário inválido");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse("csa1",
				loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Usuário ou senha inválidos", liquidarConsignacaoResponse.getMensagem());
		assertEquals("358", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoComSenhaInvalida() {
		log.info("Tentar liquidar consignação com senha inválida");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), "ser12345", adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("Usuário ou senha inválidos", liquidarConsignacaoResponse.getMensagem());
		assertEquals("358", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar liquidar consignação com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse(
				loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("IP de acesso inválido", liquidarConsignacaoResponse.getMensagem());
		assertEquals("362", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoSemInformarUsuario() {
		log.info("Tentar liquidar consignação sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            liquidarConsignacaoClient.getResponse("", loginCsa2.getSenha(), adeNumero, adeIdenficador, codigoMotivo, periodo);
        });

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoSemInformarSenha() {
		log.info("Tentar liquidar consignação sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            liquidarConsignacaoClient.getResponse(loginCsa2.getLogin(), "", adeNumero, adeIdenficador, codigoMotivo, periodo);
        });

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarLiquidarConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar liquidar consignação com usuário sem permissão");

		final LiquidarConsignacaoResponse liquidarConsignacaoResponse = liquidarConsignacaoClient.getResponse("cse",
				"cse12345", adeNumero, adeIdenficador, codigoMotivo, periodo);

		assertEquals("O usuário não tem permissão para executar esta operação",
				liquidarConsignacaoResponse.getMensagem());
		assertEquals("329", liquidarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(liquidarConsignacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}
}
