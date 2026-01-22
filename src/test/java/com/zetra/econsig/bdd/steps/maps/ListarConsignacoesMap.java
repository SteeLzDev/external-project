package com.zetra.econsig.bdd.steps.maps;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ListarConsignacoesMap {
	
	@FindBy(css = "card-header-title")
	public WebElement btnPesquisar;
	
	@FindBy(id = "dataTables")
	public WebElement dataTables;
	
	@FindBy(css = ".selecionarLinha")
	public List<WebElement> selecionarLinha;
	
	@FindBy(linkText = "Confirmar")
	public WebElement confirmarBotao;	

}
