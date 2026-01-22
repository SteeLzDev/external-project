package com.zetra.econsig.helper;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumHelper {

    /**
     * Seleciona pelo texto contido no select
     * (Exatamente o que você vê quando está usando pelo browser)
     * @param bySelect
     * @param selectVisibleText
     */
    public static void selectByVisibleText(WebDriver d, By bySelect, String selectVisibleText) {
        selectByVisibleText(d.findElement(bySelect), selectVisibleText);
    }

    /**
     * Seleciona pelo texto contido no select
     * (Exatamente o que você vê quando está usando pelo browser)
     * @param bySelect
     * @param selectVisibleText
     */
    public static void selectByVisibleText(WebElement element, String selectVisibleText) {
        final Select select = new Select(element);
        for (final WebElement option : select.getOptions()) {
            if (option.getText().trim().contains(selectVisibleText)) {
                option.click();
                break;
            }
        }
    }

    /**
     * Seleciona pelo valor da tag <option>
     * (Geralmente é o id do objeto no banco de dados)
     * @param bySelect
     * @param selectVisibleText
     */
    public static void selectByOptionValue(WebDriver d, By bySelect, String optionValueText) {
        final Select select = new Select(d.findElement(bySelect));
        for (final WebElement option : select.getOptions()) {
            if (option.getDomProperty("value").trim().contains(optionValueText)) {
                option.click();
                break;
            }
        }
    }

    public static WebElement clickAndWait(WebDriver d, By elementLocator, By elementLocatorToWait) {
        d.findElement(elementLocator).click();
        final Wait<WebDriver> wait = new WebDriverWait(d, Duration.ofSeconds(30));
        final WebElement element = wait.until(visibilityOfElementLocated(elementLocatorToWait));
        return element;
    }

    public static WebElement waitForElemment(WebDriver d, By elementLocatorToWait) {
        final Wait<WebDriver> wait = new WebDriverWait(d, Duration.ofSeconds(30));
        final WebElement element = wait.until(visibilityOfElementLocated(elementLocatorToWait));
        return element;
    }

    public static WebElement waitForAnyElemment(WebDriver d, By... elementLocatorToWait) {
        final Wait<WebDriver> wait = new WebDriverWait(d, Duration.ofSeconds(30));
        final WebElement element = wait.until(visibilityOfElementLocated(elementLocatorToWait));
        return element;
    }

    public static ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
        return driver -> {
            final WebElement toReturn = driver.findElement(locator);
            if (toReturn.isDisplayed()) {
                return toReturn;
            }
            return null;
        };
    }

    public static ExpectedCondition<WebElement> visibilityOfElementLocated(final By... locators) {
        return driver -> {
            for (By l : locators) {
                final WebElement toReturn = driver.findElement(l);
                if (toReturn.isDisplayed()) {
                    return toReturn;
                }
            }
            return null;
        };
    }

    public static List<WebElement> waitForElemments(WebDriver driver, By elementLocatorToWait, long timeInSeconds) throws Throwable {
        final Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
        final List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(elementLocatorToWait));
        return elements;
    }

    public static boolean isAlertPresent(WebDriver webDriver) {
        final WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));
        boolean alert = false;

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            alert = true;
        } catch (final TimeoutException eTO) {
            alert = false;
        }
        return alert;
    }

    public static boolean isElementPresent(WebDriver webDriver, WebElement element) {
        final WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));
        boolean isPresent = false;

        try {
            wait.pollingEvery(Duration.ofSeconds(2)).until(ExpectedConditions.visibilityOf(element));
            isPresent = true;
        } catch (final TimeoutException | StaleElementReferenceException exception) {
            isPresent = false;
        }
        return isPresent;
    }
}
