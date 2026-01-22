package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.ConsultarConsignacaoClient;
import com.zetra.econsig.enomina.soap.config.SoapClientConfig;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.ConsultarConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsultarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final LoginInfo loginCse = LoginValues.cse1;
	private final String codServico = "001";
	private final String cpf = "092.459.399-79";
	private final int pagas = 0;
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private Long adeNumero = Long.valueOf(60922);
	private final String adeIdentificador = "";

	private final AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(
			SoapClientConfig.class);
	private final ConsultarConsignacaoClient consultarConsignacaoClient = annotationConfigApplicationContext
			.getBean(ConsultarConsignacaoClient.class);

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void consultarConsignacaoComUsuarioCSA() {
		log.info("Consultar consignação com usuario CSA");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
				codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarConsignacaoResponse.getMensagem());
		assertEquals("000", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(consultarConsignacaoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				String.valueOf(consultarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		assertEquals("041", consultarConsignacaoResponse.getBoleto().getValue().getIndice());
		assertEquals("csa2", consultarConsignacaoResponse.getBoleto().getValue().getResponsavel());
		assertEquals("EMPRÉSTIMO", consultarConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals("145", consultarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertTrue(
				consultarConsignacaoResponse.getBoleto().getValue().getDataReserva().toString().contains("2021-06-10"));
		assertEquals("50.0", String.valueOf(consultarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(10, consultarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(pagas, consultarConsignacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("Aguard. Confirmação", consultarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals(codServico, consultarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("1", consultarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("BANCO BRASIL", consultarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("csa2", consultarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertFalse(consultarConsignacaoResponse.getHistoricos().get(0).getTipo().isEmpty());
		assertFalse(consultarConsignacaoResponse.getHistoricos().get(0).getDescricao().isEmpty());
	}

	@Test
	public void consultarConsignacaoComUsuarioCSE() {
		log.info("Consultar consignação com usuário CSE");

		adeNumero = Long.valueOf(60915);

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCse.getLogin(), loginCse.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
				codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarConsignacaoResponse.getMensagem());
		assertEquals("000", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(consultarConsignacaoResponse.isSucesso());

		assertEquals(adeNumero.toString(),
				String.valueOf(consultarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		assertEquals("001", consultarConsignacaoResponse.getBoleto().getValue().getIndice());
		assertEquals("csa", consultarConsignacaoResponse.getBoleto().getValue().getResponsavel());
		assertEquals("EMPRÉSTIMO", consultarConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals("155", consultarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertTrue(
				consultarConsignacaoResponse.getBoleto().getValue().getDataReserva().toString().contains("2021-06-10"));
		assertEquals("198.0", String.valueOf(consultarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(5, consultarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(pagas, consultarConsignacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("Aguard. Liquidação Portabilidade",
				consultarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals(codServico, consultarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("15", consultarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("BANCO TREINAMENTO", consultarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("csa2", consultarConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertFalse(consultarConsignacaoResponse.getHistoricos().get(0).getTipo().isEmpty());
		assertFalse(consultarConsignacaoResponse.getHistoricos().get(0).getDescricao().isEmpty());
	}

	@Test
	public void consultarConsignacaoPorMatricula() {
		log.info("Consultar consignação por matricula");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, loginServidor.getLogin(),
				"", codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarConsignacaoResponse.getMensagem());
		assertEquals("000", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(consultarConsignacaoResponse.isSucesso());
		assertTrue(consultarConsignacaoResponse.getResumos().size() > 1);
	}

	@Test
	public void consultarConsignacaoPorCPF() {
		log.info("Consultar consignação por CPF");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", cpf, "", "");

		assertEquals("Operação realizada com sucesso.", consultarConsignacaoResponse.getMensagem());
		assertEquals("000", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(consultarConsignacaoResponse.isSucesso());
		assertTrue(consultarConsignacaoResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarConsultarConsignacaoComAdeIdentificadorInexistente() {
		log.info("Tentar consultar consignação com usuário inválido");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "Web teste", loginServidor.getLogin(), cpf,
				codOrgao, codEstabelecimento);

		assertEquals("Nenhuma consignação encontrada", consultarConsignacaoResponse.getMensagem());
		assertEquals("294", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComUsuarioInvalido() {
		log.info("Tentar consultar consignação com usuário inválido");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", consultarConsignacaoResponse.getMensagem());
		assertEquals("358", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComSenhaInvalida() {
		log.info("Tentar consultar consignação com senha inválida");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), "abc1234", adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", consultarConsignacaoResponse.getMensagem());
		assertEquals("358", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoSemInformarMatriculaOuCPFDoServidor() {
		log.info("Tentar consultar consignação sem informar matricula ou CPF do servidor");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), "", "", "", codOrgao, codEstabelecimento);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.",
				consultarConsignacaoResponse.getMensagem());
		assertEquals("305", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoParaServidorNaoCadastrado() {
		log.info("Tentar consultar consignação para servidor não cadastrado");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "859674", cpf, codOrgao,
				codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", consultarConsignacaoResponse.getMensagem());
		assertEquals("293", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComOrgaoInexistente() {
		log.info("Tentar consultar consignação com orgão inexistente");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
				"125", codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", consultarConsignacaoResponse.getMensagem());
		assertEquals("293", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComEstabelecimentoInexistente() {
		log.info("Tentar consultar consignação com estabelecimento inexistente");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
				codOrgao, "52");

		assertEquals("Nenhum servidor encontrado", consultarConsignacaoResponse.getMensagem());
		assertEquals("293", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComMatriculaInvalida() {
		log.info("Tentar consultar consignação com matricula inválida");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "12", "", codOrgao,
				codEstabelecimento);

		assertEquals("A matrícula informada é inválida.", consultarConsignacaoResponse.getMensagem());
		assertEquals("210", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar consultar consignação com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf, codOrgao,
				codEstabelecimento);

		assertEquals("IP de acesso inválido", consultarConsignacaoResponse.getMensagem());
		assertEquals("362", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarConsultarConsignacaoComAdeInexistente() {
		log.info("Tentar consultar consignação com ade inexistente");

		ConsultarConsignacaoResponse consultarConsignacaoResponse = consultarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(60800), adeIdentificador,
				loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento);

		assertEquals("Nenhuma consignação encontrada", consultarConsignacaoResponse.getMensagem());
		assertEquals("294", consultarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(consultarConsignacaoResponse.isSucesso());
	}

}
