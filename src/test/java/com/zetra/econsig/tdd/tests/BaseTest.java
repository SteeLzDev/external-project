package com.zetra.econsig.tdd.tests;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;

import com.zetra.econsig.config.ContextSpringConfiguration;
import com.zetra.econsig.econsig.utils.BaseSelenium;
import com.zetra.econsig.econsig.utils.ScreenshotHelper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class BaseTest extends ContextSpringConfiguration {

    protected WebDriver webDriver;

    protected void setUp() throws Exception {
        webDriver = BaseSelenium.seleniumStart();
        BaseSelenium.seleniumOpenStartPage(webDriver);
    }

    protected void tearDown() throws Exception {
        BaseSelenium.seleniumEnd(webDriver);
    }

    @RegisterExtension
    AfterTestExecutionCallback afterTestExecutionCallback = context -> {
        Optional<Throwable> exception = context.getExecutionException();
        if (exception.isPresent()) {
            String testName = context.getTestMethod().get().getName();
            log.info("Test Failed for test {}: ", context.getDisplayName());

            ScreenshotHelper.takeScreenshot(webDriver, testName);
        }
    };
}
