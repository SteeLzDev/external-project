package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.zetra.econsig.enomina.soap.client.CancelarConsignacaoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.CancelarConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CancelarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private Long adeNumero = (long) 60897;
	private final String adeIdentificador = "Solicitação Web";
	private final String codigoMotivo = "01";

	@Autowired
    private CancelarConsignacaoClient cancelarConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@BeforeEach
	public void setUp() {
		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();
	}

	@Test
	public void cancelarConsignacaoComStatusSolicitacao() {
		log.info("Cancelar consignacao com status solicitação");

		// ade com status solicitacao
		adeNumero = (long) 61000;

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("000", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(cancelarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("Cancelada", cancelarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("BANCO BRASIL", cancelarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("EMPRÉSTIMO", cancelarConsignacaoResponse.getBoleto().getValue().getServico());
		assertTrue(cancelarConsignacaoResponse.getHistoricos().get(0).getDescricao().contains(
				"NOVA SITUAÇÃO: Cancelada Motivo: Cancelamento Observação:  Automacao (Período de referência:"));
		assertEquals("csa2", cancelarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", cancelarConsignacaoResponse.getHistoricos().get(0).getTipo());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarConsignacaoComStatusAguardandoConfirmacao() {
		log.info("Cancelar consignacao com status aguardando confirmação");

		// ade com status aguardando confirmação
		adeNumero = (long) 60999;

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("000", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(cancelarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertTrue(cancelarConsignacaoResponse.getHistoricos().get(0).getDescricao().contains(
				"NOVA SITUAÇÃO: Cancelada Motivo: Cancelamento Observação:  Automacao (Período de referência:"));
		assertEquals("csa2", cancelarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", cancelarConsignacaoResponse.getHistoricos().get(0).getTipo());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarConsignacaoComStatusAguardandoDeferimento() {
		log.info("Cancelar consignacao com status aguardando deferimento");

		// ade com status aguardando deferimento
		adeNumero = (long) 60998;

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("000", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(cancelarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertTrue(cancelarConsignacaoResponse.getHistoricos().get(0).getDescricao().contains(
				"NOVA SITUAÇÃO: Cancelada Motivo: Cancelamento Observação:  Automacao (Período de referência:"));
		assertEquals("csa", cancelarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", cancelarConsignacaoResponse.getHistoricos().get(0).getTipo());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarConsignacaoComStatusDeferido() {
		log.info("Cancelar consignacao com status aguardando deferimento");

		// ade com status deferido
		adeNumero = (long) 60997;

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("000", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(cancelarConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarConsignacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarConsignacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarConsignacaoResponse.getBoleto().getValue().getConta().getValue());
		assertTrue(cancelarConsignacaoResponse.getHistoricos().get(0).getDescricao().contains(
				"NOVA SITUAÇÃO: Cancelada Motivo: Cancelamento Observação:  Automacao (Período de referência:"));
		assertEquals("csa2", cancelarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertEquals("Informação", cancelarConsignacaoResponse.getHistoricos().get(0).getTipo());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarConsignacaoComMaisDeUmaConsignacao() {
		log.info("Cancelar consignacao com mais de uma consignação");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient.getResponse(
				loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 0, adeIdentificador, codigoMotivo);

		assertEquals("Mais de uma consignação encontrada", cancelarConsignacaoResponse.getMensagem());
		assertEquals("245", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());
		assertTrue(cancelarConsignacaoResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarCancelarConsignacaoSemInformarAdeNumeroEAdeIdentificador() {
		log.info("Tentar cancelar consignacao sem informar adeNumero e adeIdentificador");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 0, "", codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("322", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarCancelarConsignacaoDeConsignacaoNaoExistente() {
		log.info("Tentar cancelar consignacao de consignação não existente");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), (long) 60800, "", codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarConsignacaoResponse.getMensagem());
		assertEquals("294", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarCancelarConsignacaoDeConsignacaoJaCancelada() {
		log.info("Tentar cancelar consignacao de consignação já cancelada");

		// ade com status cancelada
		adeNumero = (long) 60903;

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarConsignacaoResponse.getMensagem());
		assertEquals("294", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoSemInformarMotivoDeOperacao() {
		log.info("Tentar cancelar consignacao sem informar motivo operação");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdentificador, "");

		assertEquals("O motivo da operação deve ser informado.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("445", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoComMotivoDaOperacaoInvalido() {
		log.info("Tentar cancelar consignacao com motivo da operação inválido");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), loginCsa2.getSenha(), adeNumero, adeIdentificador, "001");

		assertEquals("O motivo da operação inválido.", cancelarConsignacaoResponse.getMensagem());
		assertEquals("401", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoComUsuarioInvalido() {
		log.info("Tentar cancelar consignacao com usuário inválido");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient.getResponse("csa1",
				loginCsa2.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", cancelarConsignacaoResponse.getMensagem());
		assertEquals("358", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoComSenhaInvalida() {
		log.info("Tentar cancelar consignacao com senha inválida");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa2.getLogin(), "ser12345", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", cancelarConsignacaoResponse.getMensagem());
		assertEquals("358", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar cancelar consignacao com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria(loginCsa1.getLogin(), CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient
				.getResponse(loginCsa1.getLogin(), loginCsa1.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("IP de acesso inválido", cancelarConsignacaoResponse.getMensagem());
		assertEquals("362", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoSemInformarUsuario() {
		log.info("Tentar cancelar consignacao sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarConsignacaoClient.getResponse("", loginCsa2.getSenha(), adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoSemInformarSenha() {
		log.info("Tentar cancelar consignacao sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarConsignacaoClient.getResponse(loginCsa2.getLogin(), "", adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar cancelar consignacao com usuário sem permissão");

		final CancelarConsignacaoResponse cancelarConsignacaoResponse = cancelarConsignacaoClient.getResponse("cse",
				"cse12345", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação",
				cancelarConsignacaoResponse.getMensagem());
		assertEquals("329", cancelarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarConsignacaoResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}
}
