package com.zetra.econsig.bdd.steps.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.ListarConsignacoesMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ListarConsignacoesPage extends BasePage {

    private final ListarConsignacoesMap listarConsignacoesMap;

    public ListarConsignacoesPage(WebDriver webDriver) {
        super(webDriver);
        listarConsignacoesMap = PageFactory.initElements(webDriver, ListarConsignacoesMap.class);
    }

	public void carregarListagemConsignacoes() {
		waitDriver.until(ExpectedConditions.visibilityOf(listarConsignacoesMap.dataTables));
	}

	public void selecionarConsignacoesAdeNumero(List<String> adeNumeros) {
		if (!adeNumeros.isEmpty()) {
			//seleciona as linhas da tabela de consignações correspondentes aos adeNumeros passados por parâmetro e os seleciona.
			listarConsignacoesMap.selecionarLinha.stream()
			.filter(tdLinha -> tdLinha.findElements(By.cssSelector(".selecionarColuna")).stream().filter(colWebElement -> adeNumeros.contains(colWebElement.getText())).count() > 0)
			.collect(Collectors.toList())
			.forEach(WebElement::click);
		}
	}

	public void detalharAdeListaConsignacoes(String adeNumero) {
		listarConsignacoesMap.selecionarLinha.stream()
		.filter(tdLinha -> tdLinha.findElements(By.cssSelector(".selecionarColuna")).stream().filter(colWebElement -> adeNumero.equals(colWebElement.getText())).count() > 0)
		.findFirst().ifPresent(linhaSelecionada -> js.executeScript("arguments[0].click()", linhaSelecionada.findElement(By.tagName("a"))));
	}

	public void clicarBotaoConfirmar() {
		listarConsignacoesMap.confirmarBotao.click();
	}
}
