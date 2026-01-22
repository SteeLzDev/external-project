package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ManutencaoPostoElementMap {
	
	@FindBy (linkText = "Editar")
	public WebElement editar;
	
	@FindBy (name = "posIdentificador")
	public WebElement codigo;

	@FindBy (name = "posDescricao")
	public WebElement descricao;
	
	@FindBy (name = "posVlrSoldo")
	public WebElement valorSaldo;
	
	@FindBy (name = "perTxUsoCond")
	public WebElement percentualTaxaCond; 
	
	@FindBy (linkText = "Salvar")
	public WebElement salvar;
}
