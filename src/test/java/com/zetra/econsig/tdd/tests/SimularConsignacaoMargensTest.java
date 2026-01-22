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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class SimularConsignacaoMargensTest extends BaseTest {
	
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
    
    @Autowired
    private RegistroServidorService registroServidorService;

    private final LoginInfo loginServidor = LoginValues.servidor1;

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
        
        registroServidorService.alterarRseMargemRest("123456", BigDecimal.valueOf(-9680d));

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
        
        registroServidorService.alterarRseMargemRest("123456", BigDecimal.valueOf(9680d));
    }
    
    @Test
    public void validarTelaErroServidorPossuiMargemNegativa() {
        log.info("Validando tela de erro da simulação quando o servidor possui margem negativa.");

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
        econsigHelper.verificaTextoPagina(webDriver, "Servidor não possui margem suficiente.");

        assertTrue(webDriver.getPageSource().contains("Servidor não possui margem suficiente."));
        
    }
    
    @Test
    public void validarMensagemPopUpValorPrestacaoMaiorMargemConsignavel() {
        log.info("Validando a mensagem quando o servidor realiza uma simulação através do valor da prestação maior que sua margem.");

        // login no sistema
        loginPage.loginServidor(loginServidor);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuOperacionalSolicitarEmprestimo();

        // simula consignação
        simularConsignacaoPage.preencherCampoValorPrestacao(vlrLiberado);
        simularConsignacaoPage.preencherCampoNumeroPrestacoes(przVlr);
        simularConsignacaoPage.clicarBotaoSimularConsignacao();

        assertEquals("O valor da prestação não pode ser maior do que a margem disponível.", econsigHelper.getMensagemPopUp(webDriver));
    }
    
    @Test
    public void validarSimulacaoMargemZerada() {
        log.info("Validar tela de simulação quando margem está zerada.");
        
        // Negativa a margem do servidor
        registroServidorService.alterarRseMargemRest("123456", BigDecimal.valueOf(0.00));

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

        assertFalse(webDriver.getPageSource().contains("Mais ações"));
    }
}
