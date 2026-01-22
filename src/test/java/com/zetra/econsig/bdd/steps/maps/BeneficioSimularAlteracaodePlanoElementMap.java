package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BeneficioSimularAlteracaodePlanoElementMap {
	
	@FindBy (id = "4")
	public WebElement botaoPlanodeSaude;
	
	@FindBy (id = "selectOperadora")
	public WebElement operadoraAlteraPlano;
	
	@FindBy (id = "radioPlano_2105")
	public WebElement listaplanoaltera;
	
	@FindBy (id = "linha_plano_saude_0282808080808080808080808080B0F5")
	public WebElement beneficiario;

}
