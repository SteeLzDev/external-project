package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.ConsultarParametrosClient;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.ConsultarParametrosResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConsultarParametrosTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final String codServico = "001";
	private final String codVerba = "145";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private String svcCodigo;

	@Autowired
    private ConsultarParametrosClient consultarParametrosClient;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private ConvenioDao convenioDao;

	@Autowired
	VerbaConvenioDao verbaConvenioDao;

	@BeforeEach
	public void setUp() {
		svcCodigo = servicoService.retornaSvcCodigo("023");
		// alterar parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "6");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA_MAX, "0");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_MAX_PRAZO, "15");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO,
				"1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "0");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_INF_BANCARIA_OBRIGATORIA, "1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA, "1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA,
				"1");
		ENominaInitializer.limparCache();
	}

	@Test
	public void consultarParametrosDeServicosParaEmprestimo() {
		log.info("Consultar parâmetros de servicos para emprestimo");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), codVerba, codServico, codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarParametrosResponse.getMensagem());
		assertEquals("000", consultarParametrosResponse.getCodRetorno().getValue());
		assertTrue(consultarParametrosResponse.isSucesso());

		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeCpfMatriculaPesquisa());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isValidaCpfPesquisa());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeTac());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeCadMensVinc());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isExigeCadVlrLiberado());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeIof());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isValidaDataNascReserva());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeInfoBancaria());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isValidaInfoBancaria());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isAlteraAutMargemNegativa());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isExigeSenhaServReservarRenegociar());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeSenhaServConsMargem());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeSenhaServAltContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isPermiteAltContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isPermiteRenegociarContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isVisualizaMargem());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isVisualizaMargemNegativa());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isPermiteCompraContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().getUsaCet().getValue());
	}

	@Test
	public void consultarParametrosEmprestimo() {
		log.info("Consultar parâmetros emprestimo");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), codVerba, codServico, codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarParametrosResponse.getMensagem());
		assertEquals("000", consultarParametrosResponse.getCodRetorno().getValue());
		assertTrue(consultarParametrosResponse.isSucesso());

		assertEquals("EMPRÉSTIMO", consultarParametrosResponse.getParametroSet().getValue().getSvcDescricao());
		assertEquals("6",
				consultarParametrosResponse.getParametroSet().getValue().getTamMinMatriculaServidor().toString());
		assertEquals("0",
				consultarParametrosResponse.getParametroSet().getValue().getTamMaxMatriculaServidor().toString());
		assertEquals("-1",
				consultarParametrosResponse.getParametroSet().getValue().getQtdMaxParcelas().getValue().toString());
		assertEquals("0",
				consultarParametrosResponse.getParametroSet().getValue().getQtdMinPrdPgsParaRenegociarAut().toString());
		assertEquals("15", consultarParametrosResponse.getParametroSet().getValue().getDiaCorte().toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasInfoSaldoDevedor().getValue()
				.toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasAprovSaldoDevedor().getValue()
				.toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasInfoPgSaldoDevedor()
				.getValue().toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasLiquidacaoAdeCompra()
				.getValue().toString());
	}

	@Test
	public void consultarParametrosDeServicosParaFinanciamentoDivida() {
		log.info("Consultar parâmetros de servicos para financiamento divida");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "123", "", codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarParametrosResponse.getMensagem());
		assertEquals("000", consultarParametrosResponse.getCodRetorno().getValue());
		assertTrue(consultarParametrosResponse.isSucesso());

		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeCpfMatriculaPesquisa());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isValidaCpfPesquisa());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeTac());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeCadMensVinc());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isExigeCadVlrLiberado());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeIof());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isValidaDataNascReserva());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isExigeInfoBancaria());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isValidaInfoBancaria());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isAlteraAutMargemNegativa());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isExigeSenhaServReservarRenegociar());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeSenhaServConsMargem());
		assertFalse(consultarParametrosResponse.getParametroSet().getValue().isExigeSenhaServAltContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isPermiteAltContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isPermiteRenegociarContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isVisualizaMargem());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isVisualizaMargemNegativa());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().isPermiteCompraContrato());
		assertTrue(consultarParametrosResponse.getParametroSet().getValue().getUsaCet().getValue());
	}

	@Test
	public void consultarParametrosFinanciamentoDivida() {
		log.info("Consultar parâmetros financiamento divida");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "123", "", codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", consultarParametrosResponse.getMensagem());
		assertEquals("000", consultarParametrosResponse.getCodRetorno().getValue());
		assertTrue(consultarParametrosResponse.isSucesso());

		assertEquals("FINANCIAMENTO DE DÍVIDA",
				consultarParametrosResponse.getParametroSet().getValue().getSvcDescricao());
		assertEquals("6",
				consultarParametrosResponse.getParametroSet().getValue().getTamMinMatriculaServidor().toString());
		assertEquals("0",
				consultarParametrosResponse.getParametroSet().getValue().getTamMaxMatriculaServidor().toString());
		assertEquals("15",
				consultarParametrosResponse.getParametroSet().getValue().getQtdMaxParcelas().getValue().toString());
		assertEquals("0",
				consultarParametrosResponse.getParametroSet().getValue().getQtdMinPrdPgsParaRenegociarAut().toString());
		assertEquals("15", consultarParametrosResponse.getParametroSet().getValue().getDiaCorte().toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasInfoSaldoDevedor().getValue()
				.toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasAprovSaldoDevedor().getValue()
				.toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasInfoPgSaldoDevedor()
				.getValue().toString());
		assertEquals("0", consultarParametrosResponse.getParametroSet().getValue().getDiasLiquidacaoAdeCompra()
				.getValue().toString());
	}

	@Test
	public void consultarParametrosMaisDeUmServicoEncontrado() throws InterruptedException {
		log.info("Consultar parâmetros mais de um serviço encontrado");

		Convenio newCnv = new Convenio();
		newCnv.setCnvCodigo("25808080808080808080808080808280");
		newCnv.setOrgCodigo("751F8080808080808080808080809780");
		newCnv.setCsaCodigo("267");
		newCnv.setSvcCodigo("15808080808080808080808080808280");
		newCnv.setScvCodigo(CodedValues.SCV_ATIVO);
		newCnv.setVceCodigo("1");
		newCnv.setCnvIdentificador("cnv_289140_001_17167412007023");
		newCnv.setCnvDataIni(new Date(LocalDate.now().minusMonths(10).toEpochDay()));
		newCnv.setCnvCodVerba("123");
		convenioDao.save(newCnv);

		VerbaConvenio vco = new VerbaConvenio();
		vco.setVcoCodigo("35808080808080808080808080808280");
		vco.setCnvCodigo("25808080808080808080808080808280");
		vco.setVcoAtivo(CodedValues.STS_ATIVO);
		vco.setVcoDataIni(new Date(LocalDate.now().minusMonths(10).toEpochDay()));
		vco.setVcoDataFim(new Date(LocalDate.now().plusMonths(10).toEpochDay()));
		vco.setVcoVlrVerba(BigDecimal.valueOf(99999999.99));
		vco.setVcoVlrVerbaRest(BigDecimal.valueOf(99999999.99));
		verbaConvenioDao.save(vco);

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA_MAX, "8");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "6");
		ParamSist.getInstance().reset();

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "123", "", codOrgao, codEstabelecimento);

		assertEquals("Mais de um serviço encontrado", consultarParametrosResponse.getMensagem());
		assertEquals("243", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
		assertTrue(!consultarParametrosResponse.getServicos().isEmpty());

		verbaConvenioDao.delete(vco);
		convenioDao.delete(newCnv);

	}

	@Test
	public void tentarConsultarParametrosComUsuarioInvalido() {
		log.info("Tentar consultar parâmetros com usuário inválido");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient.getResponse("csa1",
				loginCsa.getSenha(), codVerba, codServico, codOrgao, codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", consultarParametrosResponse.getMensagem());
		assertEquals("358", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	@Test
	public void tentarConsultarParametrosComSenhaInvalida() {
		log.info("Tentar consultar parâmetros com senha inválida");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), "abc1234", codVerba, codServico, codOrgao, codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", consultarParametrosResponse.getMensagem());
		assertEquals("358", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	@Test
	public void tentarConsultarParametrosComOrgaoInexistente() {
		log.info("Tentar consultar parâmetros com orgão inexistente");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), codVerba, codServico, "125", codEstabelecimento);

		assertEquals("Órgão não encontrado.", consultarParametrosResponse.getMensagem());
		assertEquals("291", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	@Test
	public void tentarConsultarParametrosComEstabelecimentoInexistente() {
		log.info("Tentar consultar parâmetros com estabelecimento inexistente");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), codVerba, codServico, codOrgao, "52");

		assertEquals("Órgão não encontrado.", consultarParametrosResponse.getMensagem());
		assertEquals("291", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	@Test
	public void tentarConsultarParametrosComCodVerbaInexistente() {
		log.info("Tentar consultar parâmetros com codVerba inexistente");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "5", codServico, codOrgao, codEstabelecimento);

		assertEquals("Código da verba ou código do serviço inválido.", consultarParametrosResponse.getMensagem());
		assertEquals("232", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	@Test
	public void tentarConsultarParametrosComServicoInexistente() {
		log.info("Tentar consultar parâmetros com servico inexistente");

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), codVerba, "52", codOrgao, codEstabelecimento);

		assertEquals("Código da verba ou código do serviço inválido.", consultarParametrosResponse.getMensagem());
		assertEquals("232", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	@Test
	public void tentarConsultarParametrosComTamanhoMinimoMatriculaInvalido() throws InterruptedException {
		log.info("Tentar consultar parâmetros com tamanho minimo matricula inválido");

		// alterar parametro
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "a");
		ENominaInitializer.limparCache();

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), codVerba, codServico, codOrgao, codEstabelecimento);

		assertEquals("Valor inválido do parâmetro de sistema referente ao tamanho mínimo da matrícula do servidor.",
				consultarParametrosResponse.getMensagem());
		assertEquals("415", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}

	/*@Test
	@RunInThread
	public void tentarConsultarParametrosComTamanhoMaximoMatriculaInvalido() throws InterruptedException {
		log.info("Tentar consultar parâmetros com tamanho maximo matricula inválido");

		lock.lock();


		try {
			// alterar parametro
			parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA_MAX, "a");
			ENominaInitializer.limparCache();

			ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient.getResponse(
					loginCsa.getLogin(), loginCsa.getSenha(), codVerba, codServico, codOrgao, codEstabelecimento);

			assertEquals("Valor inválido do parâmetro de sistema referente ao tamanho máximo da matrícula do servidor.",
					consultarParametrosResponse.getMensagem());
			assertEquals("414", consultarParametrosResponse.getCodRetorno().getValue());
			assertFalse(consultarParametrosResponse.isSucesso());
		} finally {
			parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA_MAX, "8");
			ENominaInitializer.limparCache();
			lock.unlock();
		}

	}*/

	@Test
	public void tentarConsultarParametrosComIPDeAcessoInvalido() {
		log.info("Tentar consultar parâmetros com IP de Acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		ConsultarParametrosResponse consultarParametrosResponse = consultarParametrosClient.getResponse("csa",
				loginCsa.getSenha(), codVerba, codServico, codOrgao, codEstabelecimento);

		assertEquals("IP de acesso inválido", consultarParametrosResponse.getMensagem());
		assertEquals("362", consultarParametrosResponse.getCodRetorno().getValue());
		assertFalse(consultarParametrosResponse.isSucesso());
	}
}
