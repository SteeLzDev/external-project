package com.zetra.econsig.tdd.tests;

import com.zetra.econsig.EConsigInitializer;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class RelatorioOcorrenciaConsignacoesTest extends BaseTest {

    private LoginPage loginPage;

    private MenuPage menuPage;

    private RelatoriosPage relatoriosPage;

    private final LoginInfo loginCse = LoginValues.cse1;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private ItemMenuFavoritoService itemMenuFavoritoService;

    @Autowired
    private AgendamentoDao agendamentoDao;

    @Autowired
    private RelatorioDao relatorioDao;

    private final String periodoInicial = "01/05/2024";

    private final String periodoFinal = "30/05/2024";

    private final String periodo = "05/2024";

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        relatoriosPage = new RelatoriosPage(webDriver);

        final String usuCodigo = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();

        // inclui o relatorio como favorito
        itemMenuFavoritoService.excluirItemMenuFavoritos();
        itemMenuFavoritoService.incluirItemMenuFavorito(usuCodigo,
                Integer.toString(ItemMenuEnum.RELATORIO_OCORRENCIA_DE_CONSIGNACAO.getCodigo()));

        EConsigInitializer.limparCache();
    }

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

    @Test
    public void gerarRelatorioOcorrenciaConsignacaoSemAgendamentoTest() {
        log.info("Gerando relatório de ocorrências de consignações sem agendamento.");

        // login no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);

        // acessa o relatório através do menu favoritos
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();

        // preencher periodo
        relatoriosPage.preencherPeriodo(periodo);

        // preencher formato PDF relatório
        relatoriosPage.selecionarFormato("PDF");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // preencher formato TXT relatório e periodo inicial e final
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();
        relatoriosPage.preencherPeriodoInicial(periodoInicial);
        relatoriosPage.preencherPeriodoFinal(periodoFinal);
        relatoriosPage.selecionarFormato("TXT");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // preencher formato CSV relatório e periodo inicial e final
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();
        relatoriosPage.preencherPeriodoInicial(periodoInicial);
        relatoriosPage.preencherPeriodoFinal(periodoFinal);
        relatoriosPage.selecionarFormato("CSV");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // preencher formato DOC relatório e periodo inicial e final
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();
        relatoriosPage.preencherPeriodoInicial(periodoInicial);
        relatoriosPage.preencherPeriodoFinal(periodoFinal);
        relatoriosPage.selecionarFormato("DOC");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // preencher formato XLS relatório e periodo inicial e final
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();
        relatoriosPage.preencherPeriodoInicial(periodoInicial);
        relatoriosPage.preencherPeriodoFinal(periodoFinal);
        relatoriosPage.selecionarFormato("XLS");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // preencher formato XLSX relatório e periodo inicial e final
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();
        relatoriosPage.preencherPeriodoInicial(periodoInicial);
        relatoriosPage.preencherPeriodoFinal(periodoFinal);
        relatoriosPage.selecionarFormato("XLSX");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // preencher formato XML relatório e periodo inicial e final
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();
        relatoriosPage.preencherPeriodoInicial(periodoInicial);
        relatoriosPage.preencherPeriodoFinal(periodoFinal);
        relatoriosPage.selecionarFormato("XML");
        relatoriosPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(webDriver, "Informe os parâmetros do relatório");

        // verificar quantidade de arquivos foram gerados
        assertTrue(relatoriosPage.quantidadeRelatorios() >= 7);

        // verifica link para download
        relatoriosPage.clicarOpcao();
        assertTrue(relatoriosPage.verificarDownloadRelatorio());

        // excluir relatorio
        relatoriosPage.excluiRelatorioNaoAgendadoInterface();

        assertTrue(econsigHelper.getMensagemPopUp(webDriver).contains("Confirma a exclusão do arquivo"));
        assertEquals("Arquivo removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
    }

    @Test
    public void gerarRelatorioOcorrenciaConsignacaoComAgendamentoTest() {
        log.info("Gerando relatório de ocorrências de consignações com agendamento.");

        final String usuCodigo = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();
        final String relCodigo = relatorioDao.findByFunCodigo(CodedValues.FUN_REL_OCORRENCIA_AUTORIZACAO).getRelCodigo();

        // login no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);

        // acessa o relatório através do menu favoritos
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();

        // preencher dados para gerar relatorio em formato PDF
        relatoriosPage.preencherPeriodo(periodo);
        relatoriosPage.marcarAgendamento();
        relatoriosPage.selecionarTipoAgendamento("Periódico Mensal");
        relatoriosPage.selecionarFormato("PDF");
        relatoriosPage.clicarConfirmar();

        assertEquals("Relatório agendado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

        // verificar se arquivo foi agendado
        assertTrue(webDriver.getPageSource().contains("Relatório de Ocorrência de Consignação"));
        assertTrue(webDriver.getPageSource().contains("Aguardando Execução"));
        assertTrue(webDriver.getPageSource().contains("Periódico Mensal"));

        // verifica no banco o agendamento
        final List<Agendamento> agendamentos = agendamentoDao.findByUsuCodigoAndRelCodigoAndSagCodigoAndTagCodigo(usuCodigo,
                relCodigo, "2", "3");
        assertNotNull(agendamentos.getFirst());

        // cancelar agendamento
        relatoriosPage.clicarCancelarAgendamento();
        assertEquals("Confirma o cancelamento do agendamento?", econsigHelper.getMensagemPopUp(webDriver));
        assertEquals("Agendamento cancelado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

        // verifica no banco o cancelamento
        assertEquals("4", agendamentoDao.findByAgdCodigo(agendamentos.getFirst().getAgdCodigo()).getSagCodigo());
    }

    @Test
    public void validarGerarRelatorioOcorrenciaConsignacaoComDadosDoFormatoInvalido() {
        log.info("Gerando relatório de ocorrências de consignações sem nenhum formato preenchido.");

        // login no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);

        // acessa o relatório através do menu favoritos
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();

        relatoriosPage.clicarConfirmar();

        assertTrue(webDriver.getPageSource().contains("Selecione um formato."));
    }

    @Test
    public void validarGerarRelatorioOcorrenciaConsignacaoComDadosDoPeriodoInvalido() {
        log.info("Gerando relatório de ocorrências de consignações sem nenhum periodo preenchido.");

        // login no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);

        // acessa o relatório através do menu favoritos
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosRelatorioOcorrenciaConsignacoes();

        // seleciona o formato do arquivo
        relatoriosPage.selecionarFormato("PDF");
        relatoriosPage.clicarConfirmar();

        assertTrue(webDriver.getPageSource().contains("Informe a matrícula ou CPF ou período ou início e final da ocorrência."));
    }
}
