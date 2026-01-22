package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.zetra.econsig.bdd.steps.maps.ReativarContratoBeneficioElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ReativarContratoBeneficioPage extends BasePage {

    private final ReativarContratoBeneficioElementMap reativarContratoBeneficioElementMap;

    public ReativarContratoBeneficioPage(WebDriver webDriver) {
        super(webDriver);
        reativarContratoBeneficioElementMap = PageFactory.initElements(webDriver, ReativarContratoBeneficioElementMap.class);
    }

    public void selecionarConsignataria(String operadora) {
		while (reativarContratoBeneficioElementMap.operadora.getDomProperty("value").matches("1")) {
			reativarContratoBeneficioElementMap.operadora.sendKeys(operadora);
			js.executeScript("arguments[0].click()", reativarContratoBeneficioElementMap.operadora);
		}
	}

	public void selecionarPlano() {
		await.until(() -> webDriver.getPageSource(), containsString("ENFERMARIA - REDE RESTRITA"));
		js.executeScript("arguments[0].click()", reativarContratoBeneficioElementMap.plano);
	}

	public void selecionarBeneficiario() {
		await.until(() -> webDriver.getPageSource(), containsString("VANDA IZABEL ANTAO"));
		reativarContratoBeneficioElementMap.selecionarBeneficiario.click();
	}

	public void clicarContinuar() {
	    js.executeScript("arguments[0].click()", reativarContratoBeneficioElementMap.btnContinuar);
	}

	public void selecionarContrato() {
		reativarContratoBeneficioElementMap.contrato.click();
	}
}