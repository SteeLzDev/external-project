package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.IncluirDadoConsignacaoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.IncluirDadoConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IncluirDadoConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private String adeNumero = "60909";
	private final String adeIdentificador = "Solicitação Web";
	private final String dadoCodigoSaldoDevedor = "11";
	private final String dadoCodigoSolicitacao = "25";
	private final String dadoValor = "3189563652";

	@Autowired
    private IncluirDadoConsignacaoClient incluirDadoConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void alterarDadoConsignacaoTelefoneServidorSolicitacaoComSucesso() {
		log.info("Alterar dado consignação telefone servidor solicitação com sucesso");

		adeNumero = "60906";

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSolicitacao,
				dadoValor);

		assertEquals("Operação realizada com sucesso.", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("000", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(incluirDadoConsignacaoResponse.isSucesso());
		assertEquals(dadoCodigoSolicitacao, incluirDadoConsignacaoResponse.getDados().get(0).getCodigo());
		assertEquals("Telefone do Servidor - Solicitação",
				incluirDadoConsignacaoResponse.getDados().get(0).getDescricao());
		assertEquals(dadoValor, incluirDadoConsignacaoResponse.getDados().get(0).getValor());

		// verifica que incluiu no banco de dados
		assertEquals(dadoValor, autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao).getDadValor());
	}

	@Test
	public void incluirDadoConsignacaoTelefoneServidorSaldoDevedorComSucesso() {
		log.info("Incluir dado consignação telefone servidor saldo devedor com sucesso");

		adeNumero = "60897";

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSaldoDevedor,
				dadoValor);

		assertEquals("Operação realizada com sucesso.", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("000", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(incluirDadoConsignacaoResponse.isSucesso());
		assertEquals(dadoCodigoSaldoDevedor, incluirDadoConsignacaoResponse.getDados().get(1).getCodigo());
		assertEquals("Telefone do Servidor - Saldo Devedor",
				incluirDadoConsignacaoResponse.getDados().get(1).getDescricao());
		assertEquals(dadoValor, incluirDadoConsignacaoResponse.getDados().get(1).getValor());

		// verifica que incluiu no banco de dados
		assertEquals(dadoValor,
				autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSaldoDevedor).getDadValor());
	}

	@Test
	public void incluirDadoConsignacaoTelefoneServidorSolicitacaoESaldoDevedorComSucesso() {
		log.info("Incluir dado consignação telefone servidor solicitação e saldo devedor com sucesso");

		adeNumero = "60907";

		incluirDadoConsignacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
				dadoCodigoSolicitacao, dadoValor);

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSaldoDevedor,
				"3278526352");

		assertEquals("Operação realizada com sucesso.", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("000", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(incluirDadoConsignacaoResponse.isSucesso());
		assertEquals(dadoCodigoSolicitacao, incluirDadoConsignacaoResponse.getDados().get(0).getCodigo());
		assertEquals("Telefone do Servidor - Solicitação",
				incluirDadoConsignacaoResponse.getDados().get(0).getDescricao());
		assertEquals(dadoValor, incluirDadoConsignacaoResponse.getDados().get(0).getValor());
		assertEquals(dadoCodigoSaldoDevedor, incluirDadoConsignacaoResponse.getDados().get(1).getCodigo());
		assertEquals("Telefone do Servidor - Saldo Devedor",
				incluirDadoConsignacaoResponse.getDados().get(1).getDescricao());
		assertEquals("3278526352", incluirDadoConsignacaoResponse.getDados().get(1).getValor());

		// verifica que incluiu no banco de dados
		assertEquals(dadoValor, autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao).getDadValor());
		assertEquals("3278526352",
				autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSaldoDevedor).getDadValor());
	}

	@Test
	public void tentarIncluirDadoVariasConsignacoes() {
		log.info("Tentar incluir dado varias consignações");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), "", adeIdentificador, dadoCodigoSolicitacao, dadoValor);

		assertEquals("Mais de uma consignação encontrada", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("245", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarIncluirDadoConsignacaoSemInformaAdeNumeroEAdeIdentificador() {
		log.info("Tentar incluir dado consignação sem informa adeNumero e adeIdentificador");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "", "", dadoCodigoSaldoDevedor, dadoValor);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("322", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarIncluirDadoConsignacaoSemInformarDadoConsignacao() {
		log.info("Tentar incluir dado consignação sem informar dado codigo");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            incluirDadoConsignacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "", dadoValor);
        });
	}

	@Test
	public void tentarIncluirDadoConsignacaoComDadoConsignacaoInvalido() {
		log.info("Tentar incluir dado consignação com dado consignação inválido");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "1", dadoValor);

		assertEquals("Tipo de dado de consignação inválido.", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("463", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarIncluirDadoConsignacaoQueNaoExistente() {
		log.info("Tentar incluir dado consignação que não existente");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), "60800", adeIdentificador, dadoCodigoSolicitacao, dadoValor);

		assertEquals("Nenhuma consignação encontrada", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("294", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarIncluirDadoConsignacaoCancelada() {
		log.info("Tentar incluir dado consignação cancelada");

		// ade com status cancelada
		adeNumero = "60903";

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSaldoDevedor,
				dadoValor);

		assertEquals("Nenhuma consignação encontrada", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("294", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSaldoDevedor));
	}

	@Test
	public void tentarIncluirDadoConsignacaoComUsuarioInvalido() {
		log.info("Tentar incluir dado consignação com usuário inválido");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSolicitacao, dadoValor);

		assertEquals("Usuário ou senha inválidos", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("358", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao));
	}

	@Test
	public void tentarIncluirDadoConsignacaoDeSenhaInvalida() {
		log.info("Tentar incluir dado consignação de senha inválida");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), "ser12345", adeNumero, adeIdentificador, dadoCodigoSolicitacao, dadoValor);

		assertEquals("Usuário ou senha inválidos", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("358", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao));
	}

	@Test
	public void tentarIncluirDadoConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar incluir dado consignação com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSolicitacao, dadoValor);

		assertEquals("IP de acesso inválido", incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("362", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao));
	}

	@Test
	public void tentarIncluirDadoConsignacaoSemInformarUsuario() {
		log.info("Tentar incluir dado consignação sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            incluirDadoConsignacaoClient.getResponse("", loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigoSolicitacao, dadoValor);
        });

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao));
	}

	@Test
	public void tentarIncluirDadoConsignacaoSemInformarSenha() {
		log.info("Tentar incluir dado consignação sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            incluirDadoConsignacaoClient.getResponse(loginCsa.getLogin(), "", adeNumero, adeIdentificador, dadoCodigoSolicitacao, dadoValor);
        });

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao));
	}

	@Test
	public void tentarIncluirDadoConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar incluir dado consignação com usuário sem permissão");

		final IncluirDadoConsignacaoResponse incluirDadoConsignacaoResponse = incluirDadoConsignacaoClient.getResponse("cse",
				"cse12345", adeNumero, adeIdentificador, dadoCodigoSolicitacao, dadoValor);

		assertEquals("O usuário não tem permissão para executar esta operação",
				incluirDadoConsignacaoResponse.getMensagem());
		assertEquals("329", incluirDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirDadoConsignacaoResponse.isSucesso());

		// verifica que não incluiu no banco de dados
		assertNull(autDescontoService.getDadosAutDesconto(adeNumero, dadoCodigoSolicitacao));
	}
}
