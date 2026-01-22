package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.ListarSolicitacaoClient;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.ListarSolicitacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ListarSolicitacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final LoginInfo loginCse = LoginValues.cse1;
	private final String codServico = "001";
	private final String cpf = "092.459.399-79";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private Long adeNumero = Long.valueOf(60897);
	private final String adeIdentificador = "Solicitação Web";

	@Autowired
    private ListarSolicitacaoClient listarSolicitacaoClient;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void listarSolicitacaoComSucesso() {
		log.info("Listar solicitação com sucesso");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), "", "", "", "", "");

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());
		assertTrue(listarSolicitacaoResponse.getSolicitacoes().size() > 1);
	}

	@Test
	public void listarSolicitacaoDeConsignacaoComStatusSolicitacao() {
		log.info("Listar solicitação de consignação com status solicitacao");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());

		assertTrue(
				listarSolicitacaoResponse.getSolicitacoes().get(0).getDataReserva().toString().contains("2021-05-24"));
		assertEquals(adeNumero.toString(),
				String.valueOf(listarSolicitacaoResponse.getSolicitacoes().get(0).getAdeNumero()));
		assertEquals("Sr. BOB da Silva Shawn", listarSolicitacaoResponse.getSolicitacoes().get(0).getServidor());
		assertEquals("31-32659874", listarSolicitacaoResponse.getSolicitacoes().get(0).getTelefone());
		assertEquals(cpf, listarSolicitacaoResponse.getSolicitacoes().get(0).getCpf());
		assertEquals(loginServidor.getLogin(),
				String.valueOf(listarSolicitacaoResponse.getSolicitacoes().get(0).getMatricula()));
		assertEquals("151.0", String.valueOf(listarSolicitacaoResponse.getSolicitacoes().get(0).getValorParcela()));
		assertEquals(10, listarSolicitacaoResponse.getSolicitacoes().get(0).getPrazo());
		assertEquals("EMPRÉSTIMO", listarSolicitacaoResponse.getSolicitacoes().get(0).getServico());
		assertEquals("145", listarSolicitacaoResponse.getSolicitacoes().get(0).getCodVerba());
		assertEquals("2000.0", listarSolicitacaoResponse.getSolicitacoes().get(0).getValorLiberado().toString());
		assertEquals("1.1", listarSolicitacaoResponse.getSolicitacoes().get(0).getTaxaJuros().toString());
		assertEquals("Carlota Joaquina 21.346.414/0001-47",
				listarSolicitacaoResponse.getSolicitacoes().get(0).getEstabelecimento());
		assertEquals("Carlota Joaquina 21.346.414/0001-47",
				listarSolicitacaoResponse.getSolicitacoes().get(0).getOrgao());
		assertEquals(codEstabelecimento, listarSolicitacaoResponse.getSolicitacoes().get(0).getEstabelecimentoCodigo());
		assertEquals(codOrgao, listarSolicitacaoResponse.getSolicitacoes().get(0).getOrgaoCodigo());
		assertEquals(codServico, listarSolicitacaoResponse.getSolicitacoes().get(0).getServicoCodigo());
	}

	@Test
	public void tentarListarSolicitacaoDeConsignacaoComStatusDeferida() {
		log.info("Listar solicitação de consignação com status deferida");

		adeNumero = Long.valueOf(14);

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Nenhuma solicitação encontrada.", listarSolicitacaoResponse.getMensagem());
		assertEquals("296", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComUsuarioSemPermissao() {
		log.info("tentar listar solicitação com usuário sem permissao");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCse.getLogin(),
				loginCse.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("O usuário não tem permissão para executar esta operação",
				listarSolicitacaoResponse.getMensagem());
		assertEquals("329", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoDeConsignacaoComStatusAguardandoInformacaoSaldoDevedor() {
		log.info("tentar listar solicitação de consignacao com status aguardando informacao saldo devedor");

		adeNumero = Long.valueOf(60977);

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Nenhuma solicitação encontrada.", listarSolicitacaoResponse.getMensagem());
		assertEquals("296", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void listarSolicitacaoPorMatricula() {
		log.info("Listar solicitação por matricula");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), "", loginServidor.getLogin(), "", codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());
		assertTrue(listarSolicitacaoResponse.getSolicitacoes().size() > 1);
	}

	@Test
	public void listarSolicitacaoPorAdeIdentificador() {
		log.info("Listar solicitação por adeIdentificador");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", "", "", "");

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());
		assertTrue(listarSolicitacaoResponse.getSolicitacoes().size() > 1);
	}

	@Test
	public void listarSolicitacaoPorCPF() {
		log.info("Listar solicitação por CPF");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), "", "", cpf, "", "");

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());
		assertTrue(listarSolicitacaoResponse.getSolicitacoes().size() > 1);
	}

	@Test
	public void listarSolicitacaoPorOrgao() {
		log.info("Listar solicitação por orgao");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), "", "", cpf, codOrgao, "");

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());
		assertTrue(listarSolicitacaoResponse.getSolicitacoes().size() > 1);
	}

	@Test
	public void listarSolicitacaoPorEstabelecimento() {
		log.info("Listar solicitação por estabelecimento");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), "", "", cpf, "", codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", listarSolicitacaoResponse.getMensagem());
		assertEquals("000", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(listarSolicitacaoResponse.isSucesso());
		assertTrue(listarSolicitacaoResponse.getSolicitacoes().size() > 1);
	}

	@Test
	public void tentarListarSolicitacaoComAdeIdentificadorInexistente() {
		log.info("Tentar listar solicitação com usuário inválido");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "Web teste", loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Nenhuma solicitação encontrada.", listarSolicitacaoResponse.getMensagem());
		assertEquals("296", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComUsuarioInvalido() {
		log.info("Tentar listar solicitação com usuário inválido");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", listarSolicitacaoResponse.getMensagem());
		assertEquals("358", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComSenhaInvalida() {
		log.info("Tentar listar solicitação com senha inválida");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				"abc1234", adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", listarSolicitacaoResponse.getMensagem());
		assertEquals("358", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoParaServidorNaoCadastrado() {
		log.info("Tentar listar solicitação para servidor não cadastrado");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, "859674", cpf, codOrgao, codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", listarSolicitacaoResponse.getMensagem());
		assertEquals("293", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComOrgaoInexistente() {
		log.info("Tentar listar solicitação com orgão inexistente");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, "125",
				codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", listarSolicitacaoResponse.getMensagem());
		assertEquals("293", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComEstabelecimentoInexistente() {
		log.info("Tentar listar solicitação com estabelecimento inexistente");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao, "52");

		assertEquals("Nenhum servidor encontrado", listarSolicitacaoResponse.getMensagem());
		assertEquals("293", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComMatriculaInvalida() {
		log.info("Tentar listar solicitação com matricula inválida");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0), "", "12", "", "", "");

		assertEquals("Nenhum servidor encontrado", listarSolicitacaoResponse.getMensagem());
		assertEquals("293", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComIPDeAcessoInvalido() {
		log.info("Tentar listar solicitação com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("IP de acesso inválido", listarSolicitacaoResponse.getMensagem());
		assertEquals("362", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarListarSolicitacaoComAdeNumeroInexistente() {
		log.info("Tentar listar solicitação com ade inexistente");

		ListarSolicitacaoResponse listarSolicitacaoResponse = listarSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(60800), adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Nenhuma solicitação encontrada.", listarSolicitacaoResponse.getMensagem());
		assertEquals("296", listarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(listarSolicitacaoResponse.isSucesso());
	}
}
