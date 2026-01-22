package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.tdd.tests.maps.ConsultarConsignacaoElementMap;

public class ConsultarConsignacaoPage extends BasePage {

    private final ConsultarConsignacaoElementMap consultarConsignacao;

    public ConsultarConsignacaoPage(WebDriver webDriver) {
        super(webDriver);
        consultarConsignacao = PageFactory.initElements(webDriver, ConsultarConsignacaoElementMap.class);
    }

    public void preencherMatricula(String matricula) {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("matrícula ou CPF do servidor para a pesquisa"));

		while (!consultarConsignacao.matricula.getDomProperty("value").matches(matricula)) {
			consultarConsignacao.matricula.clear();
			consultarConsignacao.matricula.click();
			consultarConsignacao.matricula.sendKeys(matricula);
		}
	}

	public void limparMatricula() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("matrícula ou CPF do servidor para a pesquisa"));

		consultarConsignacao.matricula.clear();
	}

	public void clicarPesquisar() {
		consultarConsignacao.botaoPesquisar.click();
	}

	public void pesquisarConsinacao(String matricula) {
		preencherMatricula(matricula);
		clicarPesquisar();
	}

	public void pesquisarConsinacao(String matricula, String ade) {
		consultarConsignacao.matricula.clear();
		preencherMatricula(matricula);
		preencherADE(ade);
		clicarPesquisar();
	}

	public void preencherAdeNumero(String adeNumero) {
		consultarConsignacao.numeroADE.sendKeys(adeNumero);
	}

	public void clicarVisualizar() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Consignações"));

		consultarConsignacao.visualizarADE.click();

		await.until(() -> webDriver.getPageSource(), not("Consignações"));
	}

	public void preencherADE(String ade) {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(consultarConsignacao.txtInformacao,
				"matrícula ou CPF do servidor para a pesquisa"));

		while (!consultarConsignacao.numeroADE.getDomProperty("value").matches(ade)) {
			consultarConsignacao.numeroADE.clear();
			consultarConsignacao.numeroADE.sendKeys(ade);
		}
		consultarConsignacao.adicionarAdeLista.click();
	}
}
