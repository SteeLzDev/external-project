package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.zetra.econsig.tdd.tests.maps.SimularConsignacaoElementMap;

public class SimularConsignacaoPage extends BasePage {

    private final SimularConsignacaoElementMap simularConsignacaoElementMap;

    public SimularConsignacaoPage(WebDriver webDriver) {
        super(webDriver);
        simularConsignacaoElementMap = PageFactory.initElements(webDriver, SimularConsignacaoElementMap.class);
    }

    public void preencherCampoValorPrestacao(String adeVlr) {
        simularConsignacaoElementMap.valorPrestacao.sendKeys(Keys.CONTROL + "a");
        simularConsignacaoElementMap.valorPrestacao.sendKeys(adeVlr);
    }

    public void preencherCampoValorSolicitado(String vlrLiberado) {
        simularConsignacaoElementMap.valorSolicitado.sendKeys(Keys.CONTROL + "a");
        simularConsignacaoElementMap.valorSolicitado.sendKeys(vlrLiberado);
    }

    public void preencherCampoNumeroPrestacoes(String przValor) {
        simularConsignacaoElementMap.numeroPrestacoes.sendKeys(Keys.CONTROL + "a");
        simularConsignacaoElementMap.numeroPrestacoes.sendKeys(przValor);
    }

    public void clicarBotaoSimularConsignacao() {
        simularConsignacaoElementMap.botaoSimular.click();
    }

    public String retornarTextoBotaoNovoContrato() {
        return simularConsignacaoElementMap.botaoNovoContrato.getText();
    }

    public String retornarTextoBotaoRenegociacao() {
        return simularConsignacaoElementMap.botaoRenegociacao.getText();
    }

    public String retornarTextoBotaoPortabilidade() {
        return simularConsignacaoElementMap.botaoPortabilidade.getText();
    }

    public String retornarTextoBotaoMaisAcoes() {
        return simularConsignacaoElementMap.botaoAcoes.getText();
    }

    public void clicarBotaoMaisAcoes() {
        simularConsignacaoElementMap.botaoAcoes.click();
    }

    public void clicarBotaoMaisAcoesRenegociacao() {
        await.until(() -> webDriver.getPageSource(), containsString("Renegociação"));

        simularConsignacaoElementMap.botaoMaisAcoesRenegociacao.click();
    }

    public void clicarBotaoMaisAcoesNovoContrato() {
        await.until(() -> webDriver.getPageSource(), containsString("Novo contrato"));

        simularConsignacaoElementMap.botaoMaisAcoesNovoContrato.click();
    }
}

