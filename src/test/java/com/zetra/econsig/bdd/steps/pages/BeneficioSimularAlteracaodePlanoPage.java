package com.zetra.econsig.bdd.steps.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.BeneficioSimularAlteracaodePlanoElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class BeneficioSimularAlteracaodePlanoPage extends BasePage {

    private final BeneficioSimularAlteracaodePlanoElementMap beneficioSimularAlteracaodePlanoElementMap;

    public BeneficioSimularAlteracaodePlanoPage(WebDriver webDriver) {
        super(webDriver);
        beneficioSimularAlteracaodePlanoElementMap = PageFactory.initElements(webDriver, BeneficioSimularAlteracaodePlanoElementMap.class);
    }

	public void clicarPlanoSaude() {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Plano De Saúde"));

		beneficioSimularAlteracaodePlanoElementMap.botaoPlanodeSaude.click();
	}

	public void selecionarOperadoraAltera(String operadora) {
		Select select = new Select(beneficioSimularAlteracaodePlanoElementMap.operadoraAlteraPlano);
		select.selectByVisibleText(operadora);
		waitDriver.until(ExpectedConditions.visibilityOfAllElements(beneficioSimularAlteracaodePlanoElementMap.listaplanoaltera));

		waitDriver.until(
				ExpectedConditions.elementToBeClickable(beneficioSimularAlteracaodePlanoElementMap.listaplanoaltera));
	}

	public void clicarSelecionarbeneficiario() {
		beneficioSimularAlteracaodePlanoElementMap.beneficiario.click();
	}
}