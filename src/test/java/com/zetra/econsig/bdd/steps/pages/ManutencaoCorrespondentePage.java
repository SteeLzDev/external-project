package com.zetra.econsig.bdd.steps.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.ManutencaoCorrespondenteElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ManutencaoCorrespondentePage extends BasePage {

    private final ManutencaoCorrespondenteElementMap correspondenteElementMap;

	public ManutencaoCorrespondentePage(WebDriver webDriver) {
	    super(webDriver);
	    correspondenteElementMap = PageFactory.initElements(webDriver, ManutencaoCorrespondenteElementMap.class);
	}

	public void clicarBotaoNovo() {
		await.until(() -> webDriver.getPageSource().contains("Lista de correspondentes"));

		correspondenteElementMap.novoCorrespondente.click();
	}

	public void preencherCNPJ(String cnpj) {
		correspondenteElementMap.cnpj.sendKeys(cnpj);
	}

	public void clicarPesquisar() {
		correspondenteElementMap.pesquisar.click();

		await.until(() -> webDriver.getPageSource().contains("Novo correspondente"));
	}

	public void preencherCodigo(String codigo) {
		correspondenteElementMap.codigoCorrespondente.clear();
		correspondenteElementMap.codigoCorrespondente.sendKeys(codigo);
	}

	public void preencherNome(String nome) {
		correspondenteElementMap.nome.clear();
		correspondenteElementMap.nome.sendKeys(nome);
	}

	public void preencherResponsaveis() {
		correspondenteElementMap.responsavel1.clear();
		correspondenteElementMap.cargo1.clear();
		correspondenteElementMap.telefone1.clear();

		correspondenteElementMap.responsavel1.sendKeys("Antonio Almeida");
		correspondenteElementMap.cargo1.sendKeys("Advogado");
		correspondenteElementMap.telefone1.sendKeys("3132659874");
	}

	public void preencherEndereco() {
		Select select = new Select(correspondenteElementMap.uf);

		limparEndereco();
		correspondenteElementMap.logradouro.sendKeys("Avenida Brasil");
		correspondenteElementMap.numero.sendKeys("1259");
		correspondenteElementMap.bairro.sendKeys("Centro");
		correspondenteElementMap.cidade.sendKeys("Belo Horizonte");
		select.selectByVisibleText("Minas Gerais");
		correspondenteElementMap.cep.sendKeys("31710400");
	}

	public void limparEndereco() {
		correspondenteElementMap.logradouro.clear();
		correspondenteElementMap.numero.clear();
		correspondenteElementMap.bairro.clear();
		correspondenteElementMap.cidade.clear();
		correspondenteElementMap.cep.clear();
	}

	public void preencherContato() {
		correspondenteElementMap.contatoTel.sendKeys("3265987412");
		correspondenteElementMap.email.sendKeys("antonio@advogado.com");
	}

	public void desmarcarConvenio() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(correspondenteElementMap.codigoConvenio));

		while (correspondenteElementMap.codigoConvenio.isSelected()) {
			correspondenteElementMap.codigoConvenio.click();
		}
	}

	public void selecionarConvenio() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(correspondenteElementMap.codigoConvenio));

		while (!correspondenteElementMap.codigoConvenio.isSelected()) {
			correspondenteElementMap.codigoConvenio.click();
		}
	}

	public void clicarConfigurarAuditoria() {
		correspondenteElementMap.configurarAuditoria.click();
	}

	public void selecionarFuncoes() {
		js.executeScript("arguments[0].click()", correspondenteElementMap.todosGeral);

		js.executeScript("arguments[0].click()", correspondenteElementMap.todosOperacional);

		js.executeScript("arguments[0].click()", correspondenteElementMap.funcaoConsultarUsuarioCorrespondente);

		js.executeScript("arguments[0].click()", correspondenteElementMap.funcaoValidacaoLote);

		js.executeScript("arguments[0].click()", correspondenteElementMap.todosManutencaoUsuarioServidores);

		js.executeScript("arguments[0].click()", correspondenteElementMap.todosIntegracaoFolha);

		await.until(() -> webDriver.getPageSource().contains("Validação de Lote"));
	}
}
