package com.zetra.econsig.bdd.steps.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.ConfirmarSolicitacaoMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ConfirmarSolicitacaoPage extends BasePage {

    private final ConfirmarSolicitacaoMap confirmarSolicitacaoMap;

    public ConfirmarSolicitacaoPage(WebDriver webDriver) {
        super(webDriver);
        confirmarSolicitacaoMap = PageFactory.initElements(webDriver, ConfirmarSolicitacaoMap.class);
    }

	public void carregarPaginaConfirmarSolicitacao() {
		waitDriver.until(ExpectedConditions.visibilityOf(confirmarSolicitacaoMap.btnEnvia));
	}

	public void btnEnvia() {
		js.executeScript("arguments[0].click()", confirmarSolicitacaoMap.btnEnvia);
	}
}
