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
import com.zetra.econsig.enomina.soap.client.AutorizarReservaClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.AutorizarReservaResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AutorizarReservaTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	Long adeNumero = (long) 60886;
	private final String adeIdentificador = "";
	private final String codigoMotivo = "01";
	private String svcCodigoEmprestimo;

	@Autowired
    private AutorizarReservaClient autorizarReservaClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@BeforeEach
	public void setUp() throws Exception {
		svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");
		// alterar parametro
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR, "");
	}

	@Test
	public void autorizarReservaComStatusAguardandoDeferimento() {
		log.info("Autorizar reserva com status aguardando deferimento");

		// ade com status aguardando deferimento
		adeNumero = (long) 60885;

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, "", codigoMotivo);

		assertEquals("Operação realizada com sucesso.", autorizarReservaResponse.getMensagem());
		assertEquals("000", autorizarReservaResponse.getCodRetorno().getValue());
		assertTrue(autorizarReservaResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", autorizarReservaResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", autorizarReservaResponse.getBoleto().getValue().getCpf());
		assertEquals("M", autorizarReservaResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", autorizarReservaResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", autorizarReservaResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", autorizarReservaResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", autorizarReservaResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", autorizarReservaResponse.getBoleto().getValue().getConta().getValue());

		// verifica se alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void autorizarReservaComStatusAguardandoConfirmacao() {
		log.info("Autorizar reserva com status aguardando confirmação");

		// ade com status aguardando confirmacao
		adeNumero = (long) 60887;

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador,
				codigoMotivo);

		assertEquals("Operação realizada com sucesso.", autorizarReservaResponse.getMensagem());
		assertEquals("000", autorizarReservaResponse.getCodRetorno().getValue());
		assertTrue(autorizarReservaResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", autorizarReservaResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", autorizarReservaResponse.getBoleto().getValue().getCpf());
		assertEquals("M", autorizarReservaResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", autorizarReservaResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", autorizarReservaResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", autorizarReservaResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", autorizarReservaResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", autorizarReservaResponse.getBoleto().getValue().getConta().getValue());

		// verifica se alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void autorizarReservaComMaisDeUmaConsignacao() {
		log.info("Autorizar reserva com mais de uma consignação");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), (long) 0,
				"Solicitação Web", codigoMotivo);

		assertEquals("Mais de uma consignação encontrada", autorizarReservaResponse.getMensagem());
		assertEquals("245", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());
		assertTrue(autorizarReservaResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarAutorizarReservaSemInformarAdeOuIdentificador() {
		log.info("Tentar autorizar reserva sem informar ADE ou identificador");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), (long) 0, "",
				codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.", autorizarReservaResponse.getMensagem());
		assertEquals("322", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaSemInformarSenhaOuTokenDoServidor() {
		log.info("Tentar autorizar reserva sem informar senha ou token do servidor");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), "", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("A senha ou token do servidor deve ser informada.", autorizarReservaResponse.getMensagem());
		assertEquals("211", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComUsuarioInvalido() {
		log.info("Tentar autorizar reserva com usuário inválido");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse("csa1",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador,
				codigoMotivo);

		assertEquals("Usuário ou senha inválidos", autorizarReservaResponse.getMensagem());
		assertEquals("358", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComSenhaInvalida() {
		log.info("Tentar autorizar reserva com senha inválida");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				"cse12", loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", autorizarReservaResponse.getMensagem());
		assertEquals("358", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaSemInformarUsuario() {
		log.info("Tentar autorizar reserva sem informar usuário");

		Assertions.assertThrows(SoapFaultClientException.class, () -> {
		    autorizarReservaClient.getResponse("", loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(),
		                                       adeNumero, adeIdentificador, codigoMotivo);
		});

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaSemInformarSenha() {
		log.info("Tentar autorizar reserva sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            autorizarReservaClient.getResponse(loginCsa.getLogin(), "", loginServidor.getLogin(), loginServidor.getSenha(),
                                               adeNumero, adeIdentificador, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComConsignacaoNaoCadastrado() {
		log.info("Tentar autorizar reserva com consignação não cadastrado");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), (long) 68000,
				adeIdentificador, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", autorizarReservaResponse.getMensagem());
		assertEquals("294", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComAdeIdentificadorDiferenteDoCadastrado() {
		log.info("Tentar autorizar reserva com ADE identificador diferente do cadastrado");

		// ade com status aguardando confirmacao
		adeNumero = (long) 60888;

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, "web",
				codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", autorizarReservaResponse.getMensagem());
		assertEquals("294", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComTipoDeMotivoDaOperacaoNaoExistente() {
		log.info("Tentar autorizar reserva com tipo de motivo da operação não existente");

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador,
				"Outros");

		assertEquals("Tipo de motivo da operação não encontrado.", autorizarReservaResponse.getMensagem());
		assertEquals("356", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaJaDeferida() {
		log.info("Tentar autorizar reserva já deferida");

		// ade com status deferida
		adeNumero = (long) 60876;

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador,
				codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", autorizarReservaResponse.getMensagem());
		assertEquals("294", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComIPDeAcessoInvalido() {
		log.info("Autorizar reserva com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse("csa",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador,
				codigoMotivo);

		assertEquals("IP de acesso inválido", autorizarReservaResponse.getMensagem());
		assertEquals("362", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComStatusSolicitacao() {
		log.info("Tentar autorizar reserva com status solicitação");

		// ade com status solicitacao
		adeNumero = (long) 60879;

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, adeIdentificador,
				codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", autorizarReservaResponse.getMensagem());
		assertEquals("294", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarAutorizarReservaComSenhaServidorIncorreta() {
		log.info("Tentar autorizar reserva com senha servidor incorreta");

		// ade com status aguardando confirmacao
		adeNumero = (long) 60888;

		final AutorizarReservaResponse autorizarReservaResponse = autorizarReservaClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), "ser12", adeNumero, adeIdentificador, codigoMotivo);

		assertEquals("A senha de autorização do servidor não confere.", autorizarReservaResponse.getMensagem());
		assertEquals("363", autorizarReservaResponse.getCodRetorno().getValue());
		assertFalse(autorizarReservaResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

}
