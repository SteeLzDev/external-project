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
import com.zetra.econsig.enomina.soap.client.RejeitarPgSaldoDevedorClient;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.compra.RejeitarPgSaldoDevedorResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RejeitarPgSaldoDevedorTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private Long adeNumero = (long) 33;
	private final String observacao = "Automação";
	private String svcCodigoEmprestimo;

	@Autowired
    private RejeitarPgSaldoDevedorClient rejeitarPgSaldoDevedorClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Autowired
	private ConsignatariaService consignatariaService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ServicoService servicoService;

	@BeforeEach
	public void setUp() {
		svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");

		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS, "0");

		ENominaInitializer.limparCache();
	}

	@Test
	public void rejeitarPagamentoSaldoDevedorComSucesso() {
		log.info("Rejeitar pagamento saldo devedor com sucesso");

		adeNumero = (long) 60970;

		final Consignataria csa = consignatariaService.getConsignataria("3700808080808080808080808080A538");
		csa.setCsaAtivo(CodedValues.STS_INATIVO);
		consignatariaService.updateConsignataria(csa);

		consignatariaService.criarOcorrenciaConsignataria("rtr84u8445558",
				"3700808080808080808080808080A538",
				CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA,
				"AA808080808080808080808080809E80");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Operação realizada com sucesso. A consignatária foi desbloqueada automaticamente.",
				rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("000", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica se alterou o status do relacionamento para aguardando pagamento
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar ocorrências para registro do rejeiçao do pagamento do saldo
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(
				CodedValues.TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR,
				autDescontoService.getAde(adeNumero.toString()).getAdeCodigo()));
	}

	@Test
	public void rejeitarPagamentoSaldoDevedorComBloqueioDeAmbasConsignatarias() {
		log.info("Rejeitar pagamento saldo devedor com bloqueio de ambas consignatarias");

		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS, "1");

		JspHelper.limparCacheParametros();

		adeNumero = (long) 60952;

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Operação realizada com sucesso.", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("000", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica se alterou o status do relacionamento para aguardando pagamento
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar ocorrências para registro do rejeiçao do pagamento do saldo
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(
				CodedValues.TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR,
				autDescontoService.getAde(adeNumero.toString()).getAdeCodigo()));
		// verificar que as csas foram bloqueadas
		assertEquals(CodedValues.STS_INATIVO, consignatariaService.getConsignataria(usuarioService.getCsaCodigo(loginCsa1.getLogin())).getCsaAtivo());
		assertEquals(CodedValues.STS_INATIVO, consignatariaService.getConsignataria(usuarioService.getCsaCodigo(loginCsa2.getLogin())).getCsaAtivo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorParaAdeComMaisDeUmRelacionamento() {
		log.info("Tentar rejeitar pagamento saldo devedor para ade com mais de um relacionamento");

		// ade com dois relacionamentos
		adeNumero = (long) 60949;

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, observacao);

		assertEquals("Mais de uma consignação encontrada", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("245", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComStatusAguardandoSaldoDevedor() {
		log.info("Rejeitar pagamento saldo devedor com status aguardando saldo devedor");

		// ade com status aguardando saldo devedor
		adeNumero = (long) 60915;

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("294", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("1", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComConsignatariaSemPermissao() {
		log.info("Rejeitar pagamento saldo devedor com consignataria sem permissao");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("294", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorDeConsignacaoLiquidada() {
		log.info("Tentar rejeitar pagamento saldo devedor de consignação liquidada");

		// ade liquidada
		adeNumero = (long) 60925;

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("294", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorDeConsignacaoNaoExistente() {
		log.info("Tentar rejeitar pagamento saldo devedor de consignação não existente");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 60800, observacao);

		assertEquals("Nenhuma consignação encontrada", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("294", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorDeConsignacaoAguardandoPagamento() {
		log.info("Tentar rejeitar pagamento saldo devedor de consignação aguardando pagamento");

		// ade aguardando pagamento
		adeNumero = (long) 60938;

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Nenhuma consignação encontrada", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("294", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorSemInformarAdeNumero() {
		log.info("Tentar rejeitar pagamento saldo devedor sem informar ade numero");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 0, observacao);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("322", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComUsuarioInvalido() {
		log.info("Tentar rejeitar pagamento saldo devedor com usuário inválido");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient.getResponse("csa1",
				loginCsa2.getSenha(), adeNumero, observacao);

		assertEquals("Usuário ou senha inválidos", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("358", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComSenhaInvalida() {
		log.info("Tentar rejeitar pagamento saldo devedor com senha inválida");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa2.getLogin(), "ser12345", adeNumero, observacao);

		assertEquals("Usuário ou senha inválidos", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("358", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComIPDeAcessoInvalido() {
		log.info("Tentar rejeitar pagamento saldo devedor com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("IP de acesso inválido", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("362", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorSemInformarUsuario() {
		log.info("Tentar rejeitar pagamento saldo devedor sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            rejeitarPgSaldoDevedorClient.getResponse("", loginCsa2.getSenha(), adeNumero, observacao);
        });

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorSemInformarSenha() {
		log.info("Tentar rejeitar pagamento saldo devedor sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            rejeitarPgSaldoDevedorClient.getResponse(loginCsa2.getLogin(), "", adeNumero, observacao);
        });

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComUsuarioSemPermissao() {
		log.info("Tentar rejeitar pagamento saldo devedor com usuário sem permissão");

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient.getResponse("cse",
				"cse12345", adeNumero, observacao);

		assertEquals("O usuário não tem permissão para executar esta operação",
				rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("329", rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarRejeitarPagamentoSaldoDevedorComSaldoNaoInformado() {
		log.info("Tentar rejeitar pagamento saldo devedor com saldo nao informado");

		adeNumero = (long) 60953;

		final RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedorResponse = rejeitarPgSaldoDevedorClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, observacao);

		assertEquals("Pagamento de saldo devedor não informado.", rejeitarPgSaldoDevedorResponse.getMensagem());
		assertEquals("Pagamento de saldo devedor não informado.",
				rejeitarPgSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(rejeitarPgSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}
}
