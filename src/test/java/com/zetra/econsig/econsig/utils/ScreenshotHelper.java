package com.zetra.econsig.econsig.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScreenshotHelper {

    public static void takeScreenshot(WebDriver webDriver, String testName) {
        if (webDriver != null) {
            final Date data = new Date();
            final DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
            final String imageFileDir = "./target/Screenshot";
            final File file = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);

            try {
                FileUtils.copyFile(file, new File(imageFileDir, testName + "-" + dateFormat.format(data.getTime()) + ".png"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
