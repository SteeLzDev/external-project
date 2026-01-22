package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ConsultarConsignacaoElementMap {
	
	@FindBy(id = "RSE_MATRICULA")
	public WebElement matricula;
	
	@FindBy(partialLinkText = "Visualizar")
	public WebElement visualizarADE;	
	
	@FindBy(id = "btnPesquisar")
	public WebElement botaoPesquisar;
	
	@FindBy(id = "ADE_NUMERO")
	public WebElement numeroADE;
	
	@FindBy(id = "adeLista")
	public WebElement adeLista;
	
	@FindBy(id = "adicionaAdeLista")
	public WebElement adicionarAdeLista;

	@FindBy(css = ".nav-link:nth-child(2)")
	public WebElement abaInativo;
	
	@FindBy(css = ".mb-0")
	public WebElement txtInformacao;	
}
