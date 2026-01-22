package com.zetra.econsig.bdd.steps.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.FaturamentoBeneficiosElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class FaturamentoBeneficiosPage extends BasePage {

    private final FaturamentoBeneficiosElementMap faturamentoBeneficiosElementMap;

    public FaturamentoBeneficiosPage(WebDriver webDriver) {
        super(webDriver);
        faturamentoBeneficiosElementMap = PageFactory.initElements(webDriver, FaturamentoBeneficiosElementMap.class);
    }

    public void clicarDetalharFaturamento() {
		faturamentoBeneficiosElementMap.detalharFaturamento.click();
	}

    public void clicarNotaFiscal() {
		faturamentoBeneficiosElementMap.notaFiscal.click();
	}

    public String getPeriodo() {
		return faturamentoBeneficiosElementMap.txtPeriodo.getText();
	}

    public String getOperadora() {
		return faturamentoBeneficiosElementMap.txtOperadora.getText();
	}

    public String getDataFaturamento() {
		return faturamentoBeneficiosElementMap.txtDataFaturamento.getText();
	}

    public void editarNotaFiscal() {
		// limpar campos
		faturamentoBeneficiosElementMap.valorISS.clear();
		faturamentoBeneficiosElementMap.valorIR.clear();
		faturamentoBeneficiosElementMap.valorPisConfins.clear();
		faturamentoBeneficiosElementMap.valorBruto.clear();
		faturamentoBeneficiosElementMap.valorLiquido.clear();

		// preencher os valores
		preencherValorISS("250");
		preencherValorIR("300");
		preencherValorPisConfins("13");
		preencherValorBruto("20185");
		preencherValorLiquido("1962");
	}

    public void acessarNotasFiscais(String faturamento) {
		clicarOpcoes(faturamento, "2");
		clicarNotaFiscal();
	}

    public void clicarIncluirNotaFiscal() {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Lista de notas fiscais"));

		faturamentoBeneficiosElementMap.novo.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Dados da nota fiscal"));
	}

    public void incluirNotaFiscal() {
		selecionarTipoNotaFiscal("Copart");
		preencherCodigoContrato("242241");
		preencherNumeroNF("99597845");
		preencherNumeroTitulo("526454");
		preencherDataVencimento("27/02/2021");
		preencherValorISS("250");
		preencherValorIR("300");
		preencherValorPisConfins("13");
		preencherValorBruto("20185");
		preencherValorLiquido("1962");
	}

    public void selecionarTipoNotaFiscal(String tipo) {
		Select select = new Select(faturamentoBeneficiosElementMap.tipoNotaFiscal);
		select.selectByVisibleText(tipo);
	}

    public void preencherCodigoContrato(String codigo) {
		while (!faturamentoBeneficiosElementMap.codigoContrato.getDomProperty("value").contains(codigo)) {
			faturamentoBeneficiosElementMap.codigoContrato.click();
			faturamentoBeneficiosElementMap.codigoContrato.sendKeys(codigo);
		}
	}

    public void preencherNumeroNF(String nf) {
		while (!faturamentoBeneficiosElementMap.numeroNF.getDomProperty("value").contains(nf)) {
			faturamentoBeneficiosElementMap.numeroNF.click();
			faturamentoBeneficiosElementMap.numeroNF.sendKeys(nf);
		}
	}

    public void preencherNumeroTitulo(String titulo) {
		while (!faturamentoBeneficiosElementMap.numeroTitulo.getDomProperty("value").contains(titulo)) {
			faturamentoBeneficiosElementMap.numeroTitulo.click();
			faturamentoBeneficiosElementMap.numeroTitulo.sendKeys(titulo);
		}
	}

    public void preencherDataVencimento(String data) {
		while (!faturamentoBeneficiosElementMap.dataVencimento.getDomProperty("value").matches(data)) {
			faturamentoBeneficiosElementMap.dataVencimento.clear();
			faturamentoBeneficiosElementMap.dataVencimento.sendKeys(data);
		}
	}

    public void preencherValorISS(String valor) {
		while (!faturamentoBeneficiosElementMap.valorISS.getDomProperty("value").contains(valor)) {
			faturamentoBeneficiosElementMap.valorISS.click();
			faturamentoBeneficiosElementMap.valorISS.sendKeys(valor);
		}
	}

    public void preencherValorIR(String valor) {
		while (!faturamentoBeneficiosElementMap.valorIR.getDomProperty("value").contains(valor)) {
			faturamentoBeneficiosElementMap.valorIR.click();
			faturamentoBeneficiosElementMap.valorIR.sendKeys(valor);
		}
	}

    public void preencherValorPisConfins(String valor) {
		while (!faturamentoBeneficiosElementMap.valorPisConfins.getDomProperty("value").contains(valor)) {
			faturamentoBeneficiosElementMap.valorPisConfins.click();
			faturamentoBeneficiosElementMap.valorPisConfins.sendKeys(valor);
		}
	}

    public void preencherValorBruto(String valor) {
		while (!faturamentoBeneficiosElementMap.valorBruto.getDomProperty("value").contains(valor)) {
			faturamentoBeneficiosElementMap.valorBruto.click();
			faturamentoBeneficiosElementMap.valorBruto.sendKeys(valor);
		}
	}

    public void preencherValorLiquido(String valor) {
		while (!faturamentoBeneficiosElementMap.valorLiquido.getDomProperty("value").contains(valor)) {
			faturamentoBeneficiosElementMap.valorLiquido.click();
			faturamentoBeneficiosElementMap.valorLiquido.sendKeys(valor);
		}
	}

    public void acessarDetalharFaturamento(String faturamento) {
		clicarOpcoes(faturamento, "2");
		clicarDetalharFaturamento();
	}

    public void clicarGerarFaturamentoBeneficio() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(faturamentoBeneficiosElementMap.gerarFaturamento));

		faturamentoBeneficiosElementMap.gerarFaturamento.click();
	}

    public void clicarConsultaFaturamentoBeneficio() {
		faturamentoBeneficiosElementMap.consultarFaturamento.click();
	}

    public void clicarValidarFaturamentoBeneficio() {
		faturamentoBeneficiosElementMap.validarFaturamento.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource()
				.contains("Arquivos de prévia de faturamento disponíveis"));
	}

    public void listarArquivos(String matricula) {
		waitDriver.until(ExpectedConditions.visibilityOf(faturamentoBeneficiosElementMap.matricula));

		while (!faturamentoBeneficiosElementMap.matricula.getDomProperty("value").matches(matricula)) {
			faturamentoBeneficiosElementMap.matricula.clear();
			faturamentoBeneficiosElementMap.matricula.sendKeys(matricula);
		}
		faturamentoBeneficiosElementMap.listar.click();

		await.until(() -> webDriver.getPageSource().contains("Tipo de lançamento"));
	}

    public void editarItemFaturamento() {
		preencherValorSubsidio("20");
		preencherValorRealizado("500");
		preencherValorNaoRealizado("10");
		preencherValorTotal("510");
	}

    public void preencherValorSubsidio(String valor) {
		waitDriver.until(ExpectedConditions.visibilityOf(faturamentoBeneficiosElementMap.valorSubsidio));

		faturamentoBeneficiosElementMap.valorSubsidio.clear();
		while (faturamentoBeneficiosElementMap.valorSubsidio.getDomProperty("value").isEmpty()) {
			faturamentoBeneficiosElementMap.valorSubsidio.sendKeys("20");
		}
	}

    public void preencherValorRealizado(String valor) {
		faturamentoBeneficiosElementMap.valorRealizado.clear();
		while (faturamentoBeneficiosElementMap.valorRealizado.getDomProperty("value").isEmpty()) {
			faturamentoBeneficiosElementMap.valorRealizado.sendKeys("500");
		}
	}

    public void preencherValorNaoRealizado(String valor) {
		faturamentoBeneficiosElementMap.valorNaoRealizado.clear();
		while (faturamentoBeneficiosElementMap.valorNaoRealizado.getDomProperty("value").isEmpty()) {
			faturamentoBeneficiosElementMap.valorNaoRealizado.sendKeys("10");
		}
	}

    public void preencherValorTotal(String valor) {
		faturamentoBeneficiosElementMap.valorTotal.clear();
		while (faturamentoBeneficiosElementMap.valorTotal.getDomProperty("value").isEmpty()) {
			faturamentoBeneficiosElementMap.valorTotal.sendKeys("510");
		}
	}
}
