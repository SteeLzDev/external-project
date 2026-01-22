package com.zetra.econsig.bdd.steps;

import java.util.Hashtable;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.zetra.econsig.econsig.utils.BaseSelenium;
import com.zetra.econsig.econsig.utils.ScreenshotHelper;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CucumberSeleniumManager {

    private static final Map<Long, WebDriver> webDrivers;

    static {
        webDrivers = new Hashtable<>();
    }

    private static synchronized WebDriver createWebDriver() throws Exception {
        final WebDriver webDriver = BaseSelenium.seleniumStart();
        webDrivers.put(Thread.currentThread().threadId(), webDriver);
        return webDriver;
    }

    @Before
    public static synchronized void beforeScenario(Scenario scenario) throws Exception {
        final WebDriver webDriver = getWebDriver();
        log.info("======================================================");
        log.info("Cenario a ser executado " + scenario.getName());
        log.info("======================================================");
        BaseSelenium.seleniumOpenStartPage(webDriver);
    }

    @After
    public static synchronized void afterScenario(Scenario scenario) throws Exception {
        final WebDriver webDriver = getWebDriver();
        if (scenario.isFailed()) {
            ScreenshotHelper.takeScreenshot(webDriver, scenario.getName());
        }
        BaseSelenium.seleniumEnd(webDriver);
        webDrivers.remove(Thread.currentThread().threadId());
    }

    public static WebDriver getWebDriver() {
        WebDriver webDriver = webDrivers.get(Thread.currentThread().threadId());
        if (webDriver == null) {
            try {
                webDriver = createWebDriver();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return webDriver;
    }
}
