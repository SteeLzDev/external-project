package com.zetra.econsig.bdd.steps.pages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.ManutencaoPostoElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ManutencaoPostoPage extends BasePage {

    private final ManutencaoPostoElementMap manutencaoPostoElementMap;

    public ManutencaoPostoPage(WebDriver webDriver) {
        super(webDriver);
        manutencaoPostoElementMap = PageFactory.initElements(webDriver, ManutencaoPostoElementMap.class);
    }

    public void clicarEditar(String codigo) {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Lista de postos"));

		WebElement main = webDriver.findElement(By.cssSelector(".table"));
		List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
		for (WebElement row : rows) {
			if (row.findElement(By.xpath(".//td")).getText().equals(codigo)) {
				WebElement editar = row.findElement(By.linkText("Editar"));
				editar.click();
				break;
			}
		}
	}

    public void preencherCodigo(String codigo) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoPostoElementMap.codigo));

		manutencaoPostoElementMap.codigo.clear();
		while (!manutencaoPostoElementMap.codigo.getDomProperty("value").contains(codigo)) {
			manutencaoPostoElementMap.codigo.sendKeys(codigo);
		}
	}

    public void preencherDescricao(String descricao) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoPostoElementMap.descricao));

		manutencaoPostoElementMap.descricao.clear();
		manutencaoPostoElementMap.descricao.sendKeys(descricao);
	}

    public void preencherValorSaldo(String valorSaldo) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoPostoElementMap.valorSaldo));

		manutencaoPostoElementMap.valorSaldo.clear();
		manutencaoPostoElementMap.valorSaldo.sendKeys(valorSaldo);
	}

    public void preencherPercentualTaxaCond(String perTxUsoCond) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoPostoElementMap.percentualTaxaCond));

		manutencaoPostoElementMap.percentualTaxaCond.clear();
		manutencaoPostoElementMap.percentualTaxaCond.sendKeys(perTxUsoCond);
	}

    public void clicarSalvar() {
		js.executeScript("arguments[0].click()", manutencaoPostoElementMap.salvar);
	}
}
