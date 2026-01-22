package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioChaveSessaoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RecuperaDadosValidacaoSerRestTest extends AbstractRestTest {

    private MockRestServiceServer mockServer;

    @Autowired
    private UsuarioChaveSessaoService usuarioChaveSessaoService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    @Qualifier("simpleRestemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    private final String tpcCodigo = "661";

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_NOME = "USU VALIDACAO SER";
    private static final String SERVIDOR_SENHA = "ser12345";

    @Test
    public void invalid_sso_token_api_v2_dados_validacao_ser() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "98574117", SERVIDOR_NOME, "862.260.730-55", SERVIDOR_SENHA, "ususervalidser@econsig.com.br");
        usuarioService.criarUsuarioSup("usuvalidser", "088.571.430-07", "1", "usuvalidser@econsig.com.br");

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser%2540econsig.com.br" + "&token=" + "ABGM6VE4NFV33XNW" + "&client=" + "SampleClientId")))
        .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(401)));

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "862.260.730-55";
        request.mobile = true;

        given()
        .header("Authorization", "Bearer token.mock")
        .header("username", "usuvalidser@econsig.com.br")
        .header("client", "SampleClientId")
        .header("sso_token", "true")
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2)
        .then()
        .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void sso_token_not_sent_api_v2_dados_validacao_ser() {
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "6894714", "USU VALIDACAO SER 2", "082.666.410-56", SERVIDOR_SENHA, "ususer2validser@econsig.com.br");
        usuarioService.criarUsuarioSup("usuvalidser2", "546.606.020-87", "1", "usuvalidser2@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "082.666.410-56";
        request.mobile = true;

        given()
        .header("username", "usuvalidser2@econsig.com.br")
        .header("client", "SampleClientId")
        .header("sso_token", "true")
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2)
        .then()
        .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_returns_ok() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132259", "USU VALIDACAO SER 3", "262.066.480-28", SERVIDOR_SENHA, "ususer3validser@econsig.com.br");
        usuarioService.criarUsuarioSup("usuvalidser3", "250.023.690-06", "1", "usuvalidser3@econsig.com.br");

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser3%2540econsig.com.br" + "&token=" + "ABGM6VE4NFV33XNW" + "&client=" + "SampleClientId")))
        .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "262.066.480-28";
        request.mobile = true;

        Response response = given()
                .header("Authorization", "Bearer token.mock")
        .header("username", "usuvalidser3@econsig.com.br")
        .header("client", "SampleClientId")
        .header("sso_token", "true")
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then()
        .assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals( "262.066.480-28", bodyResponse.get("id"));
        assertEquals(CodedValues.STU_ATIVO, bodyResponse.get("statusUsuario"));
        assertEquals("ususer3validser@econsig.com.br", bodyResponse.get("email"));
        assertEquals(false, bodyResponse.get("integracaoEconsigSalaryPay"));
    }

    @Test
    public void econsig_token_api_v2_dados_validacao_ser_returns_ok() throws CreateException {
        Usuario usuSer = criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "14792474", "USU VALIDACAO SER 4", "441.814.310-61", SERVIDOR_SENHA, "ususer4validser@econsig.com.br");
        usuarioService.criarUsuarioSup("usuvalidser4", "896.849.680-35", "1", "usuvalidser4@econsig.com.br");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);

        usuarioChaveSessaoService.criarUsuarioChaveSessao(usuSer.getUsuCodigo(), "ABGM6VE4NFV33XNW", calendar.getTime());

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "441.814.310-61";
        request.mobile = true;

        Response response = given()
        .header("Authorization", "Bearer token.mock")
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then()
        .assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals( "441.814.310-61", bodyResponse.get("id"));
        assertEquals(CodedValues.STU_ATIVO, bodyResponse.get("statusUsuario"));
        assertEquals("ususer4validser@econsig.com.br", bodyResponse.get("email"));
        assertEquals(false, bodyResponse.get("integracaoEconsigSalaryPay"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_todos_registro_servidor_bloqueado() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132278", "USU VALIDACAO SER TODOS BLOQUEADOS SSO 5", "705.438.390-79", SERVIDOR_SENHA, "usuvalidserbloqueadosso5@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132279", "USU VALIDACAO SER TODOS BLOQUEADOS 6", "705.438.390-79", SERVIDOR_SENHA, "usuvalidserbloqueadosso5@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132280", "USU VALIDACAO SER TODOS BLOQUEADOS 7", "705.438.390-79", SERVIDOR_SENHA, "usuvalidserbloqueadosso5@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132281", "USU VALIDACAO SER TODOS BLOQUEADOS 8", "705.438.390-79", SERVIDOR_SENHA, "usuvalidserbloqueadosso5@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132282", "USU VALIDACAO SER TODOS BLOQUEADOS 9", "705.438.390-79", SERVIDOR_SENHA, "usuvalidserbloqueadosso5@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);

        usuarioService.criarUsuarioSup("usuvalidsersso5", "243.885.250-06", "1", "usuvalidsersso5@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "705.438.390-79";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidsersso5%2540econsig.com.br" + "&token=" + "ADFE5VE4NFS88XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidsersso5@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("705.438.390-79", bodyResponse.get("id"));
        assertEquals("usuvalidserbloqueadosso5@econsig.com.br", bodyResponse.get("email"));

        final List<String> listMatricula = List.of("87132278", "87132279", "87132280", "87132281", "87132282");

        //verifica resultado da matricula do registro servidor de acordo com o status que deve ser selecionado
        assertTrue(listMatricula.contains(bodyResponse.get("rseMatricula")));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_BLOQUEADO, bodyResponse.get("statusUsuario"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_todos_registro_servidor_ativo() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132265", "USU VALIDACAO SER TODOS ATIVOS 5", "913.284.750-50", SERVIDOR_SENHA, "usuvalidserativo5@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132266", "USU VALIDACAO SER TODOS ATIVOS 6", "913.284.750-50", SERVIDOR_SENHA, "usuvalidserativo6@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132267", "USU VALIDACAO SER TODOS ATIVOS 7", "913.284.750-50", SERVIDOR_SENHA, "usuvalidserativo7@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132268", "USU VALIDACAO SER TODOS ATIVOS 8", "913.284.750-50", SERVIDOR_SENHA, "usuvalidserativo8@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132269", "USU VALIDACAO SER TODOS ATIVOS 9", "913.284.750-50", SERVIDOR_SENHA, "usuvalidserativo9@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);

        usuarioService.criarUsuarioSup("usuvalidser6", "122.266.590-56", "1", "usuvalidser6@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "913.284.750-50";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser6%2540econsig.com.br" + "&token=" + "ADFE7VE4NFV33XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser6@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("913.284.750-50", bodyResponse.get("id"));
        assertEquals("usuvalidserativo5@econsig.com.br", bodyResponse.get("email"));

        final List<String> listMatricula = List.of("87132265", "87132266", "87132267", "87132268", "87132269");

        //verifica resultado da matricula do registro servidor de acordo com o status que deve ser selecionado
        assertTrue(listMatricula.contains(bodyResponse.get("rseMatricula")));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_ATIVO, bodyResponse.get("statusUsuario"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_lista_servidores_busca_status_ativo() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132270", "USU VALIDACAO LISTA UM ATIVO 5", "953.242.840-25", SERVIDOR_SENHA, "usuvalidserumativo5@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132271", "USU VALIDACAO LISTA UM ATIVO 6", "953.242.840-25", SERVIDOR_SENHA, "usuvalidserumativo6@econsig.com.br", CodedValues.SRS_EXCLUIDO, CodedValues.STU_EXCLUIDO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132272", "USU VALIDACAO LISTA UM ATIVO 7", "953.242.840-25", SERVIDOR_SENHA, "usuvalidserumativo7@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132273", "USU VALIDACAO LISTA UM ATIVO 8", "953.242.840-25", SERVIDOR_SENHA, "usuvalidserumativo8@econsig.com.br", CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA, CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132274", "USU VALIDACAO LISTA UM ATIVO 9", "953.242.840-25", SERVIDOR_SENHA, "usuvalidserumativo9@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);

        usuarioService.criarUsuarioSup("usuvalidser7", "635.347.340-09", "1", "usuvalidser7@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "953.242.840-25";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser7%2540econsig.com.br" + "&token=" + "ABEE6VE4NFV33XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser7@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("953.242.840-25", bodyResponse.get("id"));
        assertEquals("usuvalidserumativo5@econsig.com.br", bodyResponse.get("email"));

        //verifica resultado da matricula do registro servidor de acordo com o status que deve ser selecionado
        assertEquals("87132270", bodyResponse.get("rseMatricula"));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_ATIVO, bodyResponse.get("statusUsuario"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_excluido_lista_servidores_busca_status_ativo() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132290", "USU VALIDACAO LISTA EXCLUIDO 7", "770.483.350-25", SERVIDOR_SENHA, "usuvalidserumexcluido7@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_EXCLUIDO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132291", "USU VALIDACAO LISTA EXCLUIDO 8", "770.483.350-25", SERVIDOR_SENHA, "usuvalidserumexcluido8@econsig.com.br", CodedValues.SRS_EXCLUIDO, CodedValues.STU_ATIVO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132292", "USU VALIDACAO LISTA EXCLUIDO 9", "770.483.350-25", SERVIDOR_SENHA, "usuvalidserumexcluido9@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132293", "USU VALIDACAO LISTA EXCLUIDO 10", "770.483.350-25", SERVIDOR_SENHA, "usuvalidserumexcluido10@econsig.com.br", CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA, CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132294", "USU VALIDACAO LISTA EXCLUIDO 11", "770.483.350-25", SERVIDOR_SENHA, "usuvalidserumexcluido11@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);

        usuarioService.criarUsuarioSup("usuvalidser12", "259.526.370-60", "1", "usuvalidser12@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "770.483.350-25";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser12%2540econsig.com.br" + "&token=" + "ACEE1VE2NAV33XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser12@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("770.483.350-25", bodyResponse.get("id"));
        assertEquals("usuvalidserumexcluido7@econsig.com.br", bodyResponse.get("email"));

        //verifica resultado da matricula do registro servidor de acordo com o status que deve ser selecionado
        assertEquals("87132290", bodyResponse.get("rseMatricula"));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_EXCLUIDO, bodyResponse.get("statusUsuario"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_um_registro_servidor_ativo() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132275", "USU VALIDACAO SER ATIVO 5", "341.789.010-10", SERVIDOR_SENHA, "usuvalidserumregistroativo5@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_ATIVO);

        usuarioService.criarUsuarioSup("usuvalidser8", "411.443.250-72", "1", "usuvalidser8@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "341.789.010-10";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser8%2540econsig.com.br" + "&token=" + "A55E6AE4NSV33XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser8@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("341.789.010-10", bodyResponse.get("id"));
        assertEquals("usuvalidserumregistroativo5@econsig.com.br", bodyResponse.get("email"));

        //verifica resultado da matricula do registro servidor de acordo com o status que deve ser selecionado
        assertEquals("87132275", bodyResponse.get("rseMatricula"));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_ATIVO, bodyResponse.get("statusUsuario"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_um_registro_servidor_bloquado() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132276", "USU VALIDACAO SER BLOQUEADO 5", "024.985.860-67", SERVIDOR_SENHA, "usuvalidserumregistrobloqueado5@econsig.com.br", CodedValues.SRS_BLOQUEADO, CodedValues.STU_BLOQUEADO);

        usuarioService.criarUsuarioSup("usuvalidser9", "263.603.280-03", "1", "usuvalidser9@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "024.985.860-67";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser9%2540econsig.com.br" + "&token=" + "A55E6VE4NFV33XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser9@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("024.985.860-67", bodyResponse.get("id"));
        assertEquals("usuvalidserumregistrobloqueado5@econsig.com.br", bodyResponse.get("email"));

        //verifica resultado da matricula do registro servidor de acordo com o status que deve ser selecionado
        assertEquals("87132276", bodyResponse.get("rseMatricula"));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_BLOQUEADO, bodyResponse.get("statusUsuario"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_um_registro_servidor_excluido_error() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132277", "USU VALIDACAO SER EXCLUIDO 5", "594.669.120-11", SERVIDOR_SENHA, "usuvalidserumregistroexcluido5@econsig.com.br", CodedValues.SRS_EXCLUIDO, CodedValues.STU_EXCLUIDO);

        usuarioService.criarUsuarioSup("usuvalidser10", "822.108.430-14", "1", "usuvalidser10@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "594.669.120-11";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser10%2540econsig.com.br" + "&token=" + "A22E6VF4DAV22XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser10@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_CONFLICT);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("Servidor não encontrado.", bodyResponse.get("mensagem"));
    }

    @Test
    public void sso_token_api_v2_dados_validacao_ser_retornar_status_ativo_registro_usuario_ser_excluido_error() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        final String psiVlr = parametroSistemaService.getParamSistemaConsignante(tpcCodigo).getPsiVlr();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "87132295", "USU VALIDACAO EXCLUIDO 6", "419.028.340-11", SERVIDOR_SENHA, "usuvalidserregistrousuarioxcluido5@econsig.com.br", CodedValues.SRS_ATIVO, CodedValues.STU_EXCLUIDO);

        usuarioService.criarUsuarioSup("usuvalidser11", "801.882.090-26", "1", "usuvalidser11@econsig.com.br");

        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = "419.028.340-11";
        request.mobile = true;
        request.retornaApenasSrsAtivo = true;

        mockServer.reset();
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(psiVlr + "/sso/v0/oauth/token/verify?username=" + "usuvalidser11%2540econsig.com.br" + "&token=" + "A33E7AF4DAV22XNW" + "&client=" + "SampleClientId")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, equalTo("application/x-www-form-urlencoded")))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)));

        final Response response = given()
                .header("Authorization", "Bearer token.mock")
                .header("username", "usuvalidser11@econsig.com.br")
                .header("client", "SampleClientId")
                .header("sso_token", "true")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get(URL_RECUPERA_DADOS_SER_VALIDACAO_V2);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        Map<?,?> bodyResponse = response.getBody().as(Map.class);

        assertEquals("419.028.340-11", bodyResponse.get("id"));
        assertEquals("usuvalidserregistrousuarioxcluido5@econsig.com.br", bodyResponse.get("email"));
        assertEquals("87132295", bodyResponse.get("rseMatricula"));

        //verifica resultado do status do usuario de acordo com a matricula do registro servidor retornada
        assertEquals(CodedValues.STU_EXCLUIDO, bodyResponse.get("statusUsuario"));
    }

}
