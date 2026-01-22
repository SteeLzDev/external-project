package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.tdd.tests.maps.RegraTaxaJurosElementMap;

public class EditarRegraTaxaJurosConsignatariaPage extends BasePage {

    private final RegraTaxaJurosElementMap regraTaxaJurosElementMap;

    public EditarRegraTaxaJurosConsignatariaPage(WebDriver webDriver) {
        super(webDriver);
        regraTaxaJurosElementMap = PageFactory.initElements(webDriver, RegraTaxaJurosElementMap.class);
    }

    public void clicarCriarNovaRegraTaxaJuros() {
        regraTaxaJurosElementMap.criarNovaRegraTaxaJuros.click();

        await.until(() -> webDriver.getPageSource(), containsString("Inclusão de nova regra de taxa de juros"));
    }

    public void selecionarOrgao(String orgao) {
        final Select select = new Select(regraTaxaJurosElementMap.orgao);
        select.selectByVisibleText(orgao);
    }

    public void selecionarServico(String servico) {
        final Select select = new Select(regraTaxaJurosElementMap.servico);
        select.selectByVisibleText(servico);
    }

    public void selecionarFuncao(String funcao) {
        final Select select = new Select(regraTaxaJurosElementMap.funcao);
        select.selectByVisibleText(funcao);
    }

    public void preencherFaixaTempoServicoInicial(String faixaTempoServicoInicial) {
        regraTaxaJurosElementMap.faixaTempoServicoInicial.sendKeys(faixaTempoServicoInicial);
    }

    public void preencherFaixaTempoServicoFinal(String faixaTempoServicoFinal) {
        regraTaxaJurosElementMap.faixaTempoServicoFinal.sendKeys(faixaTempoServicoFinal);
    }

    public void preencherFaixaSalarialInicial(String faixaSalarialInicial) {
        regraTaxaJurosElementMap.faixaSalarialInicial.sendKeys(faixaSalarialInicial);
    }

    public void preencherFaixaSalarialFinal(String faixaSalarialFinal) {
        regraTaxaJurosElementMap.faixaSalarialFinal.sendKeys(faixaSalarialFinal);
    }

    public void preencherFaixaEtariaInicial(String faixaEtariaInicial) {
        regraTaxaJurosElementMap.faixaEtariaInicial.sendKeys(faixaEtariaInicial);
    }

    public void preencherFaixaEtariaFinal(String faixaEtariaFinal) {
        regraTaxaJurosElementMap.faixaEtariaFinal.sendKeys(faixaEtariaFinal);
    }

    public void preencherFaixaMargemInicial(String faixaMargemInicial) {
        regraTaxaJurosElementMap.faixaMargemInicial.sendKeys(faixaMargemInicial);
    }

    public void preencherFaixaMargemFinal(String faixaMargemFinal) {
        regraTaxaJurosElementMap.faixaMargemFinal.sendKeys(faixaMargemFinal);
    }

    public void preencherFaixaValorTotalInicial(String faixaValorTotalInicial) {
        regraTaxaJurosElementMap.faixaValorTotalInicial.sendKeys(faixaValorTotalInicial);
    }

    public void preencherFaixaValorTotalFinal(String faixaValorTotalFinal) {
        regraTaxaJurosElementMap.faixaValorTotalFinal.sendKeys(faixaValorTotalFinal);
    }

    public void preencherFaixaValorContratoInicial(String faixaValorContratoInicial) {
        regraTaxaJurosElementMap.faixaValorContratoInicial.sendKeys(faixaValorContratoInicial);
    }

    public void preencherFaixaValorContratoFinal(String faixaValorContratoFinal) {
        regraTaxaJurosElementMap.faixaValorContratoFinal.sendKeys(faixaValorContratoFinal);
    }

    public void preencherFaixaPrazoInicial(String faixaPrazoInicial) {
        if (!regraTaxaJurosElementMap.faixaPrazoInicial.getDomProperty("value").isBlank()) {
            regraTaxaJurosElementMap.faixaPrazoInicial.clear();
        }
        regraTaxaJurosElementMap.faixaPrazoInicial.sendKeys(faixaPrazoInicial);
    }

    public void preencherFaixaPrazoFinal(String faixaPrazoFinal) {
        if (!regraTaxaJurosElementMap.faixaPrazoFinal.getDomProperty("value").isBlank()) {
            regraTaxaJurosElementMap.faixaPrazoFinal.clear();
        }
        regraTaxaJurosElementMap.faixaPrazoFinal.sendKeys(faixaPrazoFinal);
    }

    public void preencherTaxaJuros(String taxaJuros) {
        if (!regraTaxaJurosElementMap.taxaJuros.getDomProperty("value").isBlank()) {
            regraTaxaJurosElementMap.taxaJuros.clear();
        }
        regraTaxaJurosElementMap.taxaJuros.sendKeys(taxaJuros);
    }

    public void clicarAtivarTabela() {
        js.executeScript("arguments[0].click()", regraTaxaJurosElementMap.ativarTabela);
    }

    public void clicarIniciarTabela() {
        js.executeScript("arguments[0].click()", regraTaxaJurosElementMap.iniciarTabela);
    }
}
