package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.DetalharConsultaConsignacaoClient;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.DetalharConsultaConsignacaoResponse;
import com.zetra.econsig.soap.SituacaoContrato;
import com.zetra.econsig.soap.SituacaoServidor;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DetalharConsultaConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final LoginInfo loginCse = LoginValues.cse1;
	private final String codServico = "001";
	private final String cpf = "092.459.399-79";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private final Long adeNumero = Long.valueOf(60922);
	private final String adeIdentificador = "";
	private final String servicoCodigo = "001";
	private final String codigoVerba = "145";
	private final String periodo = "";
	private final String dataInclusao = "";
	private final String dataInclusaoFim = "";
	private final String integraFolha = "";
	private final String codigoMargem = "";
	private final String indice = "";
	private final String correspondenteCodigo = "";
	private final SituacaoContrato situacaoContrato = new SituacaoContrato();
	private final SituacaoServidor situacaoServidor = new SituacaoServidor();

	@Autowired
    private DetalharConsultaConsignacaoClient detalharConsultaConsignacaoClient;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@BeforeEach
	public void setUp() {
		situacaoContrato.setSolicitado(false);
		situacaoContrato.setAguardandoConfirmacao(true);
		situacaoContrato.setAguardandoDeferimento(false);
		situacaoContrato.setIndeferida(false);
		situacaoContrato.setDeferida(false);
		situacaoContrato.setEmAndamento(false);
		situacaoContrato.setSuspensa(false);
		situacaoContrato.setCancelada(false);
		situacaoContrato.setLiquidada(false);
		situacaoContrato.setConcluido(false);
		situacaoContrato.setSuspensaPeloConsignante(false);
		situacaoContrato.setAguardandoLiquidacao(false);
		situacaoContrato.setEstoque(false);
		situacaoContrato.setEstoqueNaoLiberado(false);
		situacaoContrato.setEmCarencia(false);
		situacaoContrato.setAguardandoLiquidacaoCompra(false);
		situacaoContrato.setEstoqueMensal(false);

		situacaoServidor.setAtivo(true);
		situacaoServidor.setBloqueado(false);
		situacaoServidor.setExcluido(false);
		situacaoServidor.setFalecido(false);
		situacaoServidor.setPendente(false);
	}

	@Test
	public void detalharConsultaConsignacaoComSucessoDadosPessoais() {
		log.info("Detalhar consulta consignação com sucesso dados pessoais");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento, servicoCodigo, codigoVerba,
						situacaoContrato, situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());

		assertEquals("Sr. BOB da Silva Shawn",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, detalharConsultaConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", detalharConsultaConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("1980-01-01-03:00",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("Solteiro(a)", detalharConsultaConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("31-32659874", detalharConsultaConsignacaoResponse.getBoleto().getValue().getTelefone());
		assertEquals(loginServidor.getLogin(),
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("36", detalharConsultaConsignacaoResponse.getBoleto().getValue().getPrazoServidor().toString());
		assertEquals("csa2", detalharConsultaConsignacaoResponse.getBoleto().getValue().getResponsavel());
		assertEquals("1234", detalharConsultaConsignacaoResponse.getBoleto().getValue().getEmissorIdentidade()
				.getValue().toString());
		assertEquals("Vitoria",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getCidadeNascimento().getValue().toString());
		assertEquals("BRASILEIRO",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getNacionalidade().getValue().toString());
		assertEquals("32-987457485",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getCelular().getValue().toString());
		assertEquals("291500.5",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getSalario().getValue().toString());
		assertEquals("1", detalharConsultaConsignacaoResponse.getBoleto().getValue().getBanco().getValue().toString());
		assertEquals("1111",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getAgencia().getValue().toString());
		assertEquals("111111",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getConta().getValue().toString());
		assertEquals("1",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getQtdFilhos().getValue().toString());
		assertEquals("csa2", detalharConsultaConsignacaoResponse.getHistoricos().get(0).getResponsavel());
		assertFalse(detalharConsultaConsignacaoResponse.getHistoricos().get(0).getTipo().isEmpty());
		assertFalse(detalharConsultaConsignacaoResponse.getHistoricos().get(0).getDescricao().isEmpty());
	}

	@Test
	public void detalharConsultaConsignacaoComSucessoDadosAde() {
		log.info("Detalhar consulta consignação com sucesso dados ade");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento, servicoCodigo, codigoVerba,
						situacaoContrato, situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());

		assertEquals("Carlota Joaquina 21.346.414/0001-47",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("Carlota Joaquina 21.346.414/0001-47",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getOrgao());
		assertEquals("213464140",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", detalharConsultaConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("145", detalharConsultaConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("-1", detalharConsultaConsignacaoResponse.getBoleto().getValue().getRanking().toString());
		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getBoleto().getValue().getServico().toString());
		assertEquals("1000.0",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertTrue(detalharConsultaConsignacaoResponse.getBoleto().getValue().getDataReserva().toString()
				.contains("2021-06-10"));
		assertEquals("2021-06-01-03:00",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getDataInicial().toString());
		assertEquals("2022-03-01-03:00",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getDataFinal().toString());
		assertEquals("50.0",
				String.valueOf(detalharConsultaConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(adeNumero.toString(),
				String.valueOf(detalharConsultaConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		assertEquals("041", detalharConsultaConsignacaoResponse.getBoleto().getValue().getIndice());
		assertEquals(10, detalharConsultaConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, detalharConsultaConsignacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("1.1",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getTaxaJuros().getValue().toString());
		assertEquals("Aguard. Confirmação", detalharConsultaConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF,
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("001", detalharConsultaConsignacaoResponse.getBoleto().getValue().getConsignatariaCodigo()
				.getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorMatricula() {
		log.info("Detalhar consulta consignação por matricula");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador,
						loginServidor.getLogin(), "", codOrgao, codEstabelecimento, "", "", situacaoContrato,
						situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(0).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(0).getCodVerba());
		assertEquals("Aguard. Confirmação", detalharConsultaConsignacaoResponse.getResumos().get(0).getSituacao());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(0).getServicoCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF,
				detalharConsultaConsignacaoResponse.getResumos().get(0).getStatusCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(0).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(0).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorCPF() {
		log.info("Detalhar consulta consignação por cpf");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", cpf, "",
						"", servicoCodigo, "", situacaoContrato, situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals("Aguard. Confirmação", detalharConsultaConsignacaoResponse.getResumos().get(1).getSituacao());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF,
				detalharConsultaConsignacaoResponse.getResumos().get(1).getStatusCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorSituacaoContratoCancelada() {
		log.info("Detalhar consulta consignação por situacao contrato cancelada");

		situacaoContrato.setAguardandoConfirmacao(false);
		situacaoContrato.setCancelada(true);

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", cpf, "",
						"", servicoCodigo, "", situacaoContrato, situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals("Cancelada", detalharConsultaConsignacaoResponse.getResumos().get(1).getSituacao());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals(CodedValues.SAD_CANCELADA,
				detalharConsultaConsignacaoResponse.getResumos().get(1).getStatusCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorSituacaoContratoDeferidaEEmAndamento() {
		log.info("Detalhar consulta consignação por situacao contrato deferida e em andamento");

		situacaoContrato.setAguardandoConfirmacao(false);
		situacaoContrato.setDeferida(true);
		situacaoContrato.setEmAndamento(true);

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", cpf, "",
						"", servicoCodigo, "", situacaoContrato, situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals("Deferida", detalharConsultaConsignacaoResponse.getResumos().get(1).getSituacao());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals(CodedValues.SAD_DEFERIDA,
				detalharConsultaConsignacaoResponse.getResumos().get(1).getStatusCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorSituacaoContratoEmCarencia() {
		log.info("Detalhar consulta consignação por situacao contrato em carencia");

		situacaoContrato.setAguardandoConfirmacao(false);
		situacaoContrato.setEmCarencia(true);

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", cpf, "",
						"", servicoCodigo, "", situacaoContrato, situacaoServidor);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void detalharConsultaConsignacaoPorSituacaoServidorBloqueado() {
		log.info("Detalhar consulta consignação por situacao servidor bloqueado");

		situacaoServidor.setAtivo(false);
		situacaoServidor.setBloqueado(true);

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), adeIdentificador, "", cpf, "",
						"", servicoCodigo, "", situacaoContrato, situacaoServidor);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void detalharConsultaConsignacaoPorAdeIdentificador() {
		log.info("Detalhar consulta consignação por ade identificador");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), "Solicitação Web", "", "", "",
						"", servicoCodigo, "", situacaoContrato, situacaoServidor);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals("Aguard. Confirmação", detalharConsultaConsignacaoResponse.getResumos().get(1).getSituacao());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF,
				detalharConsultaConsignacaoResponse.getResumos().get(1).getStatusCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorPeriodo() {
		log.info("Detalhar consulta consignação por periodo");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, "2021-06-01", dataInclusao, dataInclusaoFim, integraFolha,
						codigoMargem, indice);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorDataInclusaoInicio() {
		log.info("Detalhar consulta consignação por data inclusao inicio");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, "2021-06-01", dataInclusaoFim, integraFolha, codigoMargem,
						indice);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorDataInclusaoInicioEDataInclusaoFim() {
		log.info("Detalhar consulta consignação por data inclusao inicio e data inclusao fim");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, "2021-01-01", "2021-07-01", integraFolha, codigoMargem,
						indice);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorIntegraFolha() {
		log.info("Detalhar consulta consignação por integra folha");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, dataInclusao, dataInclusaoFim, "1", codigoMargem, indice);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorNaoIntegraFolha() {
		log.info("Detalhar consulta consignação por não integra folha");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, dataInclusao, dataInclusaoFim, "0", codigoMargem, indice);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void detalharConsultaConsignacaoPorCodigoMargem1() {
		log.info("Detalhar consulta consignação por codigo margem 1");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, dataInclusao, dataInclusaoFim, integraFolha, "1", indice);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);

		assertEquals("EMPRÉSTIMO", detalharConsultaConsignacaoResponse.getResumos().get(1).getServico());
		assertEquals(codigoVerba, detalharConsultaConsignacaoResponse.getResumos().get(1).getCodVerba());
		assertEquals(codServico, detalharConsultaConsignacaoResponse.getResumos().get(1).getServicoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void detalharConsultaConsignacaoPorCodigoMargem3() {
		log.info("Detalhar consulta consignação por codigo margem 3");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, dataInclusao, dataInclusaoFim, integraFolha, "3", indice);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar detalhar consulta consignação com usuário sem permissao");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCse.getLogin(), loginCse.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento, servicoCodigo, codigoVerba,
						situacaoContrato, situacaoServidor);

		assertEquals("O usuário não tem permissão para executar esta operação",
				detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("329", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComCorrespondenteInexistente() {
		log.info("Tentar detalhar consulta consignação com correspondente inexistente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), "02", false, false,
						false, true, periodo, dataInclusao, dataInclusaoFim, integraFolha, codigoMargem, indice);

		assertEquals("Correspondente não encontrado.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("468", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void detalharConsultaConsignacaoPorCorrespondente() {
		log.info("Detalhar consulta consignação por correspondente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), "138", false, false,
						false, true, periodo, dataInclusao, dataInclusaoFim, integraFolha, codigoMargem, indice);

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, detalharConsultaConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", detalharConsultaConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("1980-01-01-03:00",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("Solteiro(a)", detalharConsultaConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("213464140",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", detalharConsultaConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("745", detalharConsultaConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("-1", detalharConsultaConsignacaoResponse.getBoleto().getValue().getRanking().toString());
		assertEquals("CARTAO DE CREDITO - LANCAMENTO",
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getServico().toString());
		assertEquals("-1.0", detalharConsultaConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertTrue(detalharConsultaConsignacaoResponse.getBoleto().getValue().getDataReserva().toString()
				.contains("2021-07-01"));
		assertEquals("100.0",
				String.valueOf(detalharConsultaConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals("61014",
				String.valueOf(detalharConsultaConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		assertEquals("025", detalharConsultaConsignacaoResponse.getBoleto().getValue().getIndice());
		assertEquals(5, detalharConsultaConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, detalharConsultaConsignacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("Deferida", detalharConsultaConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("020", detalharConsultaConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals(CodedValues.SAD_DEFERIDA,
				detalharConsultaConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
	}

	@Test
	public void detalharConsultaConsignacaoPorIndice() {
		log.info("Detalhar consulta consignação por indice");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), correspondenteCodigo,
						false, false, false, true, periodo, dataInclusao, dataInclusaoFim, integraFolha, codigoMargem,
						"025");

		assertEquals("Operação realizada com sucesso.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("000", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(detalharConsultaConsignacaoResponse.isSucesso());
		assertTrue(detalharConsultaConsignacaoResponse.getResumos().size() >= 1);
		assertEquals("BANCO BRASIL", detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignataria());
		assertEquals("001",
				detalharConsultaConsignacaoResponse.getResumos().get(1).getConsignatariaCodigo().getValue().toString());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoPorSaldoDevedor() {
		log.info("Tentar detalhar consulta consignação por correspondente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), "", false, false, true,
						true, periodo, dataInclusao, dataInclusaoFim, integraFolha, codigoMargem, indice);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComAdeIdentificadorInexistente() {
		log.info("Tentar detalhar consulta consignação com usuário inválido");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "Web teste", loginServidor.getLogin(),
						cpf, codOrgao, codEstabelecimento, servicoCodigo, codigoVerba, situacaoContrato,
						situacaoServidor);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComUsuarioInvalido() {
		log.info("Tentar detalhar consulta consignação com usuário inválido");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse("csa1", loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
						codOrgao, codEstabelecimento, servicoCodigo, codigoVerba, situacaoContrato, situacaoServidor);

		assertEquals("Usuário ou senha inválidos", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("358", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComSenhaInvalida() {
		log.info("Tentar detalhar consulta consignação com senha inválida");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), "abc1234", adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
						codOrgao, codEstabelecimento, servicoCodigo, codigoVerba, situacaoContrato, situacaoServidor);

		assertEquals("Usuário ou senha inválidos", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("358", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoSemInformarMatriculaAdeNumeroECPFDoServidor() {
		log.info("Tentar detalhar consulta consignação sem informar matricula ou CPF do servidor");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(0), "", "", "", codOrgao,
						codEstabelecimento, servicoCodigo, codigoVerba, situacaoContrato, situacaoServidor);

		assertEquals(
				"Pelo menos um dos parâmetros a seguir deve ser informado: o número da autorização, o identificador da autorização, o CPF do servidor ou a matrícula do servidor.",
				detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("345", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoParaServidorNaoCadastrado() {
		log.info("Tentar detalhar consulta consignação para servidor não cadastrado");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "859674", cpf,
						codOrgao, codEstabelecimento, servicoCodigo, codigoVerba, situacaoContrato, situacaoServidor);

		assertEquals("Nenhum servidor encontrado", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("293", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComOrgaoInexistente() {
		log.info("Tentar detalhar consulta consignação com orgão inexistente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, "125", codEstabelecimento, servicoCodigo, codigoVerba,
						situacaoContrato, situacaoServidor);

		assertEquals("Nenhum servidor encontrado", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("293", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComEstabelecimentoInexistente() {
		log.info("Tentar detalhar consulta consignação com estabelecimento inexistente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, "52", servicoCodigo, codigoVerba, situacaoContrato,
						situacaoServidor);

		assertEquals("Nenhum servidor encontrado", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("293", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar detalhar consulta consignação com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse("csa", loginCsa.getSenha(), adeNumero, adeIdentificador, loginServidor.getLogin(), cpf,
						codOrgao, codEstabelecimento, servicoCodigo, codigoVerba, situacaoContrato, situacaoServidor);

		assertEquals("IP de acesso inválido", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("362", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComAdeInexistente() {
		log.info("Tentar detalhar consulta consignação com ade inexistente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), Long.valueOf(60800), adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento, servicoCodigo, codigoVerba,
						situacaoContrato, situacaoServidor);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComServicoInexistente() {
		log.info("Tentar detalhar consulta consignação com servico inexistente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento, "01", codigoVerba,
						situacaoContrato, situacaoServidor);

		assertEquals("Serviço não encontrado.", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("411", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarDetalharConsultaConsignacaoComCodVervaInexistente() {
		log.info("Tentar detalhar consulta consignação com codVerba inexistente");

		DetalharConsultaConsignacaoResponse detalharConsultaConsignacaoResponse = detalharConsultaConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador,
						loginServidor.getLogin(), cpf, codOrgao, codEstabelecimento, servicoCodigo, "1",
						situacaoContrato, situacaoServidor);

		assertEquals("Nenhuma consignação encontrada", detalharConsultaConsignacaoResponse.getMensagem());
		assertEquals("294", detalharConsultaConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(detalharConsultaConsignacaoResponse.isSucesso());
	}
}