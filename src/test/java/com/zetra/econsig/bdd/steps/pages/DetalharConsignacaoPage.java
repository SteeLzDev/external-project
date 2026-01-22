package com.zetra.econsig.bdd.steps.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.DetalharConsignacaoMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class DetalharConsignacaoPage extends BasePage {

    private final DetalharConsignacaoMap detalharConsignacaoMap;

    public DetalharConsignacaoPage(WebDriver webDriver) {
        super(webDriver);
        detalharConsignacaoMap = PageFactory.initElements(webDriver, DetalharConsignacaoMap.class);
    }

    public void carregarDetalheConsignacao() {
        await.pollDelay(2, TimeUnit.SECONDS).until(() -> webDriver.getPageSource().contains("Ações"));

        waitDriver.until(ExpectedConditions.visibilityOf(detalharConsignacaoMap.botaoAcoes));
    }

    public void clicarBotaoAcoes() {
        detalharConsignacaoMap.botaoAcoes.click();
    }

    public void selecionarAcao(String acao) {
        detalharConsignacaoMap.dropDownItens.stream().filter(item -> acao.trim().equals(item.getText().trim())).findFirst().ifPresent(WebElement::click);
    }

    public String checkMensagemSessaoSucersso() {
        waitDriver.until(ExpectedConditions.visibilityOf(detalharConsignacaoMap.msgSuccess));

        return detalharConsignacaoMap.msgSuccess.getText().trim();
    }

}
