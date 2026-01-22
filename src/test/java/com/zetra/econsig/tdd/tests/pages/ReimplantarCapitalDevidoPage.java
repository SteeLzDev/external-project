package com.zetra.econsig.tdd.tests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.tdd.tests.maps.ReimplantarCapitalDevidoElementMap;

public class ReimplantarCapitalDevidoPage extends BasePage {
    private final ReimplantarCapitalDevidoElementMap reimplantarCapitalDevidoElementMap;

    public ReimplantarCapitalDevidoPage(WebDriver webDriver) {
        super(webDriver);
        reimplantarCapitalDevidoElementMap = PageFactory.initElements(webDriver, ReimplantarCapitalDevidoElementMap.class);
    }

    public void preencherCPF(String cpf) {
    	waitDriver.until(ExpectedConditions.textToBePresentInElement(reimplantarCapitalDevidoElementMap.txtInformacao,
				"matrícula ou CPF do servidor para a pesquisa"));

		reimplantarCapitalDevidoElementMap.cpf.clear();
		reimplantarCapitalDevidoElementMap.cpf.click();
		reimplantarCapitalDevidoElementMap.cpf.sendKeys(cpf);
    }

    public void preencherMatricula(String matricula) {
    	waitDriver.until(ExpectedConditions.textToBePresentInElement(reimplantarCapitalDevidoElementMap.txtInformacao,
				"matrícula ou CPF do servidor para a pesquisa"));

		reimplantarCapitalDevidoElementMap.matricula.clear();
		reimplantarCapitalDevidoElementMap.matricula.click();
		reimplantarCapitalDevidoElementMap.matricula.sendKeys(matricula);
    }

    public void preencherADE(String ade) {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(reimplantarCapitalDevidoElementMap.txtInformacao,
				"matrícula ou CPF do servidor para a pesquisa"));

		reimplantarCapitalDevidoElementMap.numeroADE.clear();
		reimplantarCapitalDevidoElementMap.numeroADE.click();
		reimplantarCapitalDevidoElementMap.numeroADE.sendKeys(ade);
		reimplantarCapitalDevidoElementMap.adicionarAdeLista.click();
	}

    public void clicarPesquisar() {
    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.botaoPesquisar).perform();
    	js.executeScript("arguments[0].click()", reimplantarCapitalDevidoElementMap.botaoPesquisar);
    }

    public void clicarReimplantarCapitalDevido () {
    	waitDriver.until(ExpectedConditions.elementToBeClickable(reimplantarCapitalDevidoElementMap.linkReimplantarCapitalDevido));
    	js.executeScript("arguments[0].click()", reimplantarCapitalDevidoElementMap.linkReimplantarCapitalDevido);
    }

    public void clicarProximo() {
    	waitDriver.until(ExpectedConditions.elementToBeClickable(reimplantarCapitalDevidoElementMap.botaoConfirmar));
    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.botaoConfirmar).perform();
    	js.executeScript("arguments[0].click()", reimplantarCapitalDevidoElementMap.botaoConfirmar);
    }

    public void clicarOkAlerta() {
    	waitDriver.until(ExpectedConditions.alertIsPresent());
    	webDriver.switchTo().alert().accept();
    }

    public void fecharPopUpMatricula() {
    	reimplantarCapitalDevidoElementMap.matricula.click();
    }

    public void fecharPopUpCapital() {
    	reimplantarCapitalDevidoElementMap.novoPrazoAde.click();
    }

    public void preencherNovosValores(String novoVlrAde, String novoPrazoAde, String motivoDaOp, String obsAde) {
    	preencherNovoVlrAde(novoVlrAde);
    	preencherNovoPrazoAde(novoPrazoAde);
    	selecionarMotivoDaOperacao(motivoDaOp);
    	preencherObservacaoAde(obsAde);
    }

    public void preencherNovoVlrAde(String novoVlrAde) {
    	waitDriver.until(ExpectedConditions.visibilityOf(reimplantarCapitalDevidoElementMap.novoValorAde));
    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.novoValorAde).perform();

		reimplantarCapitalDevidoElementMap.novoValorAde.clear();
		reimplantarCapitalDevidoElementMap.novoValorAde.click();
		reimplantarCapitalDevidoElementMap.novoValorAde.sendKeys(novoVlrAde);
    }

    public void preencherNovoPrazoAde(String novoPrazoAde) {
    	waitDriver.until(ExpectedConditions.visibilityOf(reimplantarCapitalDevidoElementMap.novoPrazoAde));
    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.novoPrazoAde).perform();

    	reimplantarCapitalDevidoElementMap.novoPrazoAde.clear();
    	reimplantarCapitalDevidoElementMap.novoPrazoAde.click();
		reimplantarCapitalDevidoElementMap.novoPrazoAde.sendKeys(novoPrazoAde);
    }

    public void preencherObservacaoAde(String obsAde) {
    	waitDriver.until(ExpectedConditions.visibilityOf(reimplantarCapitalDevidoElementMap.obsAde));
    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.obsAde).perform();

		reimplantarCapitalDevidoElementMap.obsAde.clear();
		reimplantarCapitalDevidoElementMap.obsAde.click();
		reimplantarCapitalDevidoElementMap.obsAde.sendKeys(obsAde);
    }

    public void selecionarMotivoDaOperacao(String motivoDaOp) {
    	waitDriver.until(ExpectedConditions.visibilityOf(reimplantarCapitalDevidoElementMap.motivoDaOp));

    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.novoPrazoAde).perform();
		reimplantarCapitalDevidoElementMap.motivoDaOp.sendKeys(motivoDaOp);
    }

    public void clicarVoltar() {
    	waitDriver.until(ExpectedConditions.elementToBeClickable(reimplantarCapitalDevidoElementMap.botaoVoltar));
    	new Actions(webDriver).scrollToElement(reimplantarCapitalDevidoElementMap.botaoVoltar).perform();
    	js.executeScript("arguments[0].click()", reimplantarCapitalDevidoElementMap.botaoVoltar);
    }
}