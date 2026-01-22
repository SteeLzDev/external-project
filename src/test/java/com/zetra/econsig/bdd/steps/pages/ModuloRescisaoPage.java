package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.ModuloRescisaoElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ModuloRescisaoPage extends BasePage {

	private final ModuloRescisaoElementMap moduloRescisaoElementMap;

	public ModuloRescisaoPage(WebDriver webDriver) {
		super(webDriver);
		moduloRescisaoElementMap = PageFactory.initElements(webDriver, ModuloRescisaoElementMap.class);
	}

	public void btnAdicionarServidor() {
		moduloRescisaoElementMap.btnAdicionarServidor.click();
	}

	public String valorCampoNomeDaLista() {
		return moduloRescisaoElementMap.campoColunaNome.getText();
	}

	public String valorCampoCpfDaLista() {
		return moduloRescisaoElementMap.campoColunaCpf.getText();
	}

	public void btnConfirmarRescisao() {
		moduloRescisaoElementMap.btnConfirmarRescisao.click();
	}

	public void aguardaInfoMsgFicarInvisivel() {
		waitDriver.until(ExpectedConditions.invisibilityOfAllElements(moduloRescisaoElementMap.msgInfo));
	}

	public void aguardaMsgNenhumRegistroEncontrado() {
		waitDriver.until(ExpectedConditions.visibilityOf(moduloRescisaoElementMap.msgNenhumRegistroEncontrado));
	}

	public String verificaMensagemSucesso() {
		return moduloRescisaoElementMap.msgSucesso.getText();
	}

	public void excluirServidor() {
		waitDriver.until(ExpectedConditions.visibilityOf(moduloRescisaoElementMap.excluirServidor));
		js.executeScript("arguments[0].click()", moduloRescisaoElementMap.excluirServidor);
		// moduloRescisaoElementMap.excluirServidor.click();
	}

	public void clicarVisualizar() {
		moduloRescisaoElementMap.visualizarDadosColaborador.click();
	}

	public void clicarInformarVerbaRescisoria() {
		moduloRescisaoElementMap.informarVerbaRescisoria.click();

		await.until(() -> webDriver.getPageSource(), containsString("Contratos para retenção da verba rescisória"));
	}

	public String retornarSituacaoColaborador() {
		return moduloRescisaoElementMap.txtSituacao.getText();
	}

	public void preencherValorDisponivelRetencao(String valor) {
		moduloRescisaoElementMap.campoValorDisponivelRetencao.sendKeys(valor);
		moduloRescisaoElementMap.campoValorDisponivelRetencao.sendKeys(Keys.TAB);
		
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> retornarValorRetencao().contains("00,00"));
	}

	public void clicarSalvar() {
		moduloRescisaoElementMap.btnSalvar.click();
	}

	public String retornarValorRetido() {
		return moduloRescisaoElementMap.txtValorRetido.getText();
	}
	
	public String retornarValorRetencao() {
		return moduloRescisaoElementMap.txtValorRetencao.getText();
	}

	public String retornarSituacaoAde() {
		return moduloRescisaoElementMap.txtSituacaoAde.getText();
	}

	public String retornarIdentificador() {
		return moduloRescisaoElementMap.txtIdentificadorAde.getText();
	}

	public void clicarLinkRelacionamento() {
		actions.moveToElement(moduloRescisaoElementMap.linkRelacionamentoAde).perform();
		moduloRescisaoElementMap.linkRelacionamentoAde.click();

		await.until(() -> webDriver.getPageSource(), containsString("Dados da consignação"));
	}
}
