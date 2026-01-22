package com.zetra.econsig.enomina.config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.enomina.ENominaInitializer;

import io.restassured.RestAssured;
import lombok.extern.log4j.Log4j2;

@ContextConfiguration(initializers = { ENominaInitializer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Log4j2
public class ENominaContextSpringConfiguration {

    private static boolean hasWaited;

    @BeforeEach
    public void waitSystemStart() {
        if (!hasWaited) {
            Awaitility
                .await().atMost(10, TimeUnit.MINUTES)
                .ignoreExceptions()
                .until(() -> {
                    log.info("Esperando o sistema inicializar");

                    try {
                        RestAssured.useRelaxedHTTPSValidation();
                        given()
                            .when()
                                .get(EConsigInitializer.getBaseURL() + "/consig/v3/verificarStatusSistema?debugLevel=3") // confere status do sistema
                            .then()
                                .assertThat()
                                    .statusCode(HttpStatus.SC_OK)
                                    .and()
                                    .body(equalTo("OK"));

                        return true;

                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                        return false;
                    }
                });
            hasWaited = true;
        } else {
            log.info("Já esperou o sistema inicializar, não precisa aguardar novamente.");
        }
    }
}
