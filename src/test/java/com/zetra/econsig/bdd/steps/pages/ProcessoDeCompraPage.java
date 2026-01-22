package com.zetra.econsig.bdd.steps.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.zetra.econsig.bdd.steps.maps.ProcessoDeCompraMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ProcessoDeCompraPage extends BasePage {

    private final ProcessoDeCompraMap processoDeCompraMap;

    public ProcessoDeCompraPage(WebDriver webDriver) {
        super(webDriver);
        processoDeCompraMap = PageFactory.initElements(webDriver, ProcessoDeCompraMap.class);
    }

	public void pesquisaNaTelaAcompanharPortabilidadeDeMargemConsignavel(String matricula) {
		processoDeCompraMap.cbOContratoOutraEnt.click();
		processoDeCompraMap.matricula.sendKeys(matricula);
		processoDeCompraMap.tipoPeriodo.sendKeys("--");
		js.executeScript("arguments[0].click()", processoDeCompraMap.pesquisar);
	}

	public String valorDoTituloDoCampoVencimentoDaTabela() {
		return processoDeCompraMap.tituloTabelaVencimento.getText();

	}

	public String valorCampoSituacao() {
		return processoDeCompraMap.campoSituacao.getText();
	}

	public String valorCampoVencimento() {
		return processoDeCompraMap.campoVencimentoValor.getText();
	}
}