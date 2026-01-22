package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AlterarSenhaElementMap {
	
	@FindBy(id = "senha")
	public WebElement senhaAtual;
	
	@FindBy(id = "senhaNova")
	public WebElement novaSenha;
	
	@FindBy(id = "senhaNovaConfirmacao")
	public WebElement confirmarNovaSenha;
	
	@FindBy(linkText = "Salvar")
	public WebElement botaoSalvar;
	
	@FindBy(css = ".btn-primary:nth-child(1)")
	public WebElement botaoVoltar;	
	
	@FindBy(css = "#divSeveridade > .mb-0")
	public WebElement mensagemAlerta;
}
