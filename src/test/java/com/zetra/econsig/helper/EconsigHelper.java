package com.zetra.econsig.helper;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.zetra.econsig.bdd.steps.maps.EconsigElementMap;

@Component
public class EconsigHelper {

	private final ConditionFactory await = await().atMost(50, TimeUnit.SECONDS);


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * test.econsig.control.EconsigHelperInterface#getEconsigInfoMessages(org.openqa
	 * .selenium.WebDriver)
	 */
	public List<String> getEconsigInfoMessages(WebDriver driver) throws Throwable {
		List<WebElement> we = SeleniumHelper.waitForElemments(driver, By.id("idMsgSuccessSession"), 5);
		List<String> res = new ArrayList<>();
		for (WebElement e : we) {
			res.add(e.getText());
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see test.econsig.control.EconsigHelperInterface#getEconsigInfo(org.openqa.
	 * selenium.WebDriver)
	 */
	public String getEconsigInfo(WebDriver driver) throws Throwable {
		StringBuilder s = new StringBuilder();
		for (String e : getEconsigInfoMessages(driver)) {
			s.append(e).append('\n');
		}
		return s.toString();
	}

	public String getMensagemSucesso(WebDriver d) {
		EconsigElementMap econsigElementMap = PageFactory.initElements(d,
				EconsigElementMap.class);

		await.ignoreException(StaleElementReferenceException.class)
				.until(() -> econsigElementMap.txtMensagemSucesso.getText(), notNullValue());

		return econsigElementMap.txtMensagemSucesso.getText();
	}

	public boolean isMensagemSucesso(WebDriver d) {
		Wait<WebDriver> wait = new WebDriverWait(d, Duration.ofSeconds(20));

		boolean mensagem = false;

		try {
			wait.until(SeleniumHelper.visibilityOfElementLocated(By.id("idMsgSuccessSession")));
			mensagem = true;
		} catch (TimeoutException exception) {
			mensagem = false;
		}
		return mensagem;
	}

	public String getMensagemErro(WebDriver d) {
		EconsigElementMap econsigElementMap = PageFactory.initElements(d,
				EconsigElementMap.class);

		await.ignoreException(StaleElementReferenceException.class)
				.until(() -> econsigElementMap.txtMensagemErro.getText(), notNullValue());

		return econsigElementMap.txtMensagemErro.getText();
	}

    public String getMensagemAlerta(WebDriver d) {
        EconsigElementMap econsigElementMap = PageFactory.initElements(d,
                EconsigElementMap.class);

        await.ignoreException(StaleElementReferenceException.class)
                .until(() -> econsigElementMap.warningAlertDiv.getText(), notNullValue());

        return econsigElementMap.warningAlertDiv.getText();
    }

	public String getMensagemInformacao(WebDriver d) {
		EconsigElementMap econsigElementMap = PageFactory.initElements(d,
				EconsigElementMap.class);

		await.ignoreException(StaleElementReferenceException.class)
				.until(() -> econsigElementMap.txtMensagemInformacao.getText(), notNullValue());

		return econsigElementMap.txtMensagemInformacao.getText();
	}

    public String getMensagemSessaoExpirada(WebDriver d) {
        WebDriverWait driverWait = new WebDriverWait(d, Duration.ofSeconds(100));

        driverWait.until(SeleniumHelper.visibilityOfElementLocated(By.id("msgSessaoExpirada")));

        EconsigElementMap econsigElementMap = PageFactory.initElements(d,
                EconsigElementMap.class);

        return econsigElementMap.txtMensagemSessaoExpirada.getText();
    }

	public String getMensagemPopUp(WebDriver d) {
		WebDriverWait driverWait = new WebDriverWait(d, Duration.ofSeconds(5));
		driverWait.until(ExpectedConditions.alertIsPresent());

		Alert alert = d.switchTo().alert();
		String mensagem = alert.getText();
		alert.accept(); // fecha popup

		return mensagem;
	}

	public void verificaTextoPagina(WebDriver d, String texto) {
		await.pollDelay(Duration.ofSeconds(2)).until(() -> d.getPageSource().contains(texto));
	}
}