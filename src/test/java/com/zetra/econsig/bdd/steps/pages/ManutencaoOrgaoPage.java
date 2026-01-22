package com.zetra.econsig.bdd.steps.pages;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.ManutencaoOrgaoElementMap;
import com.zetra.econsig.bdd.steps.maps.PerfilElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ManutencaoOrgaoPage extends BasePage {

	private final PerfilElementMap perfilElementMap;
	private final ManutencaoOrgaoElementMap manutencaoOrgaoElementMap;

	public ManutencaoOrgaoPage(WebDriver webDriver) {
	    super(webDriver);
	    perfilElementMap = PageFactory.initElements(webDriver, PerfilElementMap.class);
	    manutencaoOrgaoElementMap = PageFactory.initElements(webDriver, ManutencaoOrgaoElementMap.class);
	}

	public void clicarBotaoNovo() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getCurrentUrl().contains("listarPerfil"));

		perfilElementMap.botaoNovo.click();
	}

	public void preencherResponsavel(String responsavel) {
		manutencaoOrgaoElementMap.orgResponsavel.clear();
		manutencaoOrgaoElementMap.orgResponsavel.sendKeys(responsavel);
	}

	public void preencherCargo(String cargo) {
		manutencaoOrgaoElementMap.orgCargo.clear();
		manutencaoOrgaoElementMap.orgCargo.sendKeys(cargo);
	}

	public void preencherCEP(String cep) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoOrgaoElementMap.cep));

		manutencaoOrgaoElementMap.cep.clear();
		manutencaoOrgaoElementMap.cep.sendKeys(cep);
	}

	public void preencherCNPJ(String cnpj) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoOrgaoElementMap.cnpj));

		while (!manutencaoOrgaoElementMap.cnpj.getDomProperty("value").contains(cnpj)) {
			manutencaoOrgaoElementMap.cnpj.clear();
			manutencaoOrgaoElementMap.cnpj.sendKeys(cnpj);
		}
	}

	public void preencherCodigo(String codigo) {
		waitDriver.until(ExpectedConditions.visibilityOf(manutencaoOrgaoElementMap.orgCodigo));

		while (!manutencaoOrgaoElementMap.orgCodigo.getDomProperty("value").matches(codigo)) {
			manutencaoOrgaoElementMap.orgCodigo.clear();
			manutencaoOrgaoElementMap.orgCodigo.sendKeys(codigo);
		}
	}

	public void clicarExcluir() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(manutencaoOrgaoElementMap.excluirOrgao));

		manutencaoOrgaoElementMap.excluirOrgao.click();
	}

	public void preencherNome(String nome) {
		manutencaoOrgaoElementMap.orgNome.sendKeys(nome);
	}

	public void clicarBloquear() {
		ManutencaoOrgaoElementMap.opcaoBloquear.click();
	}

	public void clicarDesbloquear() {
		ManutencaoOrgaoElementMap.opcaoDesbloquear.click();
	}

	public void clicarBotaoSalvar() {
		js.executeScript("arguments[0].click()", perfilElementMap.botaoSalvar);
	}

	public void filtroPerfil(String descricao, String filtro) {
		await.pollDelay(Duration.ofSeconds(1))
		.until(() -> webDriver.getPageSource().contains("Lista de órgãos"));

		while (!perfilElementMap.campoFiltro.getDomProperty("value").matches(descricao)) {
			perfilElementMap.campoFiltro.clear();
			perfilElementMap.campoFiltro.sendKeys(descricao);
		}

		Select select = new Select(perfilElementMap.comboFiltro);
		select.selectByVisibleText(filtro);

		manutencaoOrgaoElementMap.btnPesquisar.click();

		waitDriver.until(ExpectedConditions.textToBePresentInElement(perfilElementMap.tituloPagina, "Lista de"));
	}

	public void selecionarEstabelecimento(String estabelecimento) {
		await.until(() -> webDriver.getPageSource().contains("Estabelecimento"));

		Select select = new Select(manutencaoOrgaoElementMap.estabelecimento);
		select.selectByVisibleText(estabelecimento);
	}

	public void clicarCriarNovoOrgao() {
		await.pollDelay(Duration.ofSeconds(1))
		.until(() -> webDriver.getPageSource().contains("Criar novo órgão"));

		manutencaoOrgaoElementMap.criarNovoOrgao.click();

	}
}
