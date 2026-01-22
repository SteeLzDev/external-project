package com.zetra.econsig.tdd.tests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.maps.GrupoServicoElementMap;

public class GrupoServicoPage extends BasePage {

    private static final String grpIdentificador = "Z99";
    private static final String grpDescricao = "Grupo Emprestimo";
    private static final String grpQuantidade = "1";
    private static final String grpQuantidadePorCsa = "2";

    private final GrupoServicoElementMap grupoServicoElementMap;

	public GrupoServicoPage(WebDriver webDriver) {
	    super(webDriver);
        grupoServicoElementMap = PageFactory.initElements(webDriver, GrupoServicoElementMap.class);
	}

	public void clicarCriarNovoGrupoServico() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(grupoServicoElementMap.botaoCriar));

		if (SeleniumHelper.isElementPresent(webDriver, grupoServicoElementMap.botaoCriar)) {
			grupoServicoElementMap.botaoCriar.click();
		}
	}

	public void preencherCodigoIdentificador(String codigo) {
		waitDriver.until(ExpectedConditions.visibilityOf(grupoServicoElementMap.codigoIdentificador));

		grupoServicoElementMap.codigoIdentificador.sendKeys(codigo);
	}

	public void preencherDescricao(String descricao) {
		grupoServicoElementMap.descricao.sendKeys(descricao);
	}

	public void preencherQuantidadeGeral(String quantidade) {
		grupoServicoElementMap.quantidadeGeral.sendKeys(quantidade);
	}

	public void preencherQuantidadePorCsa(String quantidade) {
		waitDriver.until(ExpectedConditions.visibilityOf(grupoServicoElementMap.quantidadePorCsa));

		while (grupoServicoElementMap.quantidadePorCsa.getDomProperty("value").isEmpty()) {
			grupoServicoElementMap.quantidadePorCsa.sendKeys(quantidade);
		}
	}

	public void clicarSalvar() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(grupoServicoElementMap.botaoSalvar));

		grupoServicoElementMap.botaoSalvar.click();
	}

	public void criarNovoGrupoServico() {
		clicarCriarNovoGrupoServico();
		preencherCodigoIdentificador(grpIdentificador);
		preencherDescricao(grpDescricao);
		preencherQuantidadeGeral(grpQuantidade);
		preencherQuantidadePorCsa(grpQuantidadePorCsa);
		clicarSalvar();
	}

	public void clicarServico() {
		grupoServicoElementMap.servico.click();

		await.until(() -> webDriver.getPageSource().contains("TESTE SERVICO I"));
	}

	public void clicarEditar() {
		grupoServicoElementMap.editar.click();
	}

	public void clicarExcluir() {
		grupoServicoElementMap.excluir.click();

		waitDriver.until(ExpectedConditions.alertIsPresent());
	}
}
