package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BeneficioListaContratoElementMap {
	
	@FindBy (id = "consignataria")
	public WebElement csa;
	
	@FindBy (linkText = "Editar")
	public WebElement editar;
	
	@FindBy (id = "cbe_numero")
	public WebElement nrocontratoben;
	
	@FindBy (id = "cbe_data_inicio_vigencia")
	public WebElement datainiciovigenciacontratoben;

	@FindBy (id = "cbe_data_fim_vigencia")
	public WebElement datafimvigenciacontratoben;
	
	
}
