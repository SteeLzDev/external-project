package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.CancelarReservaClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.CancelarReservaResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CancelarReservaTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private Long adeNumero = (long) 60897;
	private final String adeIdentificador = "Solicitação Web";
	private final String codigoMotivo = "01";

	@Autowired
    private CancelarReservaClient cancelarReservaClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void cancelarReservaComStatusSolicitacao() {
		log.info("Cancelar reserva com status solicitação");

		// ade com status solicitacao
		adeNumero = (long) 60900;

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarReservaResponse.getMensagem());
		assertEquals("000", cancelarReservaResponse.getCodRetorno().getValue());
		assertTrue(cancelarReservaResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarReservaResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarReservaResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarReservaResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarReservaResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarReservaResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarReservaResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarReservaResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarReservaResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("Cancelada", cancelarReservaResponse.getBoleto().getValue().getSituacao());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarReservaComStatusAguardandoConfirmacao() {
		log.info("Cancelar reserva com status aguardando confirmação");

		// ade com status aguardando confirmação
		adeNumero = (long) 60898;

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarReservaResponse.getMensagem());
		assertEquals("000", cancelarReservaResponse.getCodRetorno().getValue());
		assertTrue(cancelarReservaResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarReservaResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarReservaResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarReservaResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarReservaResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarReservaResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarReservaResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarReservaResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarReservaResponse.getBoleto().getValue().getConta().getValue());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarReservaComStatusAguardandoDeferimento() {
		log.info("Cancelar reserva com status aguardando deferimento");

		// ade com status aguardando deferimento
		adeNumero = (long) 60899;

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarReservaResponse.getMensagem());
		assertEquals("000", cancelarReservaResponse.getCodRetorno().getValue());
		assertTrue(cancelarReservaResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", cancelarReservaResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarReservaResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarReservaResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarReservaResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", cancelarReservaResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", cancelarReservaResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", cancelarReservaResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", cancelarReservaResponse.getBoleto().getValue().getConta().getValue());

		// verifica se alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void cancelarReservaQuePossuiRelacionamentoPorMotivoRenegociacaoAdeAguardandoLiquidacao() {
		log.info("Cancelar reserva que possui relacionamento por motivo renegociação ADE aguardando liquidação");

		// status aguardando confirmacao
		adeNumero = (long) 60904;
		// possui status 11 (aguardando liquidacao)
		final long adeNumeroAntigo = 60901;

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarReservaResponse.getMensagem());
		assertEquals("000", cancelarReservaResponse.getCodRetorno().getValue());
		assertTrue(cancelarReservaResponse.isSucesso());

		// verifica se alterou o status no banco para 7
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());

		// verifica se alterou o status do ade antigo para 8 (Liquidada)
		assertEquals("4", autDescontoService.getAde(Long.toString(adeNumeroAntigo)).getSadCodigo());
	}

	@Test
	public void cancelarReservaQuePossuiRelacionamentoPorMotivoRenegociacaoAdeLiquidada() {
		log.info("Cancelar reserva que possui relacionamento por motivo renegociação ADE liquidada");

		// status aguardando confirmacao
		adeNumero = (long) 60905;
		// possui status 8 (liquidada)
		final long adeNumeroAntigo = 60902;

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarReservaResponse.getMensagem());
		assertEquals("000", cancelarReservaResponse.getCodRetorno().getValue());
		assertTrue(cancelarReservaResponse.isSucesso());

		// verifica se alterou o status no banco para 7
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());

		// verifica se alterou o status do ade antigo para 8 (Liquidada)
		assertEquals("8", autDescontoService.getAde(Long.toString(adeNumeroAntigo)).getSadCodigo());
	}

	@Test
	public void cancelarReservaComMaisDeUmaConsignacao() {
		log.info("Cancelar reserva com mais de uma consignação");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 0, adeIdentificador, codigoMotivo);

		assertEquals("Mais de uma consignação encontrada", cancelarReservaResponse.getMensagem());
		assertEquals("245", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());
		assertTrue(cancelarReservaResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarCancelarReservaDeConsignacaoNaoExistente() {
		log.info("Tentar cancelar reserva de consignação não existente");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 60800, "", codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarReservaResponse.getMensagem());
		assertEquals("294", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

	}

	@Test
	public void tentarCancelarReservaDeConsignacaoJaCancelada() {
		log.info("Tentar cancelar reserva de consignação já cancelada");

		// ade com status cancelada
		adeNumero = (long) 60903;

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarReservaResponse.getMensagem());
		assertEquals("294", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaSemInformarMotivoDeOperacao() {
		log.info("Tentar cancelar reserva sem informar motivo operação");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, "");

		assertEquals("O motivo da operação deve ser informado.", cancelarReservaResponse.getMensagem());
		assertEquals("445", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaComMotivoDaOperacaoInvalido() {
		log.info("Tentar cancelar reserva com motivo da operação inválido");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, "001");

		assertEquals("O motivo da operação inválido.", cancelarReservaResponse.getMensagem());
		assertEquals("401", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaComUsuarioInvalido() {
		log.info("Tentar cancelar reserva com usuário inválido");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", cancelarReservaResponse.getMensagem());
		assertEquals("358", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaComSenhaInvalida() {
		log.info("Tentar cancelar reserva com senha inválida");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse(loginCsa.getLogin(),
				"ser12345", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", cancelarReservaResponse.getMensagem());
		assertEquals("358", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaComIPDeAcessoInvalido() {
		log.info("Tentar cancelar reserva com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("IP de acesso inválido", cancelarReservaResponse.getMensagem());
		assertEquals("362", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaSemInformarUsuario() {
		log.info("Tentar cancelar reserva sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarReservaClient.getResponse("", loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaSemInformarSenha() {
		log.info("Tentar cancelar reserva sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarReservaClient.getResponse(loginCsa.getLogin(), "", adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarReservaComUsuarioSemPermissao() {
		log.info("Tentar cancelar reserva com usuário sem permissão");

		final CancelarReservaResponse cancelarReservaResponse = cancelarReservaClient.getResponse("cse", "cse12345",
				adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação", cancelarReservaResponse.getMensagem());
		assertEquals("329", cancelarReservaResponse.getCodRetorno().getValue());
		assertFalse(cancelarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}
}
