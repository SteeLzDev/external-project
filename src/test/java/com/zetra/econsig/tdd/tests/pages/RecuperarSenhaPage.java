package com.zetra.econsig.tdd.tests.pages;

import static com.zetra.econsig.EConsigInitializer.getBaseURL;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.tdd.tests.maps.RecuperarSenhaElementMap;

public class RecuperarSenhaPage extends BasePage {
    private final RecuperarSenhaElementMap recuperarSenhaElementMap;

    public RecuperarSenhaPage(WebDriver webDriver) {
        super(webDriver);
        recuperarSenhaElementMap = PageFactory.initElements(webDriver, RecuperarSenhaElementMap.class);
    }

    public void acessarTelaRecuperarSenhaUsuCsaPasso3(String codigoRecuperacao) {
        webDriver.manage().deleteAllCookies();
        webDriver.get(getBaseURL() + "/consig/v3/recuperarSenhaUsuario?acao=iniciarUsuario&enti=CSA&tipo=recuperar&cod_recuperar=" + codigoRecuperacao);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }
    
    public void acessarTelaValidarSenhaUsuarioCSE(String codigoRecuperacao) {
        webDriver.manage().deleteAllCookies();
        webDriver.get(getBaseURL() + "/consig/v3/recuperarSenhaUsuario?acao=iniciarUsuario&enti=CSE&&tipo=recuperar&cod_recuperar=" + codigoRecuperacao);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    public void acessarTelaAutoDesbloquearUsuPasso3(String codigoRecuperacao) {
        webDriver.manage().deleteAllCookies();
        webDriver.get(getBaseURL() + "/consig/v3/autoDesbloquearUsuario?acao=iniciarUsuario&enti=CSA&tipo=recuperar&cod_recuperar=" + codigoRecuperacao);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    public void preencherUsuario(String usuario) {
        recuperarSenhaElementMap.usuario.clear();
        recuperarSenhaElementMap.usuario.sendKeys(usuario);
    }

    public void preencherCpf(String cpf) {
        recuperarSenhaElementMap.cpf.sendKeys(cpf);
    }

    public void preencherOtp(String otp) {
        recuperarSenhaElementMap.otp.sendKeys(otp);
    }

    public void preencherCaptcha() {
        recuperarSenhaElementMap.captcha.sendKeys("_test_");
    }

    public void preencherSenha(String senha) {
        recuperarSenhaElementMap.senha.sendKeys(senha);
    }

    public void preencherConfirmarSenha(String senha) {
        recuperarSenhaElementMap.confirmarSenha.sendKeys(senha);
    }

    public void clicarBotaoConfirmar() {
        js.executeScript("arguments[0].click()", recuperarSenhaElementMap.botaoConfirmar);

        await.ignoreException(NoSuchElementException.class).untilAsserted(
                () -> assertThat(ExpectedConditions.visibilityOf(recuperarSenhaElementMap.txtMensagemSucesso)).isNotNull());

    }

}
