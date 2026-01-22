package com.zetra.econsig.values;

import com.zetra.econsig.helper.senhaexterna.ParamSenhaExternaHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ParamSenhaExternaEnum</p>
 * <p>Description: Enumeração de parâmetros de senha externa.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum ParamSenhaExternaEnum {

    METODO("metodo"),

    DB_DRIVER("driver"),
    DB_URL("url"),
    DB_USERNAME("username"),
    DB_PASSWORD("password"),
    DB_QUERY("query"),
    DB_UPDATE("update"),
    DB_TIMEOUT("timeout"),

    AD_DOMINIO("dominio"),
    AD_SERVIDOR("servidor"),

    SOAP_SERVICE_URL("soap.service.url"),
    SOAP_ACTION("soap.action"),
    SOAP_CONTENT_TYPE("soap.contentType"),
    SOAP_REQUEST_XML("soap.request.xml"),
    SOAP_RESPONSE_FIELD("soap.response.field"),
    SOAP_RESPONSE_VALUE("soap.response.value"),
    SOAP_RESPONSE_CHARSET("soap.response.charset"),
    SOAP_DEBUG("soap.debug"),

    HTTPCLIENT_URL("url"),
    HTTPCLIENT_METODO("httpclient.metodo"),
    HTTPCLIENT_PARAM_USUARIO("httpclient.paramusuario"),
    HTTPCLIENT_PARAM_USUARIO_TIPO("httpclient.paramusuario.tipo"),
    HTTPCLIENT_PARAM_SENHA("httpclient.paramsenha"),
    HTTPCLIENT_PARAM_ESTABELECIMENTO("httpclient.paramestabelecimento"),
    HTTPCLIENT_PARAM_ORGAO("httpclient.paramorgao"),
    HTTPCLIENT_PARAM_CPF("httpclient.paramcpf"),
    HTTPCLIENT_PARAM_CPF_NUMERICO("httpclient.paramcpf.numerico"),
    HTTPCLIENT_PARAM_IP("httpclient.paramip"),
    HTTPCLIENT_PARAM_FIXO("httpclient.paramfixo"),
    HTTPCLIENT_RESULT_OK("httpclient.resultok"),
    HTTPCLIENT_RESULT_ENCODING("httpclient.resultencoding"),
    HTTPCLIENT_REQUEST_ENCODING("httpclient.requestencoding"),
    HTTPCLIENT_KEYSTORE_PASS("httpclient.keystore.pass"),
    HTTPCLIENT_KEYSTORE_PATH("httpclient.keystore.path"),

    JAVA_CLASS_NAME("java.class.name"),

    OAUTH2_URI_AUTHENTICATION_1_CODE("oauth2.uri.authentication.1.code"),
    OAUTH2_URI_AUTHENTICATION_2_TOKEN("oauth2.uri.authentication.2.token"),
    OAUTH2_URI_AUTHENTICATION_3_USERINFO("oauth2.uri.authentication.3.userinfo"),
    OAUTH2_URI_TOKEN_VALIDATION("oauth2.uri.token.validation"),
    OAUTH2_URI_LOGOUT("oauth2.uri.logout"),
    OAUTH2_MSG_LOGOUT("oauth2.message.logout"),
    OAUTH2_CLIENT_ID("oauth2.client.id"),
    OAUTH2_CLIENT_KEY("oauth2.client.key"),
    OAUTH2_PARAM_CPF("oauth2.param.cpf"),
    OAUTH2_PARAM_TOKEN("oauth2.param.token"),
    OAUTH2_PARAM_CODE("oauth2.param.code"),
    OAUTH2_RESPONSE_CPF("oauth2.response.cpf"),
    OAUTH2_RESPONSE_TOKEN("oauth2.response.token"),
    OAUTH2_RESPONSE_EMAIL("oauth2.response.email"),
    OAUTH2_URI_AUTHENTICATION_SCOPE("oauth2.uri.authenticatioin.scope"),
    OAUTH2_METHOD("oauth2.method"),
    OAUTH2_REMOTE_PUBLIC_KEY("oauth2.remote.public.key"),

    JWT_TOKEN("jwt.token"),

    REST_URL("rest.url"),
    REST_PARAM_USUARIO("rest.param.usuario"),
    REST_PARAM_SENHA("rest.param.senha"),
    REST_PARAM_ESTABELECIMENTO("rest.param.estabelecimento"),
    REST_PARAM_ORGAO("rest.param.orgao"),
    REST_PARAM_CPF("rest.param.cpf"),
    REST_RESULT_CAMPO("rest.result.campo"),
    REST_RESULT_VALOR("rest.result.valor"),
    X_API_KEY("x-api-key"),
    USER_AGENT("User-Agent"),

    ;

    private final String param;

    private ParamSenhaExternaEnum(String param) {
        this.param = param;
    }

    public String getChave() {
        return param;
    }

    public String getValor() {
        return getValor(null);
    }

    public String getValor(String defaultValue) {
        String valorParam = ParamSenhaExternaHelper.getValor(param);

        if (TextHelper.isNull(valorParam) && !TextHelper.isNull(defaultValue)) {
            valorParam = defaultValue;
        }

        return valorParam;
    }
}
