package com.zetra.econsig.tdd.tests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.zetra.econsig.tdd.tests.maps.MultiplosContratosElementMap;

public class MultiplosContratosPage extends BasePage {

    private final MultiplosContratosElementMap multiplosContratosElementMap;

    public MultiplosContratosPage(WebDriver webDriver) {
        super(webDriver);
        multiplosContratosElementMap = PageFactory.initElements(webDriver, MultiplosContratosElementMap.class);
    }

    public void selecionaOpcaoTodasNaturezasDeServico() {
        multiplosContratosElementMap.SelectTodasNaturezaDeServico.sendKeys("-- Todas --");
        multiplosContratosElementMap.clickAleatorio.click();
    }

    public void selecionaOpcaoTodasMargens() {
        multiplosContratosElementMap.selectTodasAsMargens.sendKeys("-- Todas --");
        multiplosContratosElementMap.clickAleatorio.click();
    }

    public void preencherNumeroAde(String numeroAde) {
        multiplosContratosElementMap.preencherNumeroAde.sendKeys(numeroAde);
        multiplosContratosElementMap.adicionaAdeLista.click();
    }

    public void preencherMatricula(String matricula) {
        multiplosContratosElementMap.campoMatricula.sendKeys(matricula);
    }

    public void descerPagina() {
    	js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
    
    public void selecionaContratos() {
        multiplosContratosElementMap.selecionaPrimeiroContrato.click();
        multiplosContratosElementMap.selecionaSegundoContrato.click();
    }
/*
    private void selecionaPeloAdeNumero(String adeNumero) {
        await.until(() -> webDriver.getPageSource(), containsString(adeNumero));
        clicarOpcoes(adeNumero, "3");
        multiplosContratosElementMap.opcaoSelecionar.click();
    }
*/
    // forcando o click para que nao tenha problemas
    public void botaoPesquisar() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.botaoPesquisa);
    }

    // forcando o click para que nao tenha problemas
    public void botaoConfirmar() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.botaoConfirmar);
    }

    public void bloquearServidorParaNovasContratacoes() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.bloquearServidor);
    }

    public void desbloquearServidorParaNovasContratacoes() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.desbloquearServidor);
    }

    public void btnEnvia() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.btnEnvia);
    }

    public void motivoBloqueio(String texto) {
        multiplosContratosElementMap.motivoBloqueio.sendKeys(texto);
    }

    public void confirmarAlert() {
        webDriver.switchTo().alert().accept();
    }

    public void botaoValdiar() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.botaoValidar);
    }

    public void preencherNovoValorDosContratos(String novoValorContratos) {
        multiplosContratosElementMap.novoValorParcela.sendKeys(novoValorContratos);
    }

    public void selecionaOpcaoMotivoOperacaoAPedidoDoServidor() {
        multiplosContratosElementMap.motivoOperacao.sendKeys("A PEDIDO DO SERVIDOR");
    }

    public void selecionaOpcaoMotivoOperacaoOutros() {
        multiplosContratosElementMap.motivoOperacao.sendKeys("Outros");
    }

    public void preencheCampoObservacao(String observacao) {
        multiplosContratosElementMap.campoObservacao.sendKeys(observacao);
    }

    public void botaoAplicar() {
        js.executeScript("arguments[0].click()", multiplosContratosElementMap.botaoAplicar);
    }
}
