package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReimplantarCapitalDevidoElementMap {
	
	@FindBy(id = "RSE_MATRICULA")
	public WebElement matricula;
	
	@FindBy(id = "SER_CPF")
	public WebElement cpf;
	
	@FindBy(id = "ADE_NUMERO")
	public WebElement numeroADE;
	
	@FindBy(linkText = "Pesquisar")
	public WebElement botaoPesquisar;
	
	@FindBy(id = "adeLista")
	public WebElement adeLista;
	
	@FindBy(id = "adicionaAdeLista")
	public WebElement adicionarAdeLista;
	
	@FindBy(linkText = "Reimp. Cap.")
	public WebElement linkReimplantarCapitalDevido;
	
	@FindBy(id = "adeVlr")
	public WebElement novoValorAde;
	
	@FindBy(id = "adePrazoEdt")
	public WebElement novoPrazoAde;
	
	@FindBy(id = "tmoCodigo")
	public WebElement motivoDaOp;
	
	@FindBy(id = "ocaObs")
	public WebElement obsAde;
	
	@FindBy(linkText = "Confirmar")
	public WebElement botaoConfirmar;
	
	@FindBy(css = ".mb-0")
	public WebElement txtInformacao;
	
	@FindBy(css = "div[class='alert alert-warning']")
    public WebElement txtMensagemAlerta;
	
	@FindBy(css = "div[class='alert alert-btn']")
    public WebElement botaoOkAlerta;
	
	@FindBy(css = ".btn-primary")
	public WebElement botaoVoltar;
}
