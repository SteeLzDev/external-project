package com.zetra.econsig.tdd.tests;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.service.*;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.SimularConsignacaoPage;
import com.zetra.econsig.values.CodedValues;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class SimularConsignacaoCampoCetTest extends BaseTest {

    private LoginPage loginPage;

    private MenuPage menuPage;

    private SimularConsignacaoPage simularConsignacaoPage;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    @Autowired
    private FuncaoService funcaoService;

    @Autowired
    private PrazoService prazoService;

    @Autowired
    private ConsignatariaService consignatariaService;

    @Autowired
    private ServicoService servicoService;

    private final LoginInfo loginServidor = LoginValues.servidor1;

    private final String adeVlr = "89,45";

    private final String vlrLiberado = "1585,00";

    private final String przVlr = "1";

    private final String servicoId = "001";

    private final Short prazo = 1;

    private static final String SVC_EMPRESTIMO = "B3858080808080808080808088887ED6";

    private static final String PERFIL_USUARIO = "PERFIL-SERVIDOR";

    private static final String CSA_ID_UM = "001";

    private static final String CSA_ID_DOIS = "17167412007983";

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        simularConsignacaoPage = new SimularConsignacaoPage(webDriver);

        funcaoService.criarFuncaoPerfil(PERFIL_USUARIO, "532");
        funcaoService.criarFuncaoPerfil(PERFIL_USUARIO, "303");

        parametroSistemaService.ativarParametroServicoServ("124", SVC_EMPRESTIMO, "1");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_NAO);
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SIMULADOR_COM_CET_TIPO_OPERACAO, CodedValues.TPC_SIM);
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, CodedValues.TPC_SIM);

        final Consignataria consignataria = consignatariaService.obterConsignatariaPorIdentificador(CSA_ID_UM);
        final Consignataria outraConsignataria = consignatariaService.obterConsignatariaPorIdentificador(CSA_ID_DOIS);
        final Servico servico = servicoService.obterServicoPorIdentificador(servicoId);

        final double taxa = 1.23d;
        prazoService.incluirCoeficienteAtivo(consignataria.getCsaCodigo(), servico.getSvcCodigo(), prazo, BigDecimal.valueOf(taxa));
        prazoService.incluirCoeficienteAtivo(outraConsignataria.getCsaCodigo(), servico.getSvcCodigo(), prazo, BigDecimal.valueOf(taxa));

        EConsigInitializer.limparCache();
    }

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
        final Consignataria consignataria = consignatariaService.obterConsignatariaPorIdentificador("001");
        final Consignataria outraConsignataria = consignatariaService.obterConsignatariaPorIdentificador("17167412007983");
        final Servico servico = servicoService.obterServicoPorIdentificador(servicoId);

        prazoService.deletarCoeficienteAtivo(consignataria.getCsaCodigo(), servico.getSvcCodigo(), prazo);
        prazoService.deletarCoeficienteAtivo(outraConsignataria.getCsaCodigo(), servico.getSvcCodigo(), prazo);
    }

    @Test
    public void validarSimularConsignacaoSemValorPreenchido() {
        log.info("Validando a função simular consignação sem nenhum valor (Valor solicitado/Valor da prestação) preenchido");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        assertEquals("Preencha o valor da prestação ou o valor solicitado para a simulação.", econsigHelper.getMensagemPopUp(webDriver));
    }

    @Test
    public void validarSimularConsignacaoValorSolicitadoEValorPrestacaoPreenchido() {
        log.info("Validando a função simular consignação com o valor solicitado e valor da prestação preenchido");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        assertEquals("Preencha somente um dos valores (valor da prestação ou o valor solicitado) para a simulação.", econsigHelper.getMensagemPopUp(webDriver));
    }

    @Test
    public void validarBotaoNovoContratoComValorSolicitadoPreenchido() {
        log.info("Validando o botão Novo Contrato preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        final String btnNovoContrato = simularConsignacaoPage.retornarTextoBotaoNovoContrato();

        assertTrue(webDriver.getPageSource().contains(btnNovoContrato));
    }

    @Test
    public void validarBotaoNovoContratoComValorPrestacaoPreenchido() {
        log.info("Validando o botão Novo Contrato preenchendo o valor da prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        final String btnNovoContrato = simularConsignacaoPage.retornarTextoBotaoNovoContrato();

        assertTrue(webDriver.getPageSource().contains(btnNovoContrato));
    }

    @Test
    public void validarBotaoRenegociacaoComValorSolicitadoPreenchido() {
        log.info("Validando o botão Renegociação preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        final String btnRenegociacao = simularConsignacaoPage.retornarTextoBotaoRenegociacao();

        assertTrue(webDriver.getPageSource().contains(btnRenegociacao));
    }

    @Test
    public void validarBotaoRenegociacaoComValorPrestacaoPreenchido() {
        log.info("Validando o botão Renegociação preenchendo o valor da prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        final String btnRenegociacao = simularConsignacaoPage.retornarTextoBotaoRenegociacao();

        assertTrue(webDriver.getPageSource().contains(btnRenegociacao));
    }

    @Test
    public void validarBotaoPortabilidadeComValorSolicitadoPreenchido() {
        log.info("Validando o botão Portabilidade preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        final String btnPortabilidade = simularConsignacaoPage.retornarTextoBotaoPortabilidade();

        assertTrue(webDriver.getPageSource().contains(btnPortabilidade));
    }

    @Test
    public void validarBotaoPortabilidadeComValorPrestacaoPreenchido() {
        log.info("Validando o botão Portabilidade preenchendo o valor da prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        final String btnPortabilidade = simularConsignacaoPage.retornarTextoBotaoPortabilidade();

        assertTrue(webDriver.getPageSource().contains(btnPortabilidade));
    }

    @Test
    public void validarCampoCetComValorSolicitadoPreenchido() {
        log.info("Validando o campo CET (%) preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        assertTrue(webDriver.getPageSource().contains("CET (%)"));
    }

    @Test
    public void validarCampoCetComValorPrestacaoPreenchido() {
        log.info("Validando o campo CET (%) preenchendo o valor prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        assertTrue(webDriver.getPageSource().contains("CET (%)"));
    }

    @Test
    public void validarCampoCetAnualComValorSolicitadoPreenchido() {
        log.info("Validando o campo CET anual preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        assertTrue(webDriver.getPageSource().contains("CET anual (%)"));
    }

    @Test
    public void validarCampoCetAnualComValorPrestacaoPreenchido() {
        log.info("Validando o campo CET anual preenchendo o valor prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        assertTrue(webDriver.getPageSource().contains("CET anual (%)"));
    }

    @Test
    public void validarBotaoMaisAcoesValorSolicitadoPreenchido() {
        log.info("Validando o botão Mais Ações preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        assertTrue(webDriver.getPageSource().contains(simularConsignacaoPage.retornarTextoBotaoMaisAcoes()));
    }

    @Test
    public void validarBotaoMaisAcoesValorPrestacaoPreenchido() {
        log.info("Validando o botão Mais Ações preenchendo o valor prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        assertTrue(webDriver.getPageSource().contains(simularConsignacaoPage.retornarTextoBotaoMaisAcoes()));
    }

    @Test
    public void validarTelaNovoContratoMaisAcoesValorPrestacaoPreenchido() {
        log.info("Validando a tela de novo contrato preenchendo o valor prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        simularConsignacaoPage.clicarBotaoMaisAcoes();
        simularConsignacaoPage.clicarBotaoMaisAcoesNovoContrato();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "Simulação");

        assertTrue(webDriver.getPageSource().contains("Simulação"));
    }

    @Test
    public void validarTelaNovoContratoMaisAcoesValorSolicitadoPreenchido() {
        log.info("Validando a tela de novo contrato preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        simularConsignacaoPage.clicarBotaoMaisAcoes();
        simularConsignacaoPage.clicarBotaoMaisAcoesNovoContrato();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "Simulação");

        assertTrue(webDriver.getPageSource().contains("Simulação"));
    }

    @Test
    public void validarTelaRenegociacaoMaisAcoesValorPrestacaoPreenchido() {
        log.info("Validando a tela de renegociação preenchendo o valor prestação");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(adeVlr);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        simularConsignacaoPage.clicarBotaoMaisAcoes();
        simularConsignacaoPage.clicarBotaoMaisAcoesRenegociacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "Simular Renegociação");

        assertTrue(webDriver.getPageSource().contains("Simular Renegociação"));
    }

    @Test
    public void validarTelaRenegociacaoMaisAcoesValorSolicitadoPreenchido() {
        log.info("Validando a tela de renegociação preenchendo o valor solicitado");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorSolicitado(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "RESULTADO DA SIMULAÇÃO");

        simularConsignacaoPage.clicarBotaoMaisAcoes();
        simularConsignacaoPage.clicarBotaoMaisAcoesRenegociacao();

        //aguarda texto na tela
        econsigHelper.verificaTextoPagina(webDriver, "Simular Renegociação");

        assertTrue(webDriver.getPageSource().contains("Simular Renegociação"));
    }
}
