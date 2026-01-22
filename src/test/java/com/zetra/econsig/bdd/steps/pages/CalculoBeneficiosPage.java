package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.CalculoBeneficiosElementMap;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class CalculoBeneficiosPage extends BasePage {

    private final CalculoBeneficiosElementMap calculoBeneficiosElementMap;

    public CalculoBeneficiosPage(WebDriver webDriver) {
        super(webDriver);
        calculoBeneficiosElementMap = PageFactory.initElements(webDriver, CalculoBeneficiosElementMap.class);
    }

	public void clicarNovoCalculo() {
		calculoBeneficiosElementMap.novoCalculo.click();

		await.until(() -> webDriver.getPageSource(),
				containsString("Inclusão de novo cálculo benefício"));
	}

	public void clicarAplicarReajuste() {
		calculoBeneficiosElementMap.aplicarReajuste.click();

		await.until(() -> webDriver.getPageSource().contains("Informe os dados para o reajuste"));
	}

	public void clicarAplicarReajusteCalculo() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(calculoBeneficiosElementMap.aplicarReajusteCalculo));
		js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.aplicarReajusteCalculo);
	}

	public void clicarAplicarReajusteCalculoSemSucesso() {
		while (!SeleniumHelper.isAlertPresent(webDriver)) {
			js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.aplicarReajusteCalculo);
		}
	}

	public void clicarMaisAcoes() {
		while (calculoBeneficiosElementMap.botaoAcoes.getDomAttribute("aria-expanded").contains("false")) {
			calculoBeneficiosElementMap.botaoAcoes.click();
		}
	}

	public void selecionarTipoBeneficiario(String tipoBeneficiario) {
		await.pollDelay(2, TimeUnit.SECONDS)
			.until(() -> webDriver.getPageSource().contains("Tipo do beneficiário"));

		while (!calculoBeneficiosElementMap.tipoBeneficiario.getDomProperty("value").contains("1")) {
			calculoBeneficiosElementMap.tipoBeneficiario.sendKeys(tipoBeneficiario);
		}
	}

	public void clicarPesquisar() {
		await.pollDelay(1, TimeUnit.SECONDS)
			.until(() -> webDriver.getPageSource().contains("Pesquisar"));

		try {
			webDriver.findElement(By.id("Filtrar")).click();
		} catch(StaleElementReferenceException e) {
			WebElement button = webDriver.findElement(By.id("Filtrar"));
			button.click();
		}
	}

	public void preencherPercentualReajuste(String valor) {
		await.pollDelay(2, TimeUnit.SECONDS)
			.until(() -> webDriver.getPageSource().contains("Percentual de reajuste"));

		try {
			webDriver.findElement(By.id("valorReajuste")).sendKeys(valor);
		} catch(StaleElementReferenceException e) {
			WebElement input = webDriver.findElement(By.id("valorReajuste"));
			input.sendKeys(valor);
		}
	}

	public void selecionarValorBeneficio() {
		js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.aplicarSobreBeneficio);
	}

	public void selecionarValorSubsidio() {
		js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.aplicarSobreSubsidio);
	}

	public void selecionarFaixaSalarial() {
		calculoBeneficiosElementMap.aplicarSobreFaixaSalarial.click();
	}

	public void selecionarCalculoBeneficio() {
		calculoBeneficiosElementMap.checkAplicarReajuste.click();
	}

	public void cadastrarCalculoBeneficio() {
		selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		selecionarBeneficio("DENTAL UNI");
		calculoBeneficiosElementMap.tipoBeneficio.sendKeys("Dependente");
		calculoBeneficiosElementMap.grauParentesco.sendKeys("Companheiro");
		calculoBeneficiosElementMap.inicioFaixaSalarial.sendKeys("400");
		calculoBeneficiosElementMap.finalFaixaSalarial.sendKeys("8000");
		calculoBeneficiosElementMap.inicioFaixaEtaria.sendKeys("02");
		calculoBeneficiosElementMap.fimFaixaEtaria.sendKeys("90");
		calculoBeneficiosElementMap.valorBeneficio.sendKeys("8000");
		calculoBeneficiosElementMap.valorSubsidio.sendKeys("400");
	}

	public void editarCalculoBeneficio() {
		calculoBeneficiosElementMap.inicioFaixaSalarial.clear();
		calculoBeneficiosElementMap.finalFaixaSalarial.clear();
		calculoBeneficiosElementMap.inicioFaixaEtaria.clear();
		calculoBeneficiosElementMap.fimFaixaEtaria.clear();
		calculoBeneficiosElementMap.valorBeneficio.clear();
		calculoBeneficiosElementMap.valorSubsidio.clear();

		calculoBeneficiosElementMap.inicioFaixaSalarial.sendKeys("400");
		calculoBeneficiosElementMap.finalFaixaSalarial.sendKeys("8000");
		calculoBeneficiosElementMap.inicioFaixaEtaria.sendKeys("02");
		calculoBeneficiosElementMap.fimFaixaEtaria.sendKeys("90");
		calculoBeneficiosElementMap.valorBeneficio.sendKeys("8000");
		calculoBeneficiosElementMap.valorSubsidio.sendKeys("400");
	}

	public void selecionarBeneficio(String beneficio) {
		Select select = new Select(calculoBeneficiosElementMap.beneficio);
		select.selectByVisibleText(beneficio);
	}

	public void selecionarOrgao(String orgao) {
		Select select = new Select(calculoBeneficiosElementMap.orgao);
		select.selectByVisibleText(orgao);
	}

	public void clicarAtivarTabela() {
		iniciarTabela();
		js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.ativarTabela);
	}

	public void clicarExcluirTabelaIniciada() {
		js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.excluirTabelaIniciada);
	}

	public void clicarIniciarTabela() {
		ativarTabela();
		js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.iniciarTabela);
	}

	public String getOrgao() {
		return calculoBeneficiosElementMap.txtOrgao.getText();
	}

	public String getBeneficioTitular() {
		return calculoBeneficiosElementMap.txtBeneficioTitular.getText();
	}

	public String getBeneficioDependente() {
		return calculoBeneficiosElementMap.txtBeneficioDependente.getText();
	}

	public String getBeneficioAgregado() {
		return calculoBeneficiosElementMap.txtBeneficioAgregado.getText();
	}

	public void ativarTabela() {
		if (webDriver.getPageSource().contains("Ativar tabela")) {
			js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.ativarTabela);
			//calculoBeneficiosElementMap.ativarTabela.click();

			waitDriver.until(ExpectedConditions.alertIsPresent());
			webDriver.switchTo().alert().accept();
		}
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Iniciar tabela"));
	}

	public void iniciarTabela() {
		if (webDriver.getPageSource().contains("Iniciar tabela")) {
			js.executeScript("arguments[0].click()", calculoBeneficiosElementMap.iniciarTabela);
		}
		await.until(() -> webDriver.getPageSource().contains("Ativar tabela"));
	}

	public String getInicioFaixaTitular() {
		return calculoBeneficiosElementMap.txtInicioFaixa.getText();
	}

	public String getFimFaixaTitular() {
		return calculoBeneficiosElementMap.txtFimFaixa.getText();
	}

	public String getValorBeneficioTitular() {
		return calculoBeneficiosElementMap.txtValorBeneficio.getText();
	}

	public String getValorSubsidioTitular() {
		return calculoBeneficiosElementMap.txtValorSubsidio.getText();
	}
}
