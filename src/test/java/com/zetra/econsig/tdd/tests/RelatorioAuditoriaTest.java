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
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RelatoriosPage;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RelatorioAuditoriaTest extends BaseTest {

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

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        relatoriosPage = new RelatoriosPage(webDriver);
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	/**
	 * Teste automatizado que agenda o "Relatorio de Auditoria".
	 */
	@Test
	public void relatorioAuditoriaSemAgendamento() {
		log.info("Relatorio auditoria sem agendamento");

		usuCodigo = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();
		relCodigo = relatorioDao.findByFunCodigo(CodedValues.FUN_REL_AUDITORIA).getRelCodigo();

		// Acessa o sistema
		loginPage.loginSimples(loginCse);

		// Seleciona o item desejado no menu.
		menuPage.acessarMenuRelatorios();
		menuPage.acessarItemMenuRelAuditoria();

		// preencher dados para gerar relatorio formato PDF
		relatoriosPage.preencherPeriodoInicial("24/10/2020");
		relatoriosPage.preencherPeriodoFinal("20/11/2020");
		relatoriosPage.selecionarTipoAgendamento("Periódico Diário");
		relatoriosPage.selecionarFormato("PDF");
		relatoriosPage.clicarAgendar();

		assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica no banco o agendamento
		Agendamento agendamentoRelatorio = agendamentoDao
				.findByUsuCodigoAndRelCodigoAndSagCodigoAndTagCodigo(usuCodigo, relCodigo, "2", "1").get(0);
		assertNotNull(agendamentoRelatorio);

		// cancelar agendamento
		relatoriosPage.clicarCancelarAgendamento();
		assertEquals("Confirma o cancelamento do agendamento?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Agendamento cancelado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica no banco o cancelamento
		assertEquals("4", agendamentoDao.findByAgdCodigo(agendamentoRelatorio.getAgdCodigo()).getSagCodigo());
	}

	@Test
	public void tentarGerarRelatorioAuditoriaComDadosInvalidos() {
		log.info("Relatorio auditoria com dados do periodo invalido");
		// Acessa o sistema
		loginPage.loginSimples(loginCse);

		// Seleciona o item desejado no menu.
		menuPage.acessarMenuRelatorios();
		menuPage.acessarItemMenuRelAuditoria();

		relatoriosPage.clicarAgendar();

		assertTrue(webDriver.getPageSource().contains("Selecione o tipo de agendamento."));
		assertTrue(webDriver.getPageSource().contains("Informe o início do período."));
		assertTrue(webDriver.getPageSource().contains("Informe o final do período."));
		assertTrue(webDriver.getPageSource().contains("Selecione um formato."));

		relatoriosPage.selecionarTipoAgendamento("Periódico Diário");
		relatoriosPage.preencherPeriodoInicial("20/09/2020");
		relatoriosPage.preencherPeriodoFinal("23/11/2020");
		relatoriosPage.selecionarFormato("XLSX");
		relatoriosPage.clicarAgendar();

		assertEquals("O período deve ser limitado a no máximo 30 dias.", econsigHelper.getMensagemPopUp(webDriver));
	}
}