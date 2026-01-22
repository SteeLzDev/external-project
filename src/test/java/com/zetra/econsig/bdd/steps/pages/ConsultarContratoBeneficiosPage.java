package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.ConsultarContratoBeneficioElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ConsultarContratoBeneficiosPage extends BasePage {

    private final ConsultarContratoBeneficioElementMap contratoBeneficioElementMap;

    public ConsultarContratoBeneficiosPage(WebDriver webDriver) {
        super(webDriver);
        contratoBeneficioElementMap = PageFactory.initElements(webDriver, ConsultarContratoBeneficioElementMap.class);
    }

	public void clicarDetalhar() {
		contratoBeneficioElementMap.detalharBeneficio.click();
	}

	public void clicarOpcoesBeneficioCancelado(String codigo) {
		WebElement main = webDriver
				.findElement(By.xpath(".//section/div[3]/div/div[4]/div/div/div[2]/table"));
		List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
		for (WebElement row : rows) {
			if (row.findElement(By.xpath(".//td")).getText().equals(codigo)) {
				WebElement btnOpcoes = row.findElement(By.partialLinkText("Opções"));
				while (!btnOpcoes.getDomAttribute("aria-expanded").equals("true")) {
					btnOpcoes.click();
				}
			}
		}
	}

	public void clicarRegistrarOcorrencia() {
		contratoBeneficioElementMap.registrarOcorrencia.click();
	}

	public void selecionarMotivoOperacao(String motivo) {
		await.until(() -> webDriver.getPageSource(), containsString("Motivo da operação"));

		Select select = new Select(contratoBeneficioElementMap.motivoOperacao);
		select.selectByVisibleText(motivo);
	}

	public void preencherObservacaoOcorrencia(String observacao) {
		while (contratoBeneficioElementMap.observacaoOcorrencia.getDomProperty("value").isEmpty()) {
			contratoBeneficioElementMap.observacaoOcorrencia.sendKeys(observacao);
		}
	}

	public void preencherObservacao(String observacao) {
		await.until(() -> webDriver.getPageSource().contains("Observação"));

		while (contratoBeneficioElementMap.observacao.getDomProperty("value").isEmpty()) {
			contratoBeneficioElementMap.observacao.sendKeys(observacao);
		}
	}

	public void clicarConfirmar() {
		contratoBeneficioElementMap.confirmar.click();
	}

	public void editarDadosBeneficio(String data, String periodo) {
		contratoBeneficioElementMap.dataInicioVigencia.clear();
		contratoBeneficioElementMap.periodoContribuicao.clear();

		while (!contratoBeneficioElementMap.dataInicioVigencia.getDomProperty("value").matches(data)) {
			contratoBeneficioElementMap.dataInicioVigencia.clear();
			contratoBeneficioElementMap.dataInicioVigencia.sendKeys(data);
		}

		contratoBeneficioElementMap.periodoContribuicao.sendKeys(periodo);
	}

	public void clicarCancelar() {
		contratoBeneficioElementMap.cancelar.click();
	}

	public void clicarListarLancamento() {
		contratoBeneficioElementMap.listarLancamento.click();
	}

	public void aprovarSolicitacao() {
		contratoBeneficioElementMap.aprovar.click();
	}

	public void rejeitarSolicitacao() {
		contratoBeneficioElementMap.rejeitar.click();
	}

	public void aguardarTela() {
		await.until(() -> webDriver.getPageSource(), containsString("Benefícios ativos"));
	}
}
