package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.UnhandledAlertException;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.ManutencaoConsignatariaPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.PrazoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.EditarRegraTaxaJurosConsignatariaPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class EditarRegraTaxaJurosConsignatariaTest extends BaseTest {

    private LoginPage loginPage;

    private MenuPage menuPage;

    private AcoesUsuarioPage acoesUsuarioPage;

    private ManutencaoConsignatariaPage manutencaoConsignatariaPage;

    private EditarRegraTaxaJurosConsignatariaPage editarRegraTaxaJurosConsignatariaPage;

    private final LoginInfo loginCsa1 = LoginValues.csa1;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private FuncaoService funcaoService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private PrazoService prazoService;

    public void validaPermissaoParaEditarRegraTaxaJuros() {
        funcaoService.criarFuncaoPerfilCsa(usuarioService.getUsuario(LoginValues.csa1.getLogin()).getUsuCodigo(), "452", usuarioService.getCsaCodigo(LoginValues.csa1.getLogin()));
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        validaPermissaoParaEditarRegraTaxaJuros();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        acoesUsuarioPage = new AcoesUsuarioPage(webDriver);
        manutencaoConsignatariaPage = new ManutencaoConsignatariaPage(webDriver);
        editarRegraTaxaJurosConsignatariaPage = new EditarRegraTaxaJurosConsignatariaPage(webDriver);

        // Inclui prazo para o serviço ALUGUEL e consignatária TREINAMENTO pois só exibe serviços que tenham prazo
        prazoService.incluirPrazoConsignatariaServico("3700808080808080808080808080A538", "2E0B8080808080808080808080808280", (short) 12);
    }

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

    @Test
    public void criarNovaRegraTaxaJurosComSucesso() {
        log.info("Criar nova regra de taxa de juros com sucesso");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // cria nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //consulta no banco ou validaçao de mensagem
        assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Regra de taxa de juros criada com sucesso."));
    }

    @Test
    public void falhaAoTentarCriarNovaRegraTaxaJurosSemServico() {
        log.info("Falha ao tentar criar nova regra de taxa de juros sem serviço");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // cria nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Serviço deve ser informado");
    }

    @Test
    public void falhaAoTentarCriarNovaRegraTaxaJurosSemFaixaPrazoIniciall() {
        log.info("Falha ao tentar criar nova regra de taxa de juros sem faixa prazo inicial");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // cria nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Faixa prazo inicial deve ser informada");
    }

    @Test
    public void falhaAoTentarCriarNovaRegraTaxaJurosSemFaixaPrazoFinal() {
        log.info("Falha ao tentar criar nova regra de taxa de juros sem faixa prazo final");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // cria nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Faixa prazo final deve ser informada");
    }

    @Test
    public void falhaAoTentarCriarNovaRegraTaxaJurosComPrazoInicialMaiorPrazoFinal() {
        log.info("Falha ao tentar criar nova regra de taxa de juros com prazo inicial maior que prazo final");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // Criar nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("20");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("10");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Faixa de valor inicial não pode ser maior ou igual ao final.");
    }

    @Test
    public void falhaAoTentarCriarNovaRegraTaxaJurosComPrazoInicialIgualPrazoFinal() {
        log.info("Falha ao tentar criar nova regra de taxa de juros com prazo inicial igual que prazo final");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // cria nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("10");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Faixa de valor inicial não pode ser maior ou igual ao final.");
    }

    @Test
    public void falhaAoTentarCriarNovaRegraTaxaJurosSemTaxaJuros() {
        log.info("Falha ao tentar criar nova regra de taxa de juros sem taxa de juros");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // cria nova regra de taxa de juros
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Taxa de juros deve ser informada");
    }

    @Test
    public void editarRegraTaxaJurosComSucesso() {
        log.info("Editar regra de taxa de juros com sucesso");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
//        editarRegraTaxaJurosConsignatariaPage.selecionarServico("EMPRESTIMO MARGEM 3");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("1");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("100");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("1");
        acoesUsuarioPage.clicarSalvar();

        assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Regra de taxa de juros salva com sucesso."));
    }

    @Test
    public void falhaAoTentarEditarRegraTaxaJurosComPrazoInicialMaiorPrazoFinal() {
        log.info("Editar regra de taxa de juros com prazo final maior que prazo final");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
//        editarRegraTaxaJurosConsignatariaPage.selecionarServico("EMPRESTIMO MARGEM 3");

        final Throwable exception = assertThrows(UnhandledAlertException.class, () -> {
            editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("52");
            editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("32");
        });

        assertTrue(exception.getMessage().contains("Faixa de valor inicial não pode ser maior ou igual ao final."));
    }

    @Test
    public void falhaAoTentarEditarRegraTaxaJurosComPrazoInicialIgualPrazoFinal() {
        log.info("Falha ao tentar editar regra de taxa de juros com prazo inicial igual que prazo final");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
//        editarRegraTaxaJurosConsignatariaPage.selecionarServico("EMPRESTIMO MARGEM 3");

        final Throwable exception = assertThrows(UnhandledAlertException.class, () -> {
            editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("32");
            editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("32");
        });

        assertTrue(exception.getMessage().contains("Faixa de valor inicial não pode ser maior ou igual ao final."));
    }

    @Test
    public void falhaAoTentarEditarRegraTaxaJurosSemServico() {
        log.info("Falha ao tentar editar regra de taxa de juros sem serviço");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("-- Selecione --");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Serviço deve ser informado");
    }

    @Test
    public void falhaAoTentarEditarRegraTaxaJurosSemFaixaPrazoInicial() {
        log.info("Falha ao tentar editar regra de taxa de juros sem faixa prazo inicial");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Faixa prazo inicial deve ser informada");
    }

    @Test
    public void falhaAoTentarEditarRegraTaxaJurosSemFaixaPrazoFinal() {
        log.info("Falha ao tentar editar regra de taxa de juros sem faixa prazo final");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Faixa prazo final deve ser informada");
    }

    @Test
    public void falhaAoTentarEditarRegraTaxaJurosSemTaxaJuros() {
        log.info("Falha ao tentar editar regra de taxa de juros sem taxa de juros");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder editar
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //editar regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarEditar();
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("");
        acoesUsuarioPage.clicarSalvar();

        assertEquals(webDriver.switchTo().alert().getText(), "Taxa de juros deve ser informada");
    }

    @Test
    public void excluirRegraTaxaJurosComSucesso() {
        log.info("Excluir regra de taxa de juros com sucesso");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder excluir
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //excluir regra de taxa de juros
        acoesUsuarioPage.clicarOpcoesRegraTaxaJuros();
        acoesUsuarioPage.clicarExcluir();
        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }

        assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Regra de taxa de juros excluída com sucesso."));
    }

    @Test
    public void ativarTabelaComSucesso() {
        log.info("Ativar tabela com sucesso");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder excluir
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //ativar tabela de regra de taxa de juros
        editarRegraTaxaJurosConsignatariaPage.clicarAtivarTabela();
        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }

        assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Tabela iniciada ativada com sucesso."));

        editarRegraTaxaJurosConsignatariaPage.clicarIniciarTabela();
        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }
    }

    @Test
    public void iniciarTabelaComSucesso() {
        log.info("Ativar tabela com sucesso");
        // loga no sistema
        loginPage.loginSimples(loginCsa1);

        // acessa menu
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuConsignataria();

        // precisa ativar uma nova tabela para poder excluir
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.clicarCriarNovaRegraTaxaJuros();
        editarRegraTaxaJurosConsignatariaPage.selecionarServico("ALUGUEL");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoInicial("10");
        editarRegraTaxaJurosConsignatariaPage.preencherFaixaPrazoFinal("15");
        editarRegraTaxaJurosConsignatariaPage.preencherTaxaJuros("2");
        acoesUsuarioPage.clicarSalvar();

        //para iniciar precisar estar ativada a tabela de regra de taxa de juros
        editarRegraTaxaJurosConsignatariaPage.clicarAtivarTabela();
        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }
        //iniciar tabela de regra de taxa de juros
        editarRegraTaxaJurosConsignatariaPage.clicarIniciarTabela();
        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }

        assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Tabela iniciada com sucesso."));
    }
}
