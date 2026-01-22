package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SimularConsignacaoElementMap {

    @FindBy(id = "ADE_VLR")
    public WebElement valorPrestacao;

    @FindBy(id = "VLR_LIBERADO")
    public WebElement valorSolicitado;

    @FindBy(id = "PRZ_VLR")
    public WebElement numeroPrestacoes;

    @FindBy(id = "btnConfirmar")
    public WebElement botaoSimular;

    @FindBy(id = "nv")
    public WebElement botaoNovoContrato;

    @FindBy(id = "rn")
    public WebElement botaoRenegociacao;

    @FindBy(id = "pr")
    public WebElement botaoPortabilidade;

    @FindBy(id = "acoes")
    public WebElement botaoAcoes;

    @FindBy(linkText = "Renegociação")
    public WebElement botaoMaisAcoesRenegociacao;

    @FindBy(linkText = "Novo contrato")
    public WebElement botaoMaisAcoesNovoContrato;
}
