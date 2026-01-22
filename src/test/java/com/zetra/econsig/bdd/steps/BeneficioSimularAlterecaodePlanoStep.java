package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;

import com.zetra.econsig.bdd.steps.pages.BeneficioSimularAlteracaodePlanoPage;
import com.zetra.econsig.bdd.steps.pages.ReativarContratoBeneficioPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BeneficioSimularAlterecaodePlanoStep {

    private BeneficioSimularAlteracaodePlanoPage beneficioSimularAlteracaodePlanoPage;
    private ReativarContratoBeneficioPage reativarContratoBeneficioPage;

    @Before
    public void setUp() throws Exception {
        beneficioSimularAlteracaodePlanoPage = new BeneficioSimularAlteracaodePlanoPage(getWebDriver());
        reativarContratoBeneficioPage = new ReativarContratoBeneficioPage(getWebDriver());
    }

	@Quando("clicar em Plano De Saude")
	public void selecionar_plano_saude () throws Throwable {
		log.info("Quando clicar em Plano De Saúde");

		beneficioSimularAlteracaodePlanoPage.clicarPlanoSaude();
	}

	@Quando("usuario seleciona operadora para alteracao {string}")
	public void selecionar_operado_altera_plano (String operadora) throws Throwable {
		log.info("Quando usuario seleciona operadora para alteração {}", operadora);

		beneficioSimularAlteracaodePlanoPage.selecionarOperadoraAltera(operadora);
	}

	@Entao("seleciona o beneficiario saude para alteracao")
	public void selecionar_beneficiario_altera_saude () throws Throwable {
		log.info("Então seleciona o beneficiário saude para alteração");

		beneficioSimularAlteracaodePlanoPage.clicarSelecionarbeneficiario();
	}

	@Quando("selecionar plano saude para alteracao")
	public void selecionar_plano_saude_alteracao() throws Throwable {
		log.info("Quando selecionar plano saude para alteração");

		reativarContratoBeneficioPage.selecionarPlano();
	}
}