package com.zetra.econsig.econsig.utils;

import static com.zetra.econsig.EConsigInitializer.getBaseURL;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BaseSelenium {


    public static WebDriver seleniumStart() throws Exception {
        log.info("======================================================");
        log.info("NOVO WEB DRIVER SOLICITADO");
        log.info("======================================================");


        // Inicializa o WebDriverManager para o ChromeDriver
        WebDriverManager.chromedriver().setup();


        final ChromeOptions options = new ChromeOptions();

        // Configurando preferências aceite pop-up permissao camera DESENV-20415
        final Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
        prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
        prefs.put("profile.default_content_setting_values.geolocation", 1);
        prefs.put("profile.default_content_setting_values.notifications", 1);
        prefs.put("profile.password_manager_leak_detection", false);

        //Adicionando as preferências ao ChromeOptions
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("window-size=1440,900", "--no-sandbox", "--disable-dev-shm-usage",
                             "--ignore-certificate-errors", "--remote-allow-origins=*",
                             "--headless=new");

        final WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().window().maximize();
        webDriver.manage().deleteAllCookies();
        webDriver.manage().window().setSize(new Dimension(1920, 1080));

        return webDriver;
    }


    public static void seleniumEnd(WebDriver webDriver) {
        log.info("======================================================");
        log.info("ENCERRANDO WEB DRIVER SOLICITADO");
        log.info("======================================================");

        if (webDriver != null) {
            webDriver.quit();
        }
    }

    public static void seleniumOpenStartPage(WebDriver webDriver) {
        log.info("======================================================");
        log.info("ABRINDO PAGINA INICIAL DO TESTE");
        log.info("======================================================");

        webDriver.manage().deleteAllCookies();
        webDriver.get(getBaseURL() + "/consig");
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }
}
