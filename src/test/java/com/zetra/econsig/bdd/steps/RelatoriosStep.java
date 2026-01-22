package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.dao.RelatorioDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.RelatorioService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.RelatoriosPage;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RelatoriosStep {

	private final LoginInfo loginSuporte = LoginValues.suporte;
	private String usuCodigo = null;
	private String relCodigoComissionamento = null;
	private String relCodigoBeneficiarioDataNascimento = null;
	private String relCodigoExclusaoBeneficiario = null;
	private String relCodigoContratoBeneficio = null;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private RelatorioService relatorioService;

	@Autowired
	private RelatorioDao relatorioDao;

	@Autowired
	private UsuarioServiceTest usuarioService;

    private MenuPage menuPage;
    private RelatoriosPage relatoriosPage;

    @Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        relatoriosPage = new RelatoriosPage(getWebDriver());
    }

	@Quando("gerar relatorio Comissionamento e Agenciamento Analitico em varios formatos")
	public void gerarRelatorioComissionamento() throws Throwable {
		log.info("Quando gerar relatório Comissionamento e Agenciamento Analítico em vários formatos");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato TXT
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioComissionamentoAgAnalitico();
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.selecionarFormato("TXT");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato CSV
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioComissionamentoAgAnalitico();
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.selecionarFormato("CSV");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato DOC
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioComissionamentoAgAnalitico();
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.selecionarFormato("DOC");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato XLS
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioComissionamentoAgAnalitico();
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.selecionarFormato("XLS");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato XLSX
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioComissionamentoAgAnalitico();
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.selecionarFormato("XLSX");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");
	}

	@Quando("gerar relatorio em varios formatos no periodo {string} a {string}")
	public void gerarRelatorioVariosFormatos(String periodoInicial, String periodoFinal) throws Throwable {
		log.info("Quando gerar relatório em vários formatos no período {} a {}", periodoInicial, periodoFinal);

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.selecionarFormato("TXT");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.selecionarFormato("CSV");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.selecionarFormato("DOC");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.selecionarFormato("XLS");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.selecionarFormato("XLSX");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Informe os parâmetros do relatório");
	}

	@Quando("gerar relatorio Comissionamento e Agenciamento Analitico com agendamento")
	public void gerarRelatorioComissionamentoComAgendamento() throws Throwable {
		log.info("Quando gerar relatório Comissionamento e Agenciamento Analítico com agendamento");

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodo("10/2020");
		relatoriosPage.marcarAgendamento();
		relatoriosPage.selecionarTipoAgendamento("Periódico Mensal");
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarConfirmar();
	}

	@Quando("gerar relatorio com agendamento no periodo {string} a {string}")
	public void gerarRelatorioComAgendamento(String periodoInicial, String periodoFinal) throws Throwable {
		log.info("Quando gerar relatório com agendamentono período {} a {}", periodoInicial, periodoFinal);

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial(periodoInicial);
		relatoriosPage.preencherPeriodoFinal(periodoFinal);
		relatoriosPage.marcarAgendamento();
		relatoriosPage.selecionarTipoAgendamento("Periódico Mensal");
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarConfirmar();

	}

	@Quando("excluir um relatorio")
	public void excluirRelatorioComissionamento() throws Throwable {
		log.info("Quando excluir um relatório");

		// excluir relatorio
		relatoriosPage.excluiRelatorioNaoAgendadoInterface();

		assertTrue(econsigHelper.getMensagemPopUp(getWebDriver()).contains("Confirma a exclusão do arquivo"));

		econsigHelper.verificaTextoPagina(getWebDriver(), "Arquivo removido com sucesso.");
	}

	@Quando("cancelar agendamento do relatorio")
	public void cancelarAgendamentoRelatorio() throws Throwable {
		log.info("Quando cancelar agendamento do relatório");

		// cancelar agendamento
		relatoriosPage.clicarCancelarAgendamento();
		assertEquals("Confirma o cancelamento do agendamento?", econsigHelper.getMensagemPopUp(getWebDriver()));
		assertEquals("Agendamento cancelado com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));
	}

	@Entao("verificar que relatorio Comissionamento e Agenciamento Analitico foi agendado com sucesso")
	public void relatorioComissionamentoAgendado() throws Throwable {
		log.info("Entao verificar que relatório Comissionamento e Agenciamento Analítico foi agendado com sucesso");

		assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));

		// verificar se arquivo foi agendado
		assertTrue(getWebDriver().getPageSource()
				.contains("Relatório de Comissionamento e Agenciamento Analítico"));
		assertTrue(getWebDriver().getPageSource().contains("Aguardando Execução"));
		assertTrue(getWebDriver().getPageSource().contains("Periódico Mensal"));

		// verifica no banco o agendamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoComissionamento = relatorioDao.findByFunCodigo(CodedValues.FUN_REL_COMISSIONAMENTO_AGEN_ANALITICO)
				.getRelCodigo();

		assertNotNull(relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoComissionamento, "2", "3").get(0));
	}

	@Entao("verificar que relatorio Beneficiario por Data Nascimento foi agendado com sucesso")
	public void relatorioBeneficiarioPorDataNascimentoAgendado() throws Throwable {
		log.info("Entao verificar que relatório Beneficiário por Data Nascimento foi agendado com sucesso");

		assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));

		// verificar se arquivo foi agendado
		assertTrue(getWebDriver().getPageSource().contains("Relatório Beneficiário por Data Nascimento"));
		assertTrue(getWebDriver().getPageSource().contains("Aguardando Execução"));
		assertTrue(getWebDriver().getPageSource().contains("Periódico Mensal"));

		// verifica no banco o agendamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoBeneficiarioDataNascimento = relatorioDao
				.findByFunCodigo(CodedValues.FUN_REL_BENEFICIARIO_DATA_NASCIMENTO).getRelCodigo();
		assertNotNull(relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoBeneficiarioDataNascimento, "2", "3")
				.get(0));
	}

	@Entao("verificar que relatorio Exclusao de Beneficiarios por Periodo foi agendado com sucesso")
	public void relatorioExclusaoBeneficiarioAgendado() throws Throwable {
		log.info("Entao verificar que relatório Exclusão de Beneficiários por Período foi agendado com sucesso");

		assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));

		// verificar se arquivo foi agendado
		assertTrue(getWebDriver().getPageSource()
				.contains("Relatório de Exclusão de Beneficiários por Período"));
		assertTrue(getWebDriver().getPageSource().contains("Aguardando Execução"));
		assertTrue(getWebDriver().getPageSource().contains("Periódico Mensal"));

		// verifica no banco o agendamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoExclusaoBeneficiario = relatorioDao.findByFunCodigo(CodedValues.FUN_EXCL_BENEFICIARIO_PERIODO)
				.getRelCodigo();

		assertNotNull(
				relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoExclusaoBeneficiario, "2", "3").get(0));
	}

	@Entao("verificar que relatorio Contratos de Beneficios foi agendado com sucesso")
	public void relatorioContratoBeneficioAgendado() throws Throwable {
		log.info("Entao verificar que relatório Contratos de Benefícios foi agendado com sucesso");

		assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));

		// verificar se arquivo foi agendado
		assertTrue(getWebDriver().getPageSource().contains("Relatório de Contratos de Benefícios"));
		assertTrue(getWebDriver().getPageSource().contains("Aguardando Execução"));
		assertTrue(getWebDriver().getPageSource().contains("Periódico Mensal"));

		// verifica no banco o agendamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoContratoBeneficio = relatorioDao.findByFunCodigo(CodedValues.FUN_REL_CONTRATO_BENEFICIO)
				.getRelCodigo();

		assertNotNull(relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoContratoBeneficio, "2", "3").get(0));
	}

	@Entao("verificar que o agendamento do relatorio Comissionamento e Agenciamento Analitico foi cancelado")
	public void verificarCancelamentoAgendamentoRelatorioComissionamento() throws Throwable {
		log.info("Entao verificar que o agendamento do relatório Comissionamento e Agenciamento Analítico foi cancelado");

		// verifica no banco o cancelamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoComissionamento = relatorioDao.findByFunCodigo(CodedValues.FUN_REL_COMISSIONAMENTO_AGEN_ANALITICO)
				.getRelCodigo();
		assertEquals("4",
				relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoComissionamento).get(0).getSagCodigo());
	}

	@Entao("verificar que o agendamento do relatorio Beneficiario por Data Nascimento foi cancelado")
	public void verificarCancelamentoAgendamentoRelatorioBeneficiariosPorDataNascimento() throws Throwable {
		log.info("Entao verificar que o agendamento do relatório Beneficiário por Data Nascimento foi cancelado");

		// verifica no banco o cancelamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoBeneficiarioDataNascimento = relatorioDao
				.findByFunCodigo(CodedValues.FUN_REL_BENEFICIARIO_DATA_NASCIMENTO).getRelCodigo();

		assertEquals("4", relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoBeneficiarioDataNascimento)
				.get(0).getSagCodigo());
	}

	@Entao("verificar que o agendamento do Exclusao de Beneficiarios por Periodo foi cancelado")
	public void verificarCancelamentoAgendamentoRelatorioExclusaoBeneficiariosPorPeriodo() throws Throwable {
		log.info("Entao verificar que o agendamento do Exclusão de Beneficiários por Período foi cancelado");

		// verifica no banco o cancelamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoExclusaoBeneficiario = relatorioDao.findByFunCodigo(CodedValues.FUN_EXCL_BENEFICIARIO_PERIODO)
				.getRelCodigo();

		assertEquals("4", relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoExclusaoBeneficiario).get(0)
				.getSagCodigo());
	}

	@Entao("verificar que o agendamento do relatorio Contratos de Beneficios foi cancelado")
	public void verificarCancelamentoAgendamentoRelatorioContratosBeneficios() throws Throwable {
		log.info("Entao verificar que o agendamento do relatório Contratos de Benefícios foi cancelado");

		// verifica no banco o cancelamento
		usuCodigo = usuarioService.getUsuario(loginSuporte.getLogin()).getUsuCodigo();
		relCodigoContratoBeneficio = relatorioDao.findByFunCodigo(CodedValues.FUN_REL_CONTRATO_BENEFICIO)
				.getRelCodigo();

		assertEquals("4",
				relatorioService.getAgendamentoRelatorio(usuCodigo, relCodigoContratoBeneficio).get(0).getSagCodigo());
	}

	@Entao("gera os relatorios com sucesso")
	public void relatorioComissionamentoGeradoComSucesso() throws Throwable {
		log.info("Então gera os relatórios com sucesso");

		assertTrue(relatoriosPage.quantidadeRelatorios() >= 6);

		// verifica link para download
		relatoriosPage.clicarOpcao();
		assertTrue(relatoriosPage.verificarDownloadRelatorio());
	}
}
