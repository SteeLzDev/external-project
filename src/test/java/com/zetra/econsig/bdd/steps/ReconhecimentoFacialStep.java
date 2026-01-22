package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReconhecimentoFacialPage;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReconhecimentoFacialStep {

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    @Autowired
    private FuncaoService funcaoService;

    private MenuPage menuPage;
    private ReconhecimentoFacialPage reconhecimentoFacialPage;

    @Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        reconhecimentoFacialPage = new ReconhecimentoFacialPage(getWebDriver());

    }

    //configura paramentros do sistema
    @Dado("que a funcao {string} esta configurada para o papel servidor")
    public void configurarPapelFuncaoServidor(String funCodigo) {
        log.info("Dado que a funcao {} está configurada para o papel servidor", funCodigo);

        funcaoService.criarPapelFuncao(CodedValues.PAP_SERVIDOR, funCodigo);	    
    }

    @E("ativa o servico emprestimo para que o servidor possa utilizar")
    public void ativaEmprestimoServidor() {
        log.info("E ativa o servico emprestimo para que o servidor possa utilizar");

        parametroSistemaService.ativaParametroServicoServ("124", "B3858080808080808080808088887ED6", "1");

    }

    //ativa ou desativa biometria facial 
    @Dado("que esta ativa a biometria facial")
    public void ativaBiometriaFacial () {
        log.info("Dado que está ativa a biometria facial");

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, "S");	
        EConsigInitializer.limparCache();

    }

    @Dado("que esta desativada a biometria facial")
    public void desativaBiometriaFacial() {
        log.info("Dado que está desativada a biometria facial");

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, "N");
        EConsigInitializer.limparCache();
    }

    //ativa ou desativa biometria facial para o servico Emprestimo
    @E("que esta ativa a biometria facial para emprestimo")
    public void ativaBiometriaFacialParaEmprestimo() {
        log.info("E que está ativa a biometria facial para emprestimo");

        parametroSistemaService.configurarParametroServicoCse("B3858080808080808080808088887ED6", "323", "1");
        EConsigInitializer.limparCache();
    }

    @E("que esta desativada a biometria facial para emprestimo")
    public void desativaBiometriaFacialParaEmprestimo() {
        log.info("E que está desativada a biometria facial para emprestimo");

        parametroSistemaService.deletarParametroServicoCse("B3858080808080808080808088887ED6", "323", "1");
        EConsigInitializer.limparCache();
    }

    @E("o servidor clica em solitar emprestimo e faz a solicitacao")
    public void simularConsignacao() {
        log.info("E o servidor clica em solicitar emprestimo e faz a soliticação");

        menuPage.acessarMenuOperacional();
        reconhecimentoFacialPage.selecionaSolicitarEmprestimo();
        reconhecimentoFacialPage.selecionaConsignataria();
        reconhecimentoFacialPage.preencherValorPrestacao("1000");
        reconhecimentoFacialPage.preencherValorNumeroPrestacao("2");
        reconhecimentoFacialPage.confirmarSolicitacaoEmprestimo();   
    }

    @Entao("o sistema solicita o reconhecimento facial")
    public void validaReconhecimentoFacialAtivo() {
        log.info("Então o sistema solicita o reconhecimento facial");

        assertTrue(reconhecimentoFacialPage.validarReconhecimentoFacialSimularConsignacao());  
    }

    @Entao("o sistema nao solicita a biometria facial")
    public void validaReconhecimentoFacialDesativado() {
        log.info("Então o sistema não solicita a biometria facial");

        assertFalse(reconhecimentoFacialPage.validarReconhecimentoFacialSimularConsignacao());	
    }
} 