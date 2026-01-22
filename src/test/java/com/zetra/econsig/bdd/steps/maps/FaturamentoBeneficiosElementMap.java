package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FaturamentoBeneficiosElementMap {

	@FindBy(linkText = "Detalhar faturamento")
	public WebElement detalharFaturamento;

	@FindBy(linkText = "Nota Fiscal")
	public WebElement notaFiscal;
		
	@FindBy(css = ".col-6:nth-child(2)")
	public WebElement txtPeriodo;
	
	@FindBy(css = ".col-6:nth-child(4)")
	public WebElement txtOperadora;
	
	@FindBy(css = ".col-6:nth-child(6)")
	public WebElement txtDataFaturamento;

	@FindBy(linkText = "Novo")
	public WebElement novo;
	
	@FindBy(id = "tipoNotaFiscal")
	public WebElement tipoNotaFiscal;

	@FindBy(id = "codigoContrato")
	public WebElement codigoContrato;
	
	@FindBy(id = "numeroNf")
	public WebElement numeroNF;

	@FindBy(id = "numeroTitulo")
	public WebElement numeroTitulo;
	
	@FindBy(id = "dataVencimento")
	public WebElement dataVencimento;

	@FindBy(id = "valorIss")
	public WebElement valorISS;
	
	@FindBy(id = "valorIr")
	public WebElement valorIR;

	@FindBy(id = "pisCofins")
	public WebElement valorPisConfins;
	
	@FindBy(id = "valorBruto")
	public WebElement valorBruto;

	@FindBy(id = "valorLiquido")
	public WebElement valorLiquido;
	
	@FindBy(linkText = "Gerar faturamento de benefício")
	public WebElement gerarFaturamento;
	
	@FindBy(linkText = "Consultar faturamento de benefício")
	public WebElement consultarFaturamento;
	
	@FindBy(linkText = "Validar faturamento de benefício")
	public WebElement validarFaturamento;

	@FindBy(id = "matricula")
	public WebElement matricula;

	@FindBy(linkText = "Listar")
	public WebElement listar;	
	
	@FindBy(id = "AFB_VALOR_SUBSIDIO")
	public WebElement valorSubsidio;
	
	@FindBy(id = "AFB_VALOR_REALIZADO")
	public WebElement valorRealizado;
	
	@FindBy(id = "AFB_VALOR_NAO_REALIZADO")
	public WebElement valorNaoRealizado;
	
	@FindBy(id = "AFB_VALOR_TOTAL")
	public WebElement valorTotal;

}