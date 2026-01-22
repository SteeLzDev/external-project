package com.zetra.econsig.tdd.tests.pages;

import static com.zetra.econsig.EConsigInitializer.getBaseURL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.maps.LoginElementMap;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoginPage extends BasePage {

    private final LoginElementMap loginElementMap;

	public LoginPage(WebDriver webDriver) {
	    super(webDriver);
        loginElementMap = PageFactory.initElements(webDriver, LoginElementMap.class);
	}

	public void acessarTelaLogin() {
		webDriver.manage().deleteAllCookies();
		webDriver.get(getBaseURL() + "/consig/v3/autenticarUsuario");
		webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
	}

	public void acessarTelaLoginServidor() {
		webDriver.manage().deleteAllCookies();
		webDriver.get(getBaseURL() + "/consig/servidor");
		webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
	}

	public void loginSimples(LoginInfo usuario) {
	    loginSimples(usuario, 5);

	}

	public void loginSimples(LoginInfo usuario, int retries) {
        if ((retries >= 0) && webDriver.getPageSource().contains("POR FAVOR AGUARDE.")) {
            log.info("SISTEMA INDISPONÍVEL PARA ATUALIZAÇÃO. POR FAVOR AGUARDE.");
            await().atMost(10, TimeUnit.SECONDS);
            log.info("Nova tentativa de logar.");
            webDriver.navigate().refresh();
            loginSimples(usuario, retries - 1);
            return;
        }
        // Entra no sistema
        preencherUsuario(usuario.getLogin());
        preencherSenha(usuario.getSenha());
        clicarEntrar();
    }

	public void login(String usuario, String senha) {
		// Entra no sistema
		preencherUsuario(usuario);
		preencherSenha(senha);
		clicarEntrar();
	}

	public void loginServidor(LoginInfo usuario) {
		acessarTelaLoginServidor();
		selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		preencherUsuario(usuario.getLogin());
		preencherSenha(usuario.getSenha());
		clicarEntrar();
	}

	public void preencherUsuario(String usuario) {
		loginElementMap.usuario.sendKeys(usuario);
		loginElementMap.botaoProximo.click();

		await.ignoreException(NoSuchElementException.class).pollDelay(1, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(ExpectedConditions.elementToBeClickable(loginElementMap.botaoEntrar))
						.isNotNull());
	}

	public void selecionarOrgao(String orgao) {
		loginElementMap.comboOrgao.sendKeys(orgao);
	}

	public void preencherSenha(String senha) {
		while (loginElementMap.senha.getDomProperty("value").isEmpty()) {
		    js.executeScript("arguments[0].click()", loginElementMap.senha);
			loginElementMap.senha.sendKeys(senha);
		}
	}

	public void clicarEntrar() {
		js.executeScript("arguments[0].click()", loginElementMap.botaoEntrar);

		await.ignoreException(NoSuchElementException.class).alias("Aguarda tela principal").untilAsserted(
				() -> assertThat(ExpectedConditions.visibilityOf(loginElementMap.avatar)).isNotNull());
	}

	public void clicarMaisOpcoes() {
        js.executeScript("arguments[0].click()", loginElementMap.botaoMaisOpcoes);
	}

    public void clicarRecuperarSenha() {
        js.executeScript("arguments[0].click()", loginElementMap.linkRecuperaSenha);
    }

    public void clicarAutoDesbloquear() {
        js.executeScript("arguments[0].click()", loginElementMap.linkAutoDesbloqueio);
    }

	public boolean verificarLinkAutoDesbloqueiPresente () {
		return SeleniumHelper.isElementPresent(webDriver,  loginElementMap.linkAutoDesbloqueio);
	}
        
	
}
