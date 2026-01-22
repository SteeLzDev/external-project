package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ManutencaoOrgaoElementMap {

	@FindBy(id = "ORG_RESPONSAVEL")
	public WebElement orgResponsavel;

	@FindBy(id = "ORG_RESP_CARGO")
	public WebElement orgCargo;

	@FindBy(id = "ORG_CEP")
	public WebElement cep;

	@FindBy(id = "ORG_CNPJ")
	public WebElement cnpj;

	@FindBy(id = "EST_CODIGO")
	public WebElement estabelecimento;

	@FindBy(linkText = "Criar novo órgão")
	public WebElement criarNovoOrgao;

	@FindBy(id = "idMsgErrorSession")
	public WebElement txtMensagemErroExistente;

	@FindBy(id = "ORG_IDENTIFICADOR")
	public WebElement orgCodigo;

	@FindBy(id = "ORG_NOME")
	public WebElement orgNome;

	@FindBy(partialLinkText = "Bloquear /")
	public static WebElement opcaoBloquear;

	@FindBy(partialLinkText = "Desbloquear")
	public static WebElement opcaoDesbloquear;

	@FindBy(css = ".btn-action:nth-child(2) > .btn")
	public WebElement btnPesquisar;

	@FindBy(linkText = "Confirmar")
	public WebElement confirmar;

	@FindBy(name = "btnSalvar")
	public WebElement salvarConfiguracoes;

	@FindBy(name = "Excluir")
	public WebElement excluirOrgao;


}
