package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.ConsignanteService;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import io.restassured.http.ContentType;

public class AutenticarServidorRestTest extends AbstractRestTest {

    @Autowired
    private ConsignanteService consignanteService;

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_NOME = "JOAO DE MARIA";
    private static final String SERVIDOR_CPF = "893.811.390-67";
    private static final String SERVIDOR_MATRICULA = "893811390";
    private static final String SERVIDOR_SENHA = "ser12345";

    @BeforeEach
    public void beforeEach() throws Exception {
        // criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor) {
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA, SERVIDOR_NOME, SERVIDOR_CPF, SERVIDOR_SENHA);
    }

    @Test
    public void givenWrongUsername_whenAuthRequest_thenUnauthorized() throws Exception {
        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "010.010.010-01";
        request.senha = "12345678";

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_AUTENTICACAO_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenWrongPassword_whenAuthRequest_thenConflict() throws Exception {
        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = SERVIDOR_CPF;
        request.senha = "12345678";

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_AUTENTICACAO_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    public void givenValidCredentials_whenAuthRequest_thenOk() throws Exception {
        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = SERVIDOR_CPF;
        request.senha = SERVIDOR_SENHA;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_AUTENTICACAO_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void givenValidCredentialsAndSystemBlocked_whenAuthRequest_thenConflict() throws Exception {
        // Bloqueia o sistema
        consignanteService.alterarStatusConsignante(CodedValues.STS_INDISP.toString());
        EConsigInitializer.limparCache();

        try {
            UsuarioRestRequest request = new UsuarioRestRequest();
            request.id = SERVIDOR_CPF;
            request.senha = SERVIDOR_SENHA;

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post(URL_AUTENTICACAO_SERVIDOR)
            .then()
                .assertThat().statusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);

        } finally {
            // Desbloqueia o sistema
            consignanteService.alterarStatusConsignante(CodedValues.STS_ATIVO.toString());
            EConsigInitializer.limparCache();
        }
    }

    @Test
    public void givenValidCredentials_whenAuthRequest_CheckToken() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);
        assertTrue(!TextHelper.isNull(token));
    }

    @Test
    public void givenValidCredentials_whenAuthRequest_CheckResponse() throws Exception {
        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = SERVIDOR_CPF;
        request.senha = SERVIDOR_SENHA;

        Map<String, Object> rseLogado = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(URL_AUTENTICACAO_SERVIDOR)
                .andReturn().jsonPath().getMap("rseLogado")
                ;

        assertEquals(SERVIDOR_CPF, rseLogado.get("id"));
        assertEquals(SERVIDOR_CPF, rseLogado.get("cpf"));
        assertEquals(SERVIDOR_NOME, rseLogado.get("nome"));
        assertEquals(SERVIDOR_MATRICULA, rseLogado.get("rseMatricula"));
        assertEquals(ORGAO_IDENTIFICADOR, rseLogado.get("orgIdentificador"));
        assertEquals(ESTABELECIMENTO_IDENTIFICADOR, rseLogado.get("estIdentificador"));

        List<?> permissoes = (List<?>) rseLogado.get("permissoes");
        assertTrue(permissoes != null && !permissoes.isEmpty());
    }
}
