package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginElementMap {

	@FindBy(name = "username")
	public WebElement usuario;

	@FindBy(name = "senha")
	public WebElement senha;

	@FindBy(id = "btnOK")
	public WebElement botaoEntrar;

	@FindBy(xpath = "//*[@id=\"btnOK\"]")
	public WebElement btnEntrar2;

	@FindBy(id = "codigo_orgao")
	public WebElement comboOrgao;

	@FindBy(css = ".btn-primary:nth-child(1)")
	public WebElement botaoProximo;

	@FindBy(id = "userMenu")
	public WebElement avatar;

	@FindBy(linkText = "Sair do sistema")
	public WebElement botaoSair;

	@FindBy(linkText = "Sair")
	public WebElement botaoConfirmarSair;

	@FindBy(css = ".btn-mais-opcoes")
	public WebElement botaoMaisOpcoes;

    @FindBy(id = "linkRecuperaSenha")
    public WebElement linkRecuperaSenha;

    @FindBy(id = "linkAutoDesbloqueio")
    public WebElement linkAutoDesbloqueio;
}
