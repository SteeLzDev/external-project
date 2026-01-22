package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.rest.request.ConsultarMargemRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class IpAcessoInvalidoRestApiSSOAutenticadoTest extends AbstractRestTest {

    private static final String ESTABELECIMENTO_IDENTIFICADOR = "213464140";
    private static final String ORGAO_IDENTIFICADOR = "213464140";
    private static final String SERVIDOR_SENHA = "ser12345";

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    @Autowired
    private UsuarioServiceTest usuarioServiceTest;

    @Autowired
    private ConsignatariaService consignatariaService;

    @Test
    public void givenValid_SSO_Credentials_whenReservarMargemRequest_Invalid_Ip_access() {
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, "S");
        EConsigInitializer.limparCache();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "122709140", "SER INVALID IP RESERVAR MARGEM", "179.779.560-04", SERVIDOR_SENHA, "invipresmargem@econsig.com.br",
                List.of("147.154.0.1", "2012.41.145.4"), null);

        ConsultarMargemRestRequest request = new ConsultarMargemRestRequest();

        Response response = given()
        .header("Authorization", "Bearer token.mock")
        .header("sso_token", "true")
        .header("client", "SampleClientId")
        .header("username", "invipresmargem@econsig.com.br")
        .contentType(ContentType.JSON)
        .body(request)
        .when().post(URL_RESERVAR_MARGEM_SERVIDOR);

        response.then()
        .assertThat().statusCode(HttpStatus.SC_FORBIDDEN);

        ResponseRestRequest body = response.getBody().as(ResponseRestRequest.class);
        String msgOficial = ApplicationResourcesHelper.getMessage("rotulo.endereco.acesso.invalido.ip", AcessoSistema.getAcessoUsuarioSistema());
        msgOficial = msgOficial.substring(0, msgOficial.indexOf("IP"));
        assertTrue(body.getMensagem().contains(msgOficial));

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, "N");
        EConsigInitializer.limparCache();

    }

    @Test
    public void givenValid_SSO_Credentials_whenReservarMargemRequest_Invalid_Ip_access_for_csa() {
        Consignataria csa = consignatariaService.criarConsignataria("csacredentialip", "CSA IP TESTE", "IDNIPDNS", List.of("147.154.0.1", "2012.41.145.4"), new ArrayList<>());
        usuarioServiceTest.criarUsuarioCsaNaConsignatariaCasoNaoExista("ipvalidcsa", "085.791.950-47", "invipcsa@econsig.com.br", CodedValues.STU_ATIVO, csa.getCsaCodigo());

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, "S");
        EConsigInitializer.limparCache();

        ConsultarMargemRestRequest request = new ConsultarMargemRestRequest();

        Response response = given()
        .header("Authorization", "Bearer token.mock")
        .header("sso_token", "true")
        .header("client", "SampleClientId")
        .header("username", "invipcsa@econsig.com.br")
        .contentType(ContentType.JSON)
        .body(request)
        .when().post(URL_RESERVAR_MARGEM_SERVIDOR);

        response.then()
        .assertThat().statusCode(HttpStatus.SC_FORBIDDEN);

        ResponseRestRequest body = response.getBody().as(ResponseRestRequest.class);
        String msgOficial = ApplicationResourcesHelper.getMessage("rotulo.endereco.acesso.invalido.ip", AcessoSistema.getAcessoUsuarioSistema());
        msgOficial = msgOficial.substring(0, msgOficial.indexOf("IP"));
        assertTrue(body.getMensagem().contains(msgOficial));

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, "N");
        EConsigInitializer.limparCache();

    }
    
    @Test
    public void givenValid_SSO_Credentials_whenConsultarMargemRequest_Invalid_Ip_access() {
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, "S");
        EConsigInitializer.limparCache();

        criarServidorParaTest(ESTABELECIMENTO_IDENTIFICADOR, ORGAO_IDENTIFICADOR, "122739140", "SER INVALID IP CONSULT MARGEM", "859.650.560-10", SERVIDOR_SENHA, "invipconsmargem@econsig.com.br",
                List.of("147.154.0.1", "2012.41.145.4"), null);

        ConsultarMargemRestRequest request = new ConsultarMargemRestRequest();

        Response response = given()
        .header("Authorization", "Bearer token.mock")
        .header("sso_token", "true")
        .header("client", "SampleClientId")
        .header("username", "invipconsmargem@econsig.com.br")
        .contentType(ContentType.JSON)
        .body(request)
        .when().post(URL_CONSULTAR_MARGEM_SERVIDOR);

        response.then()
        .assertThat().statusCode(HttpStatus.SC_FORBIDDEN);

        ResponseRestRequest body = response.getBody().as(ResponseRestRequest.class);
        String msgOficial = ApplicationResourcesHelper.getMessage("rotulo.endereco.acesso.invalido.ip", AcessoSistema.getAcessoUsuarioSistema());
        msgOficial = msgOficial.substring(0, msgOficial.indexOf("IP"));
        assertTrue(body.getMensagem().contains(msgOficial));

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, "N");
        EConsigInitializer.limparCache();

    }

}
