package com.zetra.econsig.bdd.steps.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.EditarParametroPostoGraduacaoMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;


public class EditarParametroPostoGraduacaoPage extends BasePage {

    private final EditarParametroPostoGraduacaoMap modalElement;

    public EditarParametroPostoGraduacaoPage(WebDriver webDriver) {
        super(webDriver);
        modalElement = PageFactory.initElements(webDriver, EditarParametroPostoGraduacaoMap.class);
    }

    public String tituloPagina() {
		return modalElement.tituloPagina.getText();
	}

	public String tituloTela() {
		return modalElement.tituloTela.getText();
	}

	public boolean temOServico(String svcCodigo) {
		List<WebElement> listOptions = modalElement.svcCodigo.findElements(By.tagName("option"));
		return !listOptions.stream().filter(option -> option.getDomProperty("value").equals("4C868080808080808080808088886275")).toList().isEmpty();
	}

	public void selecionarServico() {
        waitDriver.until(ExpectedConditions.visibilityOfAllElements(modalElement.svcCodigo));
		modalElement.svcCodigo.sendKeys("PECÚLIO");
		// Caso o sendKeys já faça o onChange do campo ser disparado, então espera novamente que o elemento esteja na página
        waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(modalElement.svcCodigo));
        // Se o sendKeys não fizer o onChange, faz novamente o blur do campo para forçar o disparo do onChange
		js.executeScript("arguments[0].blur()", modalElement.svcCodigo);
	}

	public String verificaValorCampoServico() {
		waitDriver.until(ExpectedConditions.visibilityOfAllElements(modalElement.svcCodigo));
		return modalElement.svcCodigo.getDomProperty("value");
	}

	public void preencherCamposPostoDeGraduacaoSoldado(String campoSoldado) {
		modalElement.campoSoldado.sendKeys(campoSoldado);
	}

	public void preencherCamposPostoDeGraduacaoCapitao(String campoCapitao) {
		modalElement.campoCapitao.sendKeys(campoCapitao);
	}
	public void preencherCamposPostoDeGraduacaoTenente(String campoTenente) {
		modalElement.campoTenente.sendKeys(campoTenente);
	}

	public boolean tester(String svcCodigo) {
		List<WebElement> listOptions = modalElement.svcCodigo.findElements(By.tagName("option"));
		return !listOptions.stream().filter(option -> option.getDomProperty("value").equals("4C868080808080808080808088886275")).toList().isEmpty();
	}

	public void salvarValoresPosto() {
		modalElement.enviarDados.click();
	}

	public String mensagemSucessoAoSalvar() {
		return modalElement.mensagemDeSucesso.getText();
	}
}