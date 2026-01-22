package com.zetra.econsig.bdd.steps.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.ConfirmarReservaMap;
import com.zetra.econsig.bdd.steps.maps.EconsigElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ConfirmarReservaPage extends BasePage {

    private final ConfirmarReservaMap confirmarReservaMap;
    private final EconsigElementMap econsigElementMap;

    public ConfirmarReservaPage(WebDriver webDriver) {
        super(webDriver);
        confirmarReservaMap = PageFactory.initElements(webDriver, ConfirmarReservaMap.class);
        econsigElementMap = PageFactory.initElements(webDriver, EconsigElementMap.class);
    }

    public void carregarPaginaConfirmarReserva() {
        waitDriver.until(ExpectedConditions.visibilityOf(confirmarReservaMap.pageTitle));
    }

    public void selecionarTmo() {
        confirmarReservaMap.tmoSelect.findElements(By.tagName("option")).stream().filter(option -> option.getText().equals("Outros")).findFirst().ifPresent(WebElement::click);
        confirmarReservaMap.tmoObs.sendKeys("confirmacao de reserva");
    }

    public void btnEnvia() {
        js.executeScript("arguments[0].click()", confirmarReservaMap.btnEnvia);
    }

    public void exibePaginaErro() {
        waitDriver.until(ExpectedConditions.visibilityOf(econsigElementMap.errorAlertDiv));
    }

}
