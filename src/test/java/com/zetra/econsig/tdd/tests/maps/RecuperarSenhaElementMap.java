package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RecuperarSenhaElementMap {

    @FindBy(id = "matricula")
    public WebElement usuario;

    @FindBy(id = "USU_EMAIL")
    public WebElement email;

    @FindBy(id = "USU_CPF")
    public WebElement cpf;

    @FindBy(id = "captcha")
    public WebElement captcha;

    @FindBy(id = "senhaNova")
    public WebElement senha;

    @FindBy(id = "senhaNovaConfirmacao")
    public WebElement confirmarSenha;

    @FindBy(id = "otp")
    public WebElement otp;

    @FindBy(css = ".btn-primary")
    public WebElement botaoConfirmar;

    @FindBy(id = "idMsgSuccessSession")
    public WebElement txtMensagemSucesso;
}
