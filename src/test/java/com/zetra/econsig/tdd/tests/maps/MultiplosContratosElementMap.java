package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MultiplosContratosElementMap {

    // Campo Dados do convenio, selecionando a opcao de todas as naturezas de servico.
    @FindBy(name = "NSE_CODIGO")
    public WebElement SelectTodasNaturezaDeServico;

    // Campo Dados do convenio, selecionando a opcao de todas as margens.
    @FindBy(name = "MAR_CODIGO")
    public WebElement selectTodasAsMargens;

    // click aleatorio
    @FindBy(css = "fieldset:nth-child(1) > .row:nth-child(2)")
    public WebElement clickAleatorio;

    @FindBy(css = ".selecionarLinha:nth-child(1) > .selecionarColuna:nth-child(5)")
    public WebElement selecionaPrimeiroContrato;

    @FindBy(css = ".selecionarLinha:nth-child(2) > .selecionarColuna:nth-child(5)")
    public WebElement selecionaSegundoContrato;

    @FindBy(xpath = "//*[@id=\"btnPesquisar\"]")
    public WebElement botaoPesquisa;

    @FindBy(xpath = "//*[@id=\"btnEnvia\"]")
    public WebElement btnEnvia;

    @FindBy(css = "#no-back > div.main > div > div.row > div > div.btn-action > a.btn.btn-primary")
    public WebElement botaoConfirmar;

    @FindBy(xpath = "//*[@id=\"bloquearRegistroServidorSim\"]")
    public WebElement bloquearServidor;

    @FindBy(xpath = "//*[@id=\"desbloquearRegistroServidorSim\"]")
    public WebElement desbloquearServidor;

    @FindBy(xpath = "//*[@id=\"motivoBloqueioRegistroServidor\"]")
    public WebElement motivoBloqueio;

    @FindBy(linkText = "OK")
    public WebElement confirmarAlert;

    @FindBy(xpath = "//*[@id='validar']")
    public WebElement botaoValidar;

    @FindBy(xpath = "//*[@id=\"margemLimite\"]")
    public WebElement novoValorParcela;

    @FindBy(xpath = "//*[@id=\"ADE_NUMERO\"]")
    public WebElement preencherNumeroAde;

    @FindBy(id = "RSE_MATRICULA")
    public WebElement campoMatricula;

    @FindBy(xpath = "//*[@id=\"adicionaAdeLista\"]")
    public WebElement adicionaAdeLista;

    @FindBy(xpath = "//*[@id=\"tmoCodigo\"]")
    public WebElement motivoOperacao;

    @FindBy(xpath = "//*[@id=\"adeObs\"]")
    public WebElement campoObservacao;

    @FindBy(xpath = "//*[@id=\"aplicar\"]")
    public WebElement botaoAplicar;

    @FindBy(partialLinkText = "Selecionar")
    public WebElement opcaoSelecionar;
}
