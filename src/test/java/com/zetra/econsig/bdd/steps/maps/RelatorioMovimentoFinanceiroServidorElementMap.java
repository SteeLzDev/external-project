package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RelatorioMovimentoFinanceiroServidorElementMap {
	@FindBy(xpath = "/html/body/section/div[3]/div/form/div[1]/div[2]/div/div[1]/input")
	public WebElement dataIni; 
	
	@FindBy(xpath = "/html/body/section/div[3]/div/form/div[1]/div[2]/div/div[2]/input")
	public WebElement dataFim; 
	
	@FindBy(xpath = "/html/body/section/div[3]/div/form/div[2]/a")
	public WebElement confirmaRelatorioMovimentoServidor;

	@FindBy(xpath = "//*[@id=\"formato\"]")
	public WebElement campoFormatoRelatorio;
	
	@FindBy(xpath = "//*[@id=\"RSE_MATRICULA\"]")
	public WebElement campoMatricula;
	
	@FindBy(xpath = "//*[@id=\"CPF\"]")
	public WebElement campoCpf;
	
	@FindBy(xpath =  "//*[@id=\"mensagens\"]/div[1]")
	public WebElement campoMensagemInformacao;
	
	@FindBy(xpath = "//*[@id=\"mensagens\"]/div[2]/p")
	public WebElement campoMensagemInformacaoDois;
	
	@FindBy(xpath = "//*[@id=\"dataTables\"]/tbody/tr[1]/td[1]")
	public WebElement campoTabelaRelatorio;
	
	@FindBy(xpath = "//*[@id=\"username\"]")
	public WebElement campoAutorizacaoUsername;
	
	@FindBy(xpath = "//*[@id=\"senha2aAutorizacao\"]")
	public WebElement campoAutorizacaoSenha;
	
	@FindBy(xpath = "/html/body/div[2]/div[3]/div/button[2]")
	public WebElement confirmaAutorizacao;
}