package com.zetra.econsig.tdd.tests.pages;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected static final ConditionFactory await = await().atMost(30, TimeUnit.SECONDS);

    protected final WebDriver webDriver;
    protected final WebDriverWait waitDriver;
    protected final JavascriptExecutor js;
    protected final Actions actions;

    public BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
        waitDriver = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        js = (JavascriptExecutor) webDriver;
        actions = new Actions(webDriver);
    }

    public void clicarOpcoes(String codigo, String numeroColuna) {
        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getPageSource().contains("Opções"));

        By coluna = By.xpath(".//td[" + numeroColuna + "]");
        if (numeroColuna.matches("0")) {
            coluna = By.xpath(".//td");
        }

        WebElement main = webDriver.findElement(By.cssSelector(".table"));
        List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
        for (WebElement row : rows) {
            if (row.findElement(coluna).getText().equals(codigo)) {
                WebElement btnOpcoes = row.findElement(By.partialLinkText("Opções"));
                while (!btnOpcoes.getDomAttribute("aria-expanded").equals("true")) {
                    js.executeScript("arguments[0].click()", btnOpcoes);
                }
                return;
            }
        }
    }

    public void clicarOpcoesAlterar(String codigo, String numeroColuna) {
        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getPageSource().contains("Opções"));

        By coluna = By.xpath(".//td[" + numeroColuna + "]");
        if (numeroColuna.matches("0")) {
            coluna = By.xpath(".//td");
        }

        WebElement main = webDriver.findElement(By.cssSelector(".table"));
        List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
        for (WebElement row : rows) {
            String textoCompleto = row.findElement(coluna).getText();
            String somenteCodigo = textoCompleto.split(" ")[0];
            if (somenteCodigo.equals(codigo)) {
                WebElement btnOpcoes = row.findElement(By.partialLinkText("Opções"));
                while (!btnOpcoes.getDomAttribute("aria-expanded").equals("true")) {
                    js.executeScript("arguments[0].click()", btnOpcoes);
                }
                return;
            }
        }
    }

    public void popUpAceita(String mensagem) {
        Alert alert = webDriver.switchTo().alert(); // altera "tela" para popup
        assertTrue(alert.getText().contains(mensagem));
        alert.accept(); // fecha popup
        await.pollDelay(3, TimeUnit.SECONDS).until(() -> webDriver.findElement(By.id("idMsgSuccessSession")).getText(), notNullValue());
    }

    public String getValorAtributo(By atributo, String campo) {
        await.until(() -> webDriver.findElement(atributo).isDisplayed());
        return webDriver.findElement(atributo).getDomProperty(campo);
    }
}
