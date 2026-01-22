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
import com.zetra.econsig.enomina.soap.client.ListarDadoConsignacaoClient;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.ListarDadoConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ListarDadoConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final Long adeNumero = (long) 60994;
	private final String adeIdentificador = "";
	private final String dadoCodigo = "";

	@Autowired
    private ListarDadoConsignacaoClient listarDadoConsignacaoClient;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@BeforeEach
	public void setUp() {
		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();
	}

	@Test
	public void listarDadoConsignacaoComDadosServidor() {
		log.info("Listar dado consignacao com dados servidor");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
				CodedValues.TDA_SDV_EMAIL_SERVIDOR);

		assertEquals("Operação realizada com sucesso.", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("000", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(listarDadoConsignacaoResponse.isSucesso());

		assertEquals(CodedValues.TDA_SDV_EMAIL_SERVIDOR, listarDadoConsignacaoResponse.getDados().get(0).getCodigo());
		assertEquals("E-mail do Servidor - Aprovação Saldo Devedor",
				listarDadoConsignacaoResponse.getDados().get(0).getDescricao());
		assertEquals("servidor@gmail.com", listarDadoConsignacaoResponse.getDados().get(0).getValor());
	}

	@Test
	public void listarDadoConsignacaoComDadosAutorizacaoDesconto() {
		log.info("Listar dado consignacao com dados autorização desconto");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
				CodedValues.TDA_SDV_TEL_SERVIDOR);

		assertEquals("Operação realizada com sucesso.", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("000", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(listarDadoConsignacaoResponse.isSucesso());

		assertEquals(CodedValues.TDA_SDV_TEL_SERVIDOR, listarDadoConsignacaoResponse.getDados().get(0).getCodigo());
		assertEquals("Telefone do Servidor - Saldo Devedor",
				listarDadoConsignacaoResponse.getDados().get(0).getDescricao());
		assertEquals("3132549632", listarDadoConsignacaoResponse.getDados().get(0).getValor());
	}

	@Test
	public void listarDadoConsignacaoComDadosAutorizacaoDescontoEDadosServidor() {
		log.info("Listar dado consignacao com dados autorização desconto e dados servidor");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigo);

		assertEquals("Operação realizada com sucesso.", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("000", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(listarDadoConsignacaoResponse.isSucesso());

		assertEquals(CodedValues.TDA_SOLICITACAO_TEL_SERVIDOR,
				listarDadoConsignacaoResponse.getDados().get(0).getCodigo());
		assertEquals("Telefone do Servidor - Solicitação",
				listarDadoConsignacaoResponse.getDados().get(0).getDescricao());
		assertEquals("31987457485", listarDadoConsignacaoResponse.getDados().get(0).getValor());

		assertEquals(CodedValues.TDA_SDV_TEL_SERVIDOR, listarDadoConsignacaoResponse.getDados().get(1).getCodigo());
		assertEquals("Telefone do Servidor - Saldo Devedor",
				listarDadoConsignacaoResponse.getDados().get(1).getDescricao());
		assertEquals("3132549632", listarDadoConsignacaoResponse.getDados().get(1).getValor());

		assertEquals(CodedValues.TDA_SDV_EMAIL_SERVIDOR, listarDadoConsignacaoResponse.getDados().get(2).getCodigo());
		assertEquals("E-mail do Servidor - Aprovação Saldo Devedor",
				listarDadoConsignacaoResponse.getDados().get(2).getDescricao());
		assertEquals("servidor@gmail.com", listarDadoConsignacaoResponse.getDados().get(2).getValor());
	}

	@Test
	public void listarDadoConsignacaoComMaisDeUmaConsignacao() {
		log.info("Listar dado consignacao com mais de uma consignação");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, "Solicitação Web", dadoCodigo);

		assertEquals("Mais de uma consignação encontrada", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("245", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());
		assertTrue(listarDadoConsignacaoResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarListarDadoConsignacaoSemInformarAdeNumeroEAdeIdentificador() {
		log.info("Tentar listar dado consignacao sem informar adeNumero e adeIdentificador");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, adeIdentificador, dadoCodigo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				listarDadoConsignacaoResponse.getMensagem());
		assertEquals("322", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarDadoConsignacaoDeConsignacaoNaoExistente() {
		log.info("Tentar listar dado consignacao de consignação não existente");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), (long) 60800, adeIdentificador, dadoCodigo);

		assertEquals("Nenhuma consignação encontrada", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("294", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarListarDadoConsignacaoComDadoCodigoInexistente() {
		log.info("Tentar listar dado consignacao com dado codigo inexistente");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "001");

		assertEquals("Operação realizada com sucesso.", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("000", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(listarDadoConsignacaoResponse.isSucesso());

		assertTrue(listarDadoConsignacaoResponse.getDados().isEmpty());
	}

	@Test
	public void tentarListarDadoConsignacaoComUsuarioInvalido() {
		log.info("Tentar listar dado consignacao com usuário inválido");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigo);

		assertEquals("Usuário ou senha inválidos", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("358", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarDadoConsignacaoComSenhaInvalida() {
		log.info("Tentar listar dado consignacao com senha inválida");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient
				.getResponse(loginCsa.getLogin(), "ser12345", adeNumero, adeIdentificador, dadoCodigo);

		assertEquals("Usuário ou senha inválidos", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("358", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarDadoConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar listar dado consignacao com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigo);

		assertEquals("IP de acesso inválido", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("362", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarDadoConsignacaoSemInformarUsuario() {
		log.info("Tentar listar dado consignacao sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            listarDadoConsignacaoClient.getResponse("", loginCsa.getSenha(), adeNumero, adeIdentificador, dadoCodigo);
        });
	}

	@Test
	public void tentarListarDadoConsignacaoSemInformarSenha() {
		log.info("Tentar listar dado consignacao sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            listarDadoConsignacaoClient.getResponse(loginCsa.getLogin(), "", adeNumero, adeIdentificador, dadoCodigo);
        });
	}

	@Test
	public void tentarListarDadoConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar listar dado consignacao com usuário sem permissão");

		final ListarDadoConsignacaoResponse listarDadoConsignacaoResponse = listarDadoConsignacaoClient.getResponse("cse",
				"cse12345", adeNumero, adeIdentificador, dadoCodigo);

		assertEquals("O usuário não tem permissão para executar esta operação", listarDadoConsignacaoResponse.getMensagem());
		assertEquals("329", listarDadoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(listarDadoConsignacaoResponse.isSucesso());
	}
}
