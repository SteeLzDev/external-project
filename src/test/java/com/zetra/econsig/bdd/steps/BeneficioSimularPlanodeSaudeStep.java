package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.BeneficioSimularPlanoSaudePage;
import com.zetra.econsig.helper.EconsigHelper;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BeneficioSimularPlanodeSaudeStep {

    @Autowired
    private EconsigHelper econsigHelper;

    private BeneficiariosPage beneficiariosPage;
    private BeneficioSimularPlanoSaudePage beneficioSimularPlanoSaudePage;

    @Before
    public void setUp() throws Exception {
        beneficiariosPage = new BeneficiariosPage(getWebDriver());
        beneficioSimularPlanoSaudePage = new BeneficioSimularPlanoSaudePage(getWebDriver());
    }

    @Quando("usuario seleciona operadora {string}")
    public void selecionar_operadora(String operadora) throws Throwable {
        log.info("Quando usuario seleciona operadora {}", operadora);

        beneficioSimularPlanoSaudePage.selecionarOperadora(operadora);
    }

    @Entao("exibe lista de planos {string}")
    public void exibe_lista_planos(String plano) throws Throwable {
        log.info("Então exibe lista de planos");

        assertTrue(getWebDriver().getPageSource().contains(plano));
    }

    @Quando("selecionar plano saude")
    public void selecionar_plano() throws Throwable {
        log.info("Quando selecionar plano saude");

        beneficioSimularPlanoSaudePage.clicarPlanoSaude();
    }

    @Entao("seleciona o beneficiario saude")
    public void selecionar_beneficiario() throws Throwable {
        log.info("Então seleciona o beneficiário saude");

        beneficioSimularPlanoSaudePage.clicarSelecionar();
    }

    @E("clicar em continuar")
    public void clicar_opcao_continuar() throws Throwable {
        log.info("E clicar em continuar");
        beneficiariosPage.aguardaModalSimulacaoFicarInvisivel();
        beneficioSimularPlanoSaudePage.clicarContinuar();
    }

    @Entao("exibe tela com {string}")
    public void mostraTelaResultadoBeneficiario(String resultadobeneficiario) throws Throwable {
        log.info("Então exibe tela com {}", resultadobeneficiario);

        econsigHelper.verificaTextoPagina(getWebDriver(), "Total Simulação");
        assertTrue(getWebDriver().getPageSource().contains(resultadobeneficiario));
    }

    @Quando("usuario seleciona operadora odonto {string}")
    public void selecionar_operadora_odonto(String operadoraodonto) throws Throwable {
        log.info("Quando usuario seleciona operadora odonto {}", operadoraodonto);

        beneficioSimularPlanoSaudePage.selecionarOperadoraOdonto(operadoraodonto);
    }

    @Quando("selecionar plano odonto")
    public void selecionar_plano_odonto() throws Throwable {
        log.info("Quando selecionar plano odonto");

        beneficioSimularPlanoSaudePage.clicarPlanoOdonto();
    }

    @Entao("seleciona o beneficiario odonto")
    public void selecionar_beneficiario_odonto() throws Throwable {
        log.info("Então seleciona o beneficiário odonto");

        beneficioSimularPlanoSaudePage.clicarSelecionarOdonto();
    }
}
