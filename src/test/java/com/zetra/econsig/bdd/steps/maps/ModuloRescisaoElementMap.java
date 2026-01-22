package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ModuloRescisaoElementMap {

	@FindBy(id = "btnAdicionarRescisao")
	public WebElement btnAdicionarServidor;

	@FindBy(xpath = "//*[@id=\"no-back\"]/div[3]/div/div[3]/div[2]/table/tbody/tr/td[2]")
	public WebElement campoColunaNome;

	@FindBy(xpath = "//*[@id=\"no-back\"]/div[3]/div/div[3]/div[2]/table/tbody/tr/td[3]")
	public WebElement campoColunaCpf;

	@FindBy(id = "btnExcluirRescisao")
	public WebElement excluirServidor;

	@FindBy(id = "btnConfirmarRescisao")
	public WebElement btnConfirmarRescisao;

	@FindBy(id = "idMsgSuccessSession")
	public WebElement msgSucesso;

	@FindBy(id = "idMsgInfoSession")
	public WebElement msgInfo;

	@FindBy(xpath = "//*[text()='Nenhum registro encontrado.']")
	public WebElement msgNenhumRegistroEncontrado;

	@FindBy(linkText = "Visualizar")
	public WebElement visualizarDadosColaborador;

	@FindBy(linkText = "Informar Verba Rescisória")
	public WebElement informarVerbaRescisoria;

	@FindBy(xpath = "//*[@id=\"dataTables\"]/tbody/tr[1]/td[6]")
	public WebElement txtSituacao;

	@FindBy(id = "VRR_VALOR")
	public WebElement campoValorDisponivelRetencao;

	@FindBy(xpath = "//a[@onclick=\"javascript:confirmar();\"]")
	public WebElement btnSalvar;	
	
	@FindBy(css = "table > tbody > tr > td:nth-child(4)")
	public WebElement txtValorRetido;	

	@FindBy(xpath = "/html/body/section/div[3]/div/div[2]/form/div[3]/div[2]/table/tbody/tr/td[5]/span")	
	public WebElement txtValorRetencao;
	
	@FindBy(css = ".col-6:nth-child(22)")
	public WebElement txtIdentificadorAde;
	
	@FindBy(css = ".col-6:nth-child(6)")
	public WebElement txtSituacaoAde;
	
	@FindBy(partialLinkText = "Relacionamento para Verba Rescisória - ADE")
	public WebElement linkRelacionamentoAde;
	
}
