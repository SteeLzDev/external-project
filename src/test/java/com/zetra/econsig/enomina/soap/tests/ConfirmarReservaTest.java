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
import com.zetra.econsig.enomina.soap.client.ConfirmarReservaClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.ConfirmarReservaResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConfirmarReservaTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private Long adeNumero = (long) 60892;
	private final String adeIdentificador = "Solicitação Web";
	private final String codigoMotivo = "01";

	@Autowired
    private ConfirmarReservaClient confirmarReservaClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void confirmarReservaComSucesso() {
		log.info("Confirmar reserva com sucesso");

		// ade com status aguardando confirmacao
		adeNumero = (long) 60891;

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", confirmarReservaResponse.getMensagem());
		assertEquals("000", confirmarReservaResponse.getCodRetorno().getValue());
		assertTrue(confirmarReservaResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", confirmarReservaResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", confirmarReservaResponse.getBoleto().getValue().getCpf());
		assertEquals("M", confirmarReservaResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", confirmarReservaResponse.getBoleto().getValue().getEstadoCivil());
		String dataNascimento = "";
		if (confirmarReservaResponse.getBoleto().getValue().getDataNascimento() != null) {
		    dataNascimento = confirmarReservaResponse.getBoleto().getValue().getDataNascimento().toString();
		}
		assertEquals("1980-01-01-03:00", dataNascimento);
		assertEquals("1", confirmarReservaResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", confirmarReservaResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", confirmarReservaResponse.getBoleto().getValue().getConta().getValue());

		// verifica se alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void confirmarReservaQuePossuiRelacionamentoPorMotivoRenegociacao() {
		log.info("Confirmar reserva que possui relacionamento por motivo renegociação");

		// status aguardando confirmacao
		adeNumero = (long) 60896;
		// possui status 11 (aguardando liquidacao)
		final long adeNumeroAntigo = 60895;

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", confirmarReservaResponse.getMensagem());
		assertEquals("000", confirmarReservaResponse.getCodRetorno().getValue());
		assertTrue(confirmarReservaResponse.isSucesso());

		// verifica se alterou o status no banco para 4
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());

		// verifica se alterou o status do ade antigo para 8 (Liquidada)
		assertEquals("8", autDescontoService.getAde(Long.toString(adeNumeroAntigo)).getSadCodigo());
	}

	@Test
	public void confirmarReservaComMaisDeUmaConsignacao() {
		log.info("Confirmar reserva com mais de uma consignação");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 0, adeIdentificador, codigoMotivo);

		assertEquals("Mais de uma consignação encontrada", confirmarReservaResponse.getMensagem());
		assertEquals("245", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());
		assertTrue(confirmarReservaResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarConfirmarReservaDeConsignacaoNaoExistente() {
		log.info("Tentar confirmar reserva de consignação não existente");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 60800, "", codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", confirmarReservaResponse.getMensagem());
		assertEquals("294", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

	}

	@Test
	public void tentarConfirmarReservaDeConsignacaoJaConfirmada() {
		log.info("Tentar confirmar reserva de consignação já confirmada");

		// ade com status deferida
		adeNumero = (long) 60877;

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", confirmarReservaResponse.getMensagem());
		assertEquals("294", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaSemInformarMotivoDeOperacao() {
		log.info("Tentar confirmar reserva sem informar motivo operação");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, "");

		assertEquals("O motivo da operação deve ser informado.", confirmarReservaResponse.getMensagem());
		assertEquals("445", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaComMotivoDaOperacaoInvalido() {
		log.info("Tentar confirmar reserva com motivo da operação inválido");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, "001");

		assertEquals("O motivo da operação inválido.", confirmarReservaResponse.getMensagem());
		assertEquals("401", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaDeUsuarioInvalido() {
		log.info("Tentar confirmar reserva de usuário inválido");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", confirmarReservaResponse.getMensagem());
		assertEquals("358", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaDeSenhaInvalida() {
		log.info("Tentar confirmar reserva de senha inválida");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse(loginCsa.getLogin(),
				"ser12345", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", confirmarReservaResponse.getMensagem());
		assertEquals("358", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaComIPDeAcessoInvalido() {
		log.info("Tentar confirmar reserva com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("IP de acesso inválido", confirmarReservaResponse.getMensagem());
		assertEquals("362", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaSemInformarUsuario() {
		log.info("Tentar confirmar reserva sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            confirmarReservaClient.getResponse("", loginCsa.getSenha(), adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaSemInformarSenha() {
		log.info("Tentar confirmar reserva sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            confirmarReservaClient.getResponse(loginCsa.getLogin(), "", adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarReservaComUsuarioNaoSemPermissao() {
		log.info("Tentar confirmar reserva com usuário não sem permissão");

		final ConfirmarReservaResponse confirmarReservaResponse = confirmarReservaClient.getResponse("cse", "cse12345",
				adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação", confirmarReservaResponse.getMensagem());
		assertEquals("329", confirmarReservaResponse.getCodRetorno().getValue());
		assertFalse(confirmarReservaResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}
}
