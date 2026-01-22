package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.dao.AgendamentoDao;
import com.zetra.econsig.dao.RelatorioDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.Agendamento;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RelatoriosPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RelatorioConferenciaCadastroVerbasTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private RelatoriosPage relatoriosPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private String usuCodigo = null;
	private String relCodigo = null;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private AgendamentoDao agendamentoDao;

	@Autowired
	private RelatorioDao relatorioDao;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        relatoriosPage = new RelatoriosPage(webDriver);

		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RELATORIO_VERBAS.getCodigo()));
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	/**
	 * Teste automatizado que gera o "Relatorio de Conferencia de Cadastro de
	 * Verbas" e/ou realiza seu agendamento.
	 */
	@Test
	public void relatorioConferenciaCadastroVerbasSemAgendamento() {
		log.info("Relatorio de Conferencia de Cadastro Verbas sem agendamento");
		// Acessa o sistema
		loginPage.loginSimples(loginCse);

		// Seleciona o item desejado no menu.
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato TXT
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.selecionarFormato("TXT");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato CSV
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.selecionarFormato("CSV");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato DOC
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.selecionarFormato("DOC");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato XLS
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.selecionarFormato("XLS");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

		// preencher dados para gerar relatorio formato XLSX
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.selecionarFormato("XLSX");
		relatoriosPage.clicarConfirmar();

		econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

		// verificar se os arquivos foram gerados
		assertTrue(relatoriosPage.quantidadeRelatorios() >= 6);

		// verifica link para download
		relatoriosPage.clicarOpcao();
		assertTrue(relatoriosPage.verificarDownloadRelatorio());

		// excluir relatorio
		relatoriosPage.excluiRelatorioNaoAgendadoInterface();

		assertTrue(econsigHelper.getMensagemPopUp(webDriver).contains("Confirma a exclusão do arquivo"));
		assertEquals("Arquivo removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void relatorioConferenciaCadastroVerbasComAgendamento() {
		log.info("Relatorio de Conferencia de Cadastro Verbas com agendamento");

		usuCodigo = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();
		relCodigo = relatorioDao.findByFunCodigo(CodedValues.FUN_CONS_SERVICOS).getRelCodigo();

		// Acessa o sistema
		loginPage.loginSimples(loginCse);

		// Seleciona o item desejado no menu.
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioVerbas();

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.selecionarConsignataria("BANCO BRASIL - 001");
		relatoriosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		relatoriosPage.marcarAgendamento();
		relatoriosPage.selecionarTipoAgendamento("Periódico Mensal");
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarConfirmar();

		assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verificar se arquivo foi agendado
		assertTrue(
				webDriver.getPageSource().contains("Relatório de Conferência de Cadastro de Verbas"));
		assertTrue(webDriver.getPageSource().contains("Aguardando Execução"));
		assertTrue(webDriver.getPageSource().contains("Periódico Mensal"));

		// verifica no banco o agendamento
		List<Agendamento> agendamento = agendamentoDao.findByUsuCodigoAndRelCodigoAndSagCodigoAndTagCodigo(usuCodigo,
				relCodigo, "2", "3");
		assertNotNull(agendamento.get(0));

		// cancelar agendamento
		relatoriosPage.clicarCancelarAgendamento();
		assertEquals("Confirma o cancelamento do agendamento?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Agendamento cancelado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica no banco o cancelamento
		assertEquals("4", agendamentoDao.findByAgdCodigo(agendamento.get(0).getAgdCodigo()).getSagCodigo());
	}
}
