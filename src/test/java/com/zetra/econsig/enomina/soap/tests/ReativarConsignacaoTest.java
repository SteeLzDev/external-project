package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.zetra.econsig.enomina.soap.client.ReativarConsignacaoClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.ReativarConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReativarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private Long adeNumero = (long) 61019;
	private final String adeIdentificador = "Solicitação Web";
	private final String codigoMotivo = "01";

	@Autowired
    private ReativarConsignacaoClient reativarConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@BeforeEach
	public void setUp() {
		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(),
				CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_RETEM_MARGEM_REVISAO_ACAO_SUSPENSAO, "S");
		ENominaInitializer.limparCache();
	}

	@Test
	public void reativarConsignacaoComStatusSuspensa() {
		log.info("Reativar consignacao com status suspensa");

		// ade com status suspensa
		adeNumero = (long) 61020;

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", reativarConsignacaoResponse.getMensagem());
		assertEquals("000", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(reativarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", reativarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", reativarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", reativarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", reativarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", reativarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", reativarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", reativarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", reativarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("Deferida", reativarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("BANCO BRASIL", reativarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("EMPRÉSTIMO", reativarConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals("csa2", reativarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", reativarConsignacaoResponse.getHistoricos().get(0).getTipo());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de reativacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_REATIVACAO_CONTRATO,
				autDesconto.getAdeCodigo()));
	}

	@Test
	public void reativarConsignacaoComStatusSuspensaComParcelaPagaForaDoPeriodoAtual() {
		log.info("Reativar consignacao com status suspensa com parcela paga");

		// ade com status suspensa com parcela paga fora do periodo atual
		adeNumero = (long) 61021;

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", reativarConsignacaoResponse.getMensagem());
		assertEquals("000", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(reativarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", reativarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", reativarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", reativarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", reativarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", reativarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", reativarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", reativarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", reativarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("csa2", reativarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", reativarConsignacaoResponse.getHistoricos().get(0).getTipo());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de reativacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_REATIVACAO_CONTRATO,
				autDesconto.getAdeCodigo()));
		// verificar ocorrencia liquidação
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
	}

	@Test
	public void reativarConsignacaoComStatusSuspensaComParcelaPagaForaDoPeriodoAtualEMotivoLiberacao() {
		log.info("Reativar consignacao com status suspensa com parcela paga e motivo liberacao");

		// ade com status suspensa com parcela paga fora do periodo atual
		adeNumero = (long) 61022;

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", "07");

		assertEquals("Operação realizada com sucesso.", reativarConsignacaoResponse.getMensagem());
		assertEquals("000", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(reativarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", reativarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", reativarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", reativarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", reativarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", reativarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", reativarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", reativarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", reativarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("csa2", reativarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", reativarConsignacaoResponse.getHistoricos().get(0).getTipo());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de reativacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_REATIVACAO_CONTRATO,
				autDesconto.getAdeCodigo()));
		// verificar ocorrencia liquidação
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verificar remoção dados autorizacao desconto
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero.toString(),
				CodedValues.TDA_VALOR_RETIDO_REVISAO_MARGEM));
	}

	@Test
	public void reativarConsignacaoComStatusSuspensaSemRetencaoMargem() {
		log.info("Reativar consignacao com status suspensa com parcela paga e motivo liberacao");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_RETEM_MARGEM_REVISAO_ACAO_SUSPENSAO, "N");
		ENominaInitializer.limparCache();

		// ade com status suspensa com parcela paga fora do periodo atual
		adeNumero = (long) 61023;

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", "07");

		assertEquals("Operação realizada com sucesso.", reativarConsignacaoResponse.getMensagem());
		assertEquals("000", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(reativarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", reativarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", reativarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", reativarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", reativarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", reativarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", reativarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", reativarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", reativarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("csa2", reativarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", reativarConsignacaoResponse.getHistoricos().get(0).getTipo());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDesconto.getSadCodigo());
		// verificar o ocorrências para registro de reativacao
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_REATIVACAO_CONTRATO,
				autDesconto.getAdeCodigo()));
		// verificar ocorrencia liquidação
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_TARIF_LIQUIDACAO,
				autDesconto.getAdeCodigo()));
		// verificar remoção dados autorizacao desconto
		assertNotNull(autDescontoService.getDadosAutDesconto(adeNumero.toString(),
				CodedValues.TDA_VALOR_RETIDO_REVISAO_MARGEM));
	}

	@Test
	public void tentarReativarConsignacaoSemInformarAdeNumeroEAdeIdentificador() {
		log.info("Tentar reativar consignacao sem informar adeNumero e adeIdentificador");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 0, "", codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.", reativarConsignacaoResponse.getMensagem());
		assertEquals("322", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarReativarConsignacaoDeConsignacaoNaoExistente() {
		log.info("Tentar reativar consignacao de consignação não existente");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 60800, "", codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", reativarConsignacaoResponse.getMensagem());
		assertEquals("294", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarReativarConsignacaoComStatusDeferida() {
		log.info("Tentar reativar consignacao com status deferida");

		// ade com status deferida
		adeNumero = (long) 12;

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", reativarConsignacaoResponse.getMensagem());
		assertEquals("294", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoSemInformarMotivoDeOperacao() {
		log.info("Tentar reativar consignacao sem informar motivo operação");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdentificador, "");

		assertEquals("O motivo da operação deve ser informado.", reativarConsignacaoResponse.getMensagem());
		assertEquals("445", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoComMotivoDaOperacaoInvalido() {
		log.info("Tentar reativar consignacao com motivo da operação inválido");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdentificador, "001");

		assertEquals("O motivo da operação inválido.", reativarConsignacaoResponse.getMensagem());
		assertEquals("401", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoComUsuarioInvalido() {
		log.info("Tentar reativar consignacao com usuário inválido");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient.getResponse("csa1",
				loginCsa2.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", reativarConsignacaoResponse.getMensagem());
		assertEquals("358", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoComSenhaInvalida() {
		log.info("Tentar reativar consignacao com senha inválida");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), "ser12345", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", reativarConsignacaoResponse.getMensagem());
		assertEquals("358", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar reativar consignacao com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("IP de acesso inválido", reativarConsignacaoResponse.getMensagem());
		assertEquals("362", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoSemInformarUsuario() {
		log.info("Tentar reativar consignacao sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            reativarConsignacaoClient.getResponse("", loginCsa2.getSenha(), adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoSemInformarSenha() {
		log.info("Tentar reativar consignacao sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            reativarConsignacaoClient.getResponse(loginCsa2.getLogin(), "", adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarReativarConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar reativar consignacao com usuário sem permissão");

		final ReativarConsignacaoResponse reativarConsignacaoResponse = reativarConsignacaoClient.getResponse("cse",
				"cse12345", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação",
				reativarConsignacaoResponse.getMensagem());
		assertEquals("329", reativarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(reativarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("6", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}
}
