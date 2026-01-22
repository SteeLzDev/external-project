package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReativarContratoBeneficioElementMap {
	
	@FindBy(id = "selectOperadoraPlanoSaude")
	public WebElement operadora;		
	
	@FindBy(id = "radioPlano_2105")
	public WebElement plano;

	@FindBy(name = "selecionaAcaoSelecionar")
	public WebElement selecionarBeneficiario;	
	    
    @FindBy(css = "a.btn.btn-primary")
    public  WebElement btnContinuar;
	
	@FindBy(id = "cbeCodigoSaude0_F48080808080808080808080808001AB")
	public WebElement contrato;
    
}