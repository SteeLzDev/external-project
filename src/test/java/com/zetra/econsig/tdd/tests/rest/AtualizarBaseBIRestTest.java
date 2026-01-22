package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.web.controller.rest.ExecucaoRemotaRequest;

import io.restassured.http.ContentType;

public class AtualizarBaseBIRestTest extends AbstractRestTest {

    @Test
    public void givenAllSet_whenExecutarAtualizarBaseBIRequest_thenOk() throws Exception {
        final ExecucaoRemotaRequest request = new ExecucaoRemotaRequest();
        request.setNomeClasseRotina("com.zetra.econsig.helper.bi.AtualizaBaseBI");
        request.setParametrosRotina(new String[]{"0","0"});

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/consig/v3/executarRotina")
        .then()
            .assertThat()
                .statusCode(HttpStatus.SC_OK)
            .and()
                .body(equalTo("0"));
    }
}
