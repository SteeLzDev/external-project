package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CalculoBeneficiosElementMap {

	@FindBy(linkText = "Novo cálculo")
	public WebElement novoCalculo;
	
	@FindBy(partialLinkText = "Aplicar reajuste")
	public WebElement aplicarReajuste;
	
	@FindBy(linkText = "Aplicar reajuste")
	public WebElement aplicarReajusteCalculo;
		
	@FindBy(id = "chkAplicarReajuste")
	public WebElement checkAplicarReajuste;
	
	@FindBy(id = "acoes")
	public WebElement botaoAcoes;
	
	@FindBy(id = "TIB_CODIGO")
	public WebElement tipoBeneficiario;
	
	@FindBy(id = "orgCodigo")
	public WebElement orgao;
	
	@FindBy(id = "benCodigo")
	public WebElement beneficio;
	
	@FindBy(id = "tibCodigo")
	public WebElement tipoBeneficio;
	
	@FindBy(id = "grpCodigo")
	public WebElement grauParentesco;
	
	@FindBy(id = "clbFaixaSalarialIni")
	public WebElement inicioFaixaSalarial;
	
	@FindBy(id = "clbFaixaSalarialFim")
	public WebElement finalFaixaSalarial;
	
	@FindBy(name = "clbFaixaEtariaIni")
	public WebElement inicioFaixaEtaria;
	
	@FindBy(name = "clbFaixaEtariaFim")
	public WebElement fimFaixaEtaria;
	
	@FindBy(id = "clbValorMensalidade")
	public WebElement valorBeneficio;
	
	@FindBy(id = "clbValorSubsidio")
	public WebElement valorSubsidio;

	@FindBy(partialLinkText = "Ativar tabela")
	public WebElement ativarTabela;

	@FindBy(linkText = "Excluir tabela iniciada")
	public WebElement excluirTabelaIniciada;

	@FindBy(partialLinkText = "Iniciar tabela")
	public WebElement iniciarTabela;
	
	@FindBy(css = "tr:nth-child(1) > td:nth-child(3)")
	public WebElement txtOrgao;
	
	@FindBy(css = "tr:nth-child(1) > td:nth-child(4)")
	public WebElement txtBeneficioTitular;
	
	@FindBy(css = "tr:nth-child(2) > td:nth-child(4)")
	public WebElement txtBeneficioDependente;
	
	@FindBy(css = "tr:nth-child(3) > td:nth-child(4)")
	public WebElement txtBeneficioAgregado;

	@FindBy(id = "Filtrar")
	public WebElement botaoPesquisar;
	
	@FindBy(id = "valorReajuste")
	public WebElement valorReajuste;
	
	@FindBy(id = "aplicarSobreBeneficio")
	public WebElement aplicarSobreBeneficio;
	
	@FindBy(id = "aplicarSobreSubsidio")
	public WebElement aplicarSobreSubsidio;
	
	@FindBy(id = "aplicarSobreFaixaSalarial")
	public WebElement aplicarSobreFaixaSalarial;
	
	@FindBy(css = "td:nth-child(6)")
	public WebElement txtInicioFaixa;
	
	@FindBy(css = "td:nth-child(7)")
	public WebElement txtFimFaixa;
	
	@FindBy(css = "td:nth-child(10)")
	public WebElement txtValorBeneficio;
	
	@FindBy(css = "td:nth-child(11)")
	public WebElement txtValorSubsidio;
}