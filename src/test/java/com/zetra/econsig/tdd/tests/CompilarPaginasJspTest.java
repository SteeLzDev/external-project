package com.zetra.econsig.tdd.tests;

import static com.zetra.econsig.EConsigInitializer.getBaseURL;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompilarPaginasJspTest extends BaseTest {

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
    }

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

    @Test
    public void compilarJsp() {
        webDriver.get(getBaseURL() + "/consig/v3/compilar");
        await().atMost(Duration.ofMinutes(3)).until(() -> webDriver.getPageSource(), containsString("(0)")); // Error count = 0
    }
}
