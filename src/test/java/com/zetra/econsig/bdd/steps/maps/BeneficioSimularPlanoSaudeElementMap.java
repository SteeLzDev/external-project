package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BeneficioSimularPlanoSaudeElementMap {
	
	@FindBy (id = "RSE_MATRICULA")
	public WebElement matricula;
	
	@FindBy (id = "btnPesquisar")
	public WebElement pesquisar;
	
	@FindBy (id = "selectOperadoraPlanoSaude")
	public WebElement operadora;
	
	@FindBy (name = "selecionaAcaoSelecionar")
	public WebElement selecionar;

	@FindBy (linkText = "Continuar")
	public WebElement continuar;
	
	@FindBy (linkText = "Total Simulacao")
	public WebElement totalsimulacao;
	
	@FindBy (linkText = "Resultado por beneficiário")
	public WebElement resultadobeneficiario;
	
	@FindBy (linkText = "Resultado da simulação por beneficiário - Plano de Saúde")
	public WebElement resultadoPlanoSaude;
	
	@FindBy (id = "radioPlano_2107")
	public WebElement listaplanosaude;
	
	@FindBy (id = "radioPlano_2111")
	public WebElement listaplanoodonto;
	
	@FindBy (id = "selectOperadoraPlanoOdontologico")
	public WebElement operadoraodonto;

	@FindBy (id = "linha_plano_saude_F480808080808080808080808080579C")
	public WebElement beneficiario;
	
	@FindBy (id = "linha_plano_odontologico_F480808080808080808080808080579C")
	public WebElement beneficiarioOdonto; 
	
	@FindBy(id = "idMsgErrorSession")
	public static  WebElement txtMensagemErro; 
	
}
