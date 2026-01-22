package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EconsigElementMap {

    @FindBy(id = "idMsgErrorSession")
    public WebElement txtMensagemErro;

    @FindBy(id = "idMsgSuccessSession")
    public WebElement txtMensagemSucesso;

    @FindBy(id = "idMsgInfoSession")
    public WebElement txtMensagemInformacao;

    @FindBy(id = "msgSessaoExpirada")
    public WebElement txtMensagemSessaoExpirada;

    @FindBy(css = ".alert-danger")
    public WebElement errorAlertDiv;

    @FindBy(css = ".alert-warning")
    public WebElement warningAlertDiv;
}
