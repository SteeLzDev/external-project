package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import io.restassured.http.ContentType;

public class AlterarSenhaUsuarioServidorRestTest extends AbstractRestTest {

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_NOME = "JOAO MARCELO";
    private static final String SERVIDOR_CPF = "840.614.110-71";
    private static final String SERVIDOR_MATRICULA = "840614110";
    private static final String SERVIDOR_SENHA = "ser12345";

    @BeforeEach
    public void beforeEach() throws Exception {
        // criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor) {
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA, SERVIDOR_NOME, SERVIDOR_CPF, SERVIDOR_SENHA);
    }

    @Test
    public void givenInvalidCredentials_whenAlterarSenhaRequest_thenUnauthorized() throws Exception {
        UsuarioRestRequest request = new UsuarioRestRequest();
        request.senha = SERVIDOR_SENHA;

        // Token antigo
        given()
            .header("Authorization", "Bearer token.mock")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        // Sem Token
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenInvalidRequest_whenAlterarSenhaRequest_thenConflict() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        UsuarioRestRequest request = new UsuarioRestRequest();

        // Sem informar senha
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CONFLICT);

        request.senha = SERVIDOR_SENHA;
        // Sem informar nova senha
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CONFLICT);

        request.novaSenha = "12345678";
        // Sem informar confirmação de senha senha
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CONFLICT);

        request.confirmarSenha = "87654321";
        // Nova senha e confirmação de senha com valores diferentes
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CONFLICT);
    }


    @Test
    public void givenWrongPassword_whenAlterarSenhaRequest_thenUnauthorized() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.senha = "12345678";
        request.novaSenha = SERVIDOR_SENHA;
        request.confirmarSenha = SERVIDOR_SENHA;

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenWeakPassword_whenAlterarSenhaRequest_thenConflict() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.senha = SERVIDOR_SENHA;
        request.novaSenha = "12345678";
        request.confirmarSenha = "12345678";

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    public void givenValidCredentials_whenAlterarSenhaRequest_thenOk() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.senha = SERVIDOR_SENHA;
        request.novaSenha = "Ser@1324";
        request.confirmarSenha = "Ser@1324";

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_ALTERAR_SENHA_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK);
    }
}
