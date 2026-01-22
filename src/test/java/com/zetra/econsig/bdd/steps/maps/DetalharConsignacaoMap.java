package com.zetra.econsig.bdd.steps.maps;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DetalharConsignacaoMap {
	
	@FindBy(id = "acoes")
	public WebElement botaoAcoes;
	
	@FindBy(css = ".dropdown-item")
	public List<WebElement> dropDownItens;
	
	@FindBy(id = "idMsgSuccessSession")
	public WebElement msgSuccess;

}
