package com.zetra.econsig.tdd.tests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.tdd.tests.maps.AlterarSenhaElementMap;

public class AlterarSenhaPage extends BasePage {

    private final AlterarSenhaElementMap alterarSenhaElementMap;

    public AlterarSenhaPage(WebDriver webDriver) {
        super(webDriver);
        alterarSenhaElementMap = PageFactory.initElements(webDriver, AlterarSenhaElementMap.class);
    }

	/**
	 * Teste automatizado que altera a senha de um usuario escolhido aleatoriamente
	 * e recebe como parametro o tipo de usuario que tera sua senha modificada.
	 *
	 * @param usuario - Tipo de usuario (CSE, CSA, ORG, SUPORTE ou COR) a ter a
	 *                senha alterada.
	 * @throws InterruptedException
	 */
    public void alterarSenhaUsuario(String senhaAtual, String novaSenha) throws InterruptedException {

		preencherSenhaAtual(senhaAtual);
		preencherNovaSenha(novaSenha);
		preencherConfirmarNovaSenha(novaSenha);
	}

    public void alterarSenhaUsuario(String senhaAtual, String novaSenha, String confirmarSenha)
			throws InterruptedException {

		alterarSenhaElementMap.senhaAtual.clear();
		alterarSenhaElementMap.novaSenha.clear();
		alterarSenhaElementMap.confirmarNovaSenha.clear();
		preencherSenhaAtual(senhaAtual);
		preencherNovaSenha(novaSenha);
		preencherConfirmarNovaSenha(confirmarSenha);
		clicarSalvar();
	}

	private void preencherSenhaAtual(String senha) {
		waitDriver.until(ExpectedConditions.visibilityOf(alterarSenhaElementMap.senhaAtual));

		while (!alterarSenhaElementMap.senhaAtual.getDomProperty("value").matches(senha)) {
			alterarSenhaElementMap.senhaAtual.clear();
			alterarSenhaElementMap.senhaAtual.sendKeys(senha);
		}
	}

	public void preencherNovaSenha(String novaSenha) {
		while (!alterarSenhaElementMap.novaSenha.getDomProperty("value").matches(novaSenha)) {
			alterarSenhaElementMap.novaSenha.clear();
			alterarSenhaElementMap.novaSenha.sendKeys(novaSenha);
		}
	}

	public void preencherConfirmarNovaSenha(String senhaConfirmacao) {
		while (!alterarSenhaElementMap.confirmarNovaSenha.getDomProperty("value").matches(senhaConfirmacao)) {
			alterarSenhaElementMap.confirmarNovaSenha.clear();
			alterarSenhaElementMap.confirmarNovaSenha.sendKeys(senhaConfirmacao);
		}
	}

	public void clicarSalvar() {
		js.executeScript("arguments[0].click()", alterarSenhaElementMap.botaoSalvar);
	}

	public void clicarVoltar() {
		alterarSenhaElementMap.botaoVoltar.click();
	}

	public String retornarMensagemSeveridade() {
		return alterarSenhaElementMap.mensagemAlerta.getText();
	}
}
