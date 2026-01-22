package com.zetra.econsig.bdd.steps.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.BeneficioSimularPlanoSaudeElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class BeneficioSimularPlanoSaudePage extends BasePage {

    private final BeneficioSimularPlanoSaudeElementMap beneficioSimularPlanoSaudeElementMap;

    public BeneficioSimularPlanoSaudePage(WebDriver webDriver) {
        super(webDriver);
        beneficioSimularPlanoSaudeElementMap = PageFactory.initElements(webDriver, BeneficioSimularPlanoSaudeElementMap.class);
    }

	public void selecionarOperadora(String operadora) {

		while (beneficioSimularPlanoSaudeElementMap.operadora.getDomProperty("value").matches("1")) {
			beneficioSimularPlanoSaudeElementMap.operadora.sendKeys(operadora);
			beneficioSimularPlanoSaudeElementMap.operadora.click();
			actions.sendKeys(Keys.TAB);
		}

		waitDriver.until(ExpectedConditions.elementToBeClickable(beneficioSimularPlanoSaudeElementMap.listaplanosaude));
	}

	public void selecionarOperadoraOdonto(String operadoraodonto) {
		await.pollDelay(5, TimeUnit.SECONDS).until(
				 () -> webDriver.getPageSource().contains("Plano de Odontológico"));

		while (beneficioSimularPlanoSaudeElementMap.operadoraodonto.getDomProperty("value").matches("1")) {
			beneficioSimularPlanoSaudeElementMap.operadoraodonto.sendKeys(operadoraodonto);
			beneficioSimularPlanoSaudeElementMap.operadoraodonto.click();
			actions.sendKeys(Keys.TAB);
		}

		waitDriver.until(ExpectedConditions.elementToBeClickable(beneficioSimularPlanoSaudeElementMap.listaplanoodonto));
	}

	public void clicarSelecionar() {
		beneficioSimularPlanoSaudeElementMap.beneficiario.click();
	}

	public void clicarSelecionarOdonto() {
		beneficioSimularPlanoSaudeElementMap.beneficiarioOdonto.click();
	}

	public void clicarContinuar() {
		js.executeScript("arguments[0].click()", beneficioSimularPlanoSaudeElementMap.continuar);
	}

	public void clicarPlanoSaude() {
		beneficioSimularPlanoSaudeElementMap.listaplanosaude.click();
	}

	public void clicarPlanoOdonto() {
		beneficioSimularPlanoSaudeElementMap.listaplanoodonto.click();
	}
}
