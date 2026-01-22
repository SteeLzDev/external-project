package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegraTaxaJurosElementMap {

    @FindBy(linkText = "Nova regra de taxa juros")
    public WebElement criarNovaRegraTaxaJuros;

    @FindBy(linkText = "Ativar tabela")
    public WebElement ativarTabela;

    @FindBy(linkText = "Iniciar tabela")
    public WebElement iniciarTabela;

    @FindBy(linkText = "Excluir")
    public WebElement excluir;

    @FindBy(partialLinkText = "Pesquisar")
    public WebElement pesquisar;

    @FindBy(id = "statusRegra1")
    public WebElement opcaoNovaTabelaIniciada;

    @FindBy(id = "statusRegra2")
    public WebElement opcaoTabelaAtiva;

    @FindBy(id = "statusRegra3")
    public WebElement opcaoTabelaVigenciaExpirada;

    @FindBy(id = "ORG_CODIGO")
    public WebElement orgaoSecaoPesquisar;

    @FindBy(id = "SVC_CODIGO")
    public WebElement servicoSecaoPesquisar;

    @FindBy(id = "DATA")
    public WebElement dataVigencia;

    @FindBy(id = "orgCodigo")
    public WebElement orgao;

    @FindBy(id = "svcCodigo")
    public WebElement servico;

    @FindBy(id = "funCodigo")
    public WebElement funcao;

    @FindBy(id = "faixaTempoServicoInicial")
    public WebElement faixaTempoServicoInicial;

    @FindBy(id = "faixaTempoServicoFinal")
    public WebElement faixaTempoServicoFinal;

    @FindBy(id = "faixaSalarialInicial")
    public WebElement faixaSalarialInicial;

    @FindBy(id = "faixaSalarialFinal")
    public WebElement faixaSalarialFinal;

    @FindBy(id = "faixaEtariaInicial")
    public WebElement faixaEtariaInicial;

    @FindBy(id = "faixaEtariaFinal")
    public WebElement faixaEtariaFinal;

    @FindBy(id = "faixaMargemInicial")
    public WebElement faixaMargemInicial;

    @FindBy(id = "faixaMargemFinal")
    public WebElement faixaMargemFinal;

    @FindBy(id = "faixaValorTotalInicial")
    public WebElement faixaValorTotalInicial;

    @FindBy(id = "faixaValorTotalFinal")
    public WebElement faixaValorTotalFinal;

    @FindBy(id = "faixaValorContratoInicial")
    public WebElement faixaValorContratoInicial;

    @FindBy(id = "faixaValorContratoFinal")
    public WebElement faixaValorContratoFinal;

    @FindBy(id = "faixaPrazoInicial")
    public WebElement faixaPrazoInicial;

    @FindBy(id = "faixaPrazoFinal")
    public WebElement faixaPrazoFinal;

    @FindBy(id = "taxaJuros")
    public WebElement taxaJuros;
}
