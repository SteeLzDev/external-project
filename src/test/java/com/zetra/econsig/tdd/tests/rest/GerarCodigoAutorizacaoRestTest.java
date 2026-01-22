package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.service.ServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.webservice.rest.request.CodigoUnicoRestResponse;

import io.restassured.http.ContentType;

public class GerarCodigoAutorizacaoRestTest extends AbstractRestTest {

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_NOME = "MARCELO MARTELO";
    private static final String SERVIDOR_CPF = "287.752.740-94";
    private static final String SERVIDOR_MATRICULA = "287752740";
    private static final String SERVIDOR_SENHA = "ser12345";

    @Autowired
    private ServidorService servidorService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @BeforeEach
    public void beforeEach() throws Exception {
        // criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor) {
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA, SERVIDOR_NOME, SERVIDOR_CPF, SERVIDOR_SENHA);
    }

    @Test
    public void givenInvalidCredentials_whenGerarCodigoUnicoRequest_thenUnauthorized() throws Exception {
        // Token antigo
        given()
            .header("Authorization", "Bearer token.mock")
            .contentType(ContentType.JSON)
        .when()
            .post(URL_GERAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        // Sem Token
        given()
            .contentType(ContentType.JSON)
        .when()
            .post(URL_GERAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenValidCredentialsAndValidEmail_whenGerarCodigoUnicoRequest_thenOk() throws Exception {
        // Atualiza o servidor salvando um e-mail
        Servidor servidor = servidorService.obterServidorPeloCpf(SERVIDOR_CPF);
        servidor.setSerEmail("teste.interno@nostrum.com.br");
        servidorService.alterarServidor(servidor);

        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_GERAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void givenValidCredentialsButNoEmail_whenGerarCodigoUnicoRequest_CheckResponse() throws Exception {
        // Atualiza o servidor removendo o e-mail
        Servidor servidor = servidorService.obterServidorPeloCpf(SERVIDOR_CPF);
        servidor.setSerEmail(null);
        servidorService.alterarServidor(servidor);

        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        String senha = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_GERAR_SENHA_AUT_SERVIDOR)
            .andReturn().jsonPath().getString("codigoUnico");

        assertTrue(!TextHelper.isNull(senha));
    }

    @Test
    public void givenInvalidCredentials_whenConsultarCodigoUnicoRequest_thenUnauthorized() throws Exception {
        // Token antigo
        given()
            .header("Authorization", "Bearer token.mock")
            .contentType(ContentType.JSON)
        .when()
            .post(URL_CONSULTAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        // Sem Token
        given()
            .contentType(ContentType.JSON)
        .when()
            .post(URL_CONSULTAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenValidCredentials_whenConsultarCodigoUnicoRequest_thenOk() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_CONSULTAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void givenValidCredentials_whenConsultarCodigoUnicoRequest_CheckResponse() throws Exception {
        usuarioService.removerSenhaAutorizacaoServidor(getLoginServidor(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA));
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        // Gera uma senha
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_GERAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK);

        // Consulta as senhas
        List<CodigoUnicoRestResponse> senhas = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_CONSULTAR_SENHA_AUT_SERVIDOR)
            .andReturn().jsonPath().getList("$", CodigoUnicoRestResponse.class);

        assertTrue(senhas != null && !senhas.isEmpty());
    }

    @Test
    public void givenInvalidCredentials_whenRemoverCodigoUnicoRequest_thenUnauthorized() throws Exception {
        usuarioService.removerSenhaAutorizacaoServidor(getLoginServidor(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA));
        CodigoUnicoRestResponse request = new CodigoUnicoRestResponse();
        request.dataCriacao = "01-01-2020 10:00:00";

        // Token antigo
        given()
            .header("Authorization", "Bearer token.mock")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_REMOVER_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        // Sem Token
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(URL_REMOVER_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void givenValidCredentials_whenRemoverCodigoUnicoRequest_thenOk() throws Exception {
        usuarioService.removerSenhaAutorizacaoServidor(getLoginServidor(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA));
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);

        // Gera uma senha
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_GERAR_SENHA_AUT_SERVIDOR)
        .then()
            .assertThat().statusCode(HttpStatus.SC_OK);

        // Consulta as senhas
        List<CodigoUnicoRestResponse> senhas = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
        .when()
            .post(URL_CONSULTAR_SENHA_AUT_SERVIDOR)
            .andReturn().jsonPath().getList("$", CodigoUnicoRestResponse.class);

        assertTrue(senhas != null && !senhas.isEmpty());

        // Remove as senhas
        for (CodigoUnicoRestResponse senha : senhas) {
            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(senha)
            .when()
                .post(URL_REMOVER_SENHA_AUT_SERVIDOR)
            .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
        }
    }
}
