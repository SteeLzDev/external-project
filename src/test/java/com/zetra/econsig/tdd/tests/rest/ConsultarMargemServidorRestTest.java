package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.rest.request.ConsultarMargemRestRequest;

import io.restassured.http.ContentType;

public class ConsultarMargemServidorRestTest extends AbstractRestTest {

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_NOME = "MARIA DE JOAO";
    private static final String SERVIDOR_CPF = "122.779.140-29";
    private static final String SERVIDOR_MATRICULA = "122779140";
    private static final String SERVIDOR_SENHA = "ser12345";
        
    @BeforeEach
    public void beforeEach() {
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA, SERVIDOR_NOME, SERVIDOR_CPF, SERVIDOR_SENHA);
    }

    @Test
    public void givenInvalidCredentials_whenConsultarMargemRequest_thenUnauthorized() {
        ConsultarMargemRestRequest request = new ConsultarMargemRestRequest();

        // Token antigo
        given()
        .header("Authorization", "Bearer token.mock")
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post(URL_CONSULTAR_MARGEM_SERVIDOR)
        .then()
        .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        // Sem Token
        given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post(URL_CONSULTAR_MARGEM_SERVIDOR)
        .then()
        .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenValidCredentials_whenConsultarMargemRequest_CheckResponse() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        ConsultarMargemRestRequest request = new ConsultarMargemRestRequest();

        List<Object> margens = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(URL_CONSULTAR_MARGEM_SERVIDOR)
                .andReturn().jsonPath().getList("$");

        assertTrue(margens != null && !margens.isEmpty());

        for (Object margemObject : margens) {
            assertTrue(margemObject instanceof Map<?,?>);
            if (margemObject instanceof Map<?,?> margem) {
                String marDescricao = (String) margem.get("mar_descricao");
                Short marCodigo = Short.valueOf(margem.get("mar_codigo").toString());
                BigDecimal mrsMargemRest = margem.get("mrs_margem_rest") != null ? new BigDecimal(margem.get("mrs_margem_rest").toString()) : null;

                assertTrue(!TextHelper.isNull(marDescricao));
                assertTrue(!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO));

                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    assertTrue(mrsMargemRest.signum() > 0);
                    assertEquals("MARGEM 1", marDescricao);
                    // Servidor de teste é criado com margem igual a 1000
                    assertEquals(BigDecimal.valueOf(1000.00), mrsMargemRest);
                }
            }
        }
    }    
}
