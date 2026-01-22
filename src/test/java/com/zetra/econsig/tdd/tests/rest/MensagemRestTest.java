package com.zetra.econsig.tdd.tests.rest;

import com.zetra.econsig.service.MensagemServiceTest;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.webservice.rest.request.MensagemRestRequest;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static io.restassured.RestAssured.given;

public class MensagemRestTest extends AbstractRestTest {

    @Autowired
    private MensagemServiceTest mensagemService;

    @Autowired
    private UsuarioServiceTest usuarioServiceTest;

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_NOME = "JOAO DE MARIA";
    private static final String SERVIDOR_CPF = "893.811.390-67";
    private static final String SERVIDOR_MATRICULA = "893811390";
    private static final String SERVIDOR_SENHA = "ser12345";

    @BeforeEach
    public void beforeEach() throws Exception {
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA, SERVIDOR_NOME, SERVIDOR_CPF, SERVIDOR_SENHA);
    }

    @Test
    public void givenRightUsernameAndMessageCode_whenRegistrarLeituraMensagem_thenOk() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);
        String usuCodigo = usuarioServiceTest.getUsuCodigoServ(getLoginServidor(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA));

        MensagemRestRequest request = new MensagemRestRequest();
        request.menCodigo = mensagemService.criarMensagem(usuCodigo);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL_REGISTRAR_LEITURA_MENSAGEM)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void givenInvalidMessageCode_whenRegistrarLeituraMensagemRequest_thenConflict() throws Exception {
        String token = obtainAccessToken(SERVIDOR_CPF, SERVIDOR_SENHA, URL_AUTENTICACAO_SERVIDOR);
        String usuCodigo = usuarioServiceTest.getUsuCodigoServ(getLoginServidor(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, SERVIDOR_MATRICULA));

        MensagemRestRequest request = new MensagemRestRequest();
        request.menCodigo = mensagemService.criarMensagem(usuCodigo) + "1";

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL_REGISTRAR_LEITURA_MENSAGEM)
                .then()
                .assertThat().statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    public void givenInvalidCredentials_whenRegistrarLeituraMensagemRequest_thenUnauthorized() throws Exception {
        MensagemRestRequest request = new MensagemRestRequest();
        request.menCodigo = "123456";

        // Token antigo
        given()
                .header("Authorization", "Bearer token.mock")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL_REGISTRAR_LEITURA_MENSAGEM)
                .then()
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        // Sem Token
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL_REGISTRAR_LEITURA_MENSAGEM)
                .then()
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
}
