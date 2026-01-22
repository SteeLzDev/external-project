package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReconhecimentoFacialElementMap { 	

    @FindBy(id = "RSE_MATRICULA")
    public WebElement matriculaServidor;

    @FindBy(xpath = "//*[@id=\"btnPesquisar\"]")
    public WebElement pesquisar;

    @FindBy(xpath = "/html/body/section/div[3]/div/div[2]/div/div[1]/div[2]/table/tbody/tr[1]/td[7]/a")
    public WebElement selecionaServidorTabelaSimularConsignacao;

    @FindBy(xpath = "//*[@id=\\\"no-back\\\"]/div[3]/div/div[2]/div[2]/table/tbody/tr[td[contains(text(), 'EMPRESTIMO')]]")
    public WebElement buscarEmprestimo;

    @FindBy(xpath = "//*[@id=\"adeVlr\"]")
    public WebElement preencherValorPrestacaoEmprestimo; 

    @FindBy(xpath = "//*[@id=\"adePrz\"]")
    public WebElement preencherNumeroPrestacoes;

    @FindBy(linkText = "Solicitar empréstimo")
    public WebElement soliciarEmprestimo;

    @FindBy(xpath = "//*[@id=\"no-back\"]/div[3]/div/div[4]/div[2]/table/tbody/tr[1]/td[3]/a")
    public WebElement bancoBrasil;

    @FindBy(className = "modal-content-ReconhcimentoFacial")
    public WebElement reconhecimentoFacialModal;

    @FindBy(xpath = "//*[@id=\"no-back\"]/div[3]/div/form/div[2]/a[2]")
    public WebElement confirmarSolicitacaoEmprestimo;
}