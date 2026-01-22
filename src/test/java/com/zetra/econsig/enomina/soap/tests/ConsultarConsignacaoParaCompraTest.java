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
import com.zetra.econsig.enomina.soap.client.ConsultarConsignacaoParaCompraClient;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.compra.ConsultarConsignacaoParaCompraResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsultarConsignacaoParaCompraTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final String codServico = "001";
	private final String cpf = "092.459.399-79";
	private final int prazo = 8;
	private final Integer pagas = 0;
	private final String valorParcela = "152.0";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private final Long adeNumero = (long) 60911;
	private final String adeIdentificador = "";

	@Autowired
    private ConsultarConsignacaoParaCompraClient consultarConsignacaoParaCompraClient;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void consultarConsignacaoParaCompraComSucesso() {
		log.info("Consultar consignação para compra com sucesso");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), loginServidor.getSenha(), cpf, codServico, codOrgao,
						codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarConsignacaoResponse.getMensagem());
		assertEquals("000", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(consultarConsignacaoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				String.valueOf(consultarConsignacaoResponse.getResumos().get(0).getAdeNumero()));
		assertEquals("001", consultarConsignacaoResponse.getResumos().get(0).getIndice());
		assertEquals("csa", consultarConsignacaoResponse.getResumos().get(0).getResponsavel());
		assertEquals("EMPRÉSTIMO", consultarConsignacaoResponse.getResumos().get(0).getServico());
		assertEquals("155", consultarConsignacaoResponse.getResumos().get(0).getCodVerba());
		assertTrue(consultarConsignacaoResponse.getResumos().get(0).getDataReserva().toString().contains("2021-05-28"));
		assertEquals(valorParcela, String.valueOf(consultarConsignacaoResponse.getResumos().get(0).getValorParcela()));
		assertEquals(prazo, consultarConsignacaoResponse.getResumos().get(0).getPrazo());
		assertEquals(pagas, consultarConsignacaoResponse.getResumos().get(0).getPagas());
		assertEquals("Deferida", consultarConsignacaoResponse.getResumos().get(0).getSituacao());
		assertEquals(codServico, consultarConsignacaoResponse.getResumos().get(0).getServicoCodigo());
		assertEquals("4", consultarConsignacaoResponse.getResumos().get(0).getStatusCodigo());
		assertEquals("BANCO TREINAMENTO", consultarConsignacaoResponse.getResumos().get(0).getConsignataria());
		assertEquals("17167412007983",
				consultarConsignacaoResponse.getResumos().get(0).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void consultarConsignacaoParaCompraComVariasAdes() {
		log.info("Consultar consignação para compra com varias ades");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, adeIdentificador,
						loginServidor.getLogin(), loginServidor.getSenha(), cpf, codServico, codOrgao,
						codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarConsignacaoResponse.getMensagem());
		assertEquals("000", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(consultarConsignacaoResponse.isSucesso());
		assertTrue(consultarConsignacaoResponse.getResumos().size() > 1);

	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComAdeIdentificadorInexistente() {
		log.info("Tentar consultar consignação para compra com usuário inválido");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, "Web teste", loginServidor.getLogin(),
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("Nenhuma consignação encontrada", consultarConsignacaoResponse.getMensagem());
		assertEquals("294", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComUsuarioInvalido() {
		log.info("Tentar consultar consignação para compra com usuário inválido");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse("csa1", loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(),
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", consultarConsignacaoResponse.getMensagem());
		assertEquals("358", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComSenhaInvalida() {
		log.info("Tentar consultar consignação para compra com senha inválida");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), "abc1234", adeNumero, adeIdentificador, loginServidor.getLogin(),
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", consultarConsignacaoResponse.getMensagem());
		assertEquals("358", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComUsuarioSemPermissao() {
		log.info("Tentar consultar consignação para compra com usuário sem permissão");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse("cse", "cse12345", adeNumero, adeIdentificador, loginServidor.getLogin(),
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("O usuário não tem permissão para executar esta operação",
				consultarConsignacaoResponse.getMensagem());
		assertEquals("329", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraSemCodigoServico() {
		log.info("Tentar consultar consignação para compra sem codigo serviço");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            consultarConsignacaoParaCompraClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
                                                             loginServidor.getLogin(), loginServidor.getSenha(), cpf, "", codOrgao, codEstabelecimento);
        });
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComCodigoServicoInvalido() {
		log.info("Tentar consultar consignação para compra com código serviço inválido");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), loginServidor.getSenha(), cpf, "a", codOrgao, codEstabelecimento);

		assertEquals("Código da verba ou código do serviço inválido.", consultarConsignacaoResponse.getMensagem());
		assertEquals("232", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraSemInformarMatriculaOuCPFDoServidor() {
		log.info("Tentar consultar consignação para compra sem informar matricula ou CPF do servidor");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "",
						loginServidor.getSenha(), "", codServico, codOrgao, codEstabelecimento);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.",
				consultarConsignacaoResponse.getMensagem());
		assertEquals("305", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraSemInformarSenhaOuTokenDoServidor() {
		log.info("Tentar consultar consignação para compra sem informar senha ou token do servidor");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), "", cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("A senha ou token do servidor deve ser informada.", consultarConsignacaoResponse.getMensagem());
		assertEquals("211", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComSenhaDoServidorInvalido() {
		log.info("Tentar consultar consignação para compra com senha do servidor inválido");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), "abc145", cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("A senha de autorização do servidor não confere.", consultarConsignacaoResponse.getMensagem());
		assertEquals("363", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraParaServidorNaoCadastrado() {
		log.info("Tentar consultar consignação para compra para servidor não cadastrado");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "859674",
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", consultarConsignacaoResponse.getMensagem());
		assertEquals("293", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComOrgaoInexistente() {
		log.info("Tentar consultar consignação para compra com orgão inexistente");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), loginServidor.getSenha(), cpf, codServico, "125", codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", consultarConsignacaoResponse.getMensagem());
		assertEquals("293", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComEstabelecimentoInexistente() {
		log.info("Tentar consultar consignação para compra com estabelecimento inexistente");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), loginServidor.getSenha(), cpf, codServico, codOrgao, "52");

		assertEquals("Nenhum servidor encontrado", consultarConsignacaoResponse.getMensagem());
		assertEquals("293", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComMatriculaInvalida() {
		log.info("Tentar consultar consignação para compra com matricula inválida");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "12",
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("A matrícula informada é inválida.", consultarConsignacaoResponse.getMensagem());
		assertEquals("210", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComIPDeAcessoInvalido() {
		log.info("Tentar consultar consignação para compra com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse("csa", loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(),
						loginServidor.getSenha(), cpf, codServico, codOrgao, codEstabelecimento);

		assertEquals("IP de acesso inválido", consultarConsignacaoResponse.getMensagem());
		assertEquals("362", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaCompraComAdeInexistente() {
		log.info("Tentar consultar consignação para compra com ade inexistente");

		final ConsultarConsignacaoParaCompraResponse consultarConsignacaoResponse = consultarConsignacaoParaCompraClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 60800, adeIdentificador,
						loginServidor.getLogin(), loginServidor.getSenha(), cpf, codServico, codOrgao,
						codEstabelecimento);

		assertEquals("Nenhuma consignação encontrada", consultarConsignacaoResponse.getMensagem());
		assertEquals("294", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

}
