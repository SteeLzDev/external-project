package com.zetra.econsig.bdd.steps.pages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.BeneficioListaContratoElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class BeneficioListaContratoPage extends BasePage {

    private final BeneficioListaContratoElementMap beneficioListaContratoElementMap;

    public BeneficioListaContratoPage(WebDriver webDriver) {
        super(webDriver);
        beneficioListaContratoElementMap = PageFactory.initElements(webDriver, BeneficioListaContratoElementMap.class);
    }

	public void selecionarConsignataria(String consignataria) {
		waitDriver.until(ExpectedConditions.visibilityOf(beneficioListaContratoElementMap.csa));

		Select select = new Select(beneficioListaContratoElementMap.csa);
		select.selectByValue(consignataria);

		await.pollDelay(1, TimeUnit.SECONDS).until(
				() -> webDriver.getPageSource().contains("Contratos Aguardando Exclusão Operadora"));
	}

	public void clicarEditar(String matricula) {
		await.pollDelay(1, TimeUnit.SECONDS).until(
				() -> webDriver.getPageSource().contains("Contratos Aguardando Exclusão Operadora"));

		WebElement main = webDriver.findElement(By.cssSelector(".table"));
		List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
		for (WebElement row : rows) {
			if (row.findElement(By.xpath(".//td")).getText().equals(matricula)) {
				WebElement editar = row.findElement(By.linkText("Editar"));
				editar.click();
				break;
			}
		}
	}

	public void preencherNumeroContrato(String nrocontrato) {
		waitDriver.until(ExpectedConditions.visibilityOf(beneficioListaContratoElementMap.nrocontratoben));

		while (!beneficioListaContratoElementMap.nrocontratoben.getDomProperty("value").contains(nrocontrato)) {
			beneficioListaContratoElementMap.nrocontratoben.clear();
			beneficioListaContratoElementMap.nrocontratoben.sendKeys(nrocontrato);
		}
	}

	public void preencherDataIniVigencia(String datainivigencia) {
		waitDriver
				.until(ExpectedConditions.visibilityOf(beneficioListaContratoElementMap.datainiciovigenciacontratoben));

		while (!beneficioListaContratoElementMap.datainiciovigenciacontratoben.getDomProperty("value")
				.matches(datainivigencia)) {
			beneficioListaContratoElementMap.datainiciovigenciacontratoben.clear();
			beneficioListaContratoElementMap.datainiciovigenciacontratoben.sendKeys(datainivigencia);
		}
	}

	public void preencherDataFimVigencia(String datafimvigencia) {
		waitDriver.until(ExpectedConditions.visibilityOf(beneficioListaContratoElementMap.datafimvigenciacontratoben));

		while (!beneficioListaContratoElementMap.datafimvigenciacontratoben.getDomProperty("value")
				.matches(datafimvigencia)) {
			beneficioListaContratoElementMap.datafimvigenciacontratoben.clear();
			beneficioListaContratoElementMap.datafimvigenciacontratoben.sendKeys(datafimvigencia);
		}
	}
}
