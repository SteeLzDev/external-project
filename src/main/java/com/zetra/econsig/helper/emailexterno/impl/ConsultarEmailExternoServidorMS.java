package com.zetra.econsig.helper.emailexterno.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidor;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ParamEmailExternoServidorEnum;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: ConsultarEmailExternoServidorMS</p>
 * <p>Description: Implementação da interface para consulta de e-mail de servidores em API externa
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ConsultarEmailExternoServidorMS implements ConsultarEmailExternoServidor{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarEmailExternoServidorMS.class);

    private final String paramCpf = "cpf";
    private final String resultSuccessData = ParamEmailExternoServidorEnum.RESULT_SUCCESS_DATA.getChave();
    private final String resultErrorData = ParamEmailExternoServidorEnum.RESULT_ERROR_DATA.getChave();
    private final String resultStatus = ParamEmailExternoServidorEnum.RESULT_STATUS.getChave();
    private static String URL_API = null;

    /**
     * Consulta e-mail de servidor em API externa.
     *
     * @param parametros
     *            Parametros da consulta de e-mail:
     *            0. cpf: cpf do servidor
     * @return Um {@link CustomTransferObject} com a resposta do
     * da API externa. Os atributos da resposta serão: status, ok, erros,data.
     */
    @Override
    public CustomTransferObject consultarEmailExternoServidor(String parametro) {
        final CustomTransferObject result = new CustomTransferObject();
        URL_API = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BUSCA_EMAIL_SERVIDOR_API_EXTERNA, AcessoSistema.getAcessoUsuarioSistema());
        try {
            if(TextHelper.isNull(URL_API)) {
                throw new ZetraException("mensagem.erro.rest.consultar.email.externo.url", AcessoSistema.getAcessoUsuarioSistema());
            }

            final Map<String, String> camposJson = new HashMap<>();
            final JSONParser parser = new JSONParser();
            JSONObject jsonResultado = null;

            camposJson.put(paramCpf, TextHelper.format(parametro.trim(), "###########"));
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            final JSONObject jsonObject = new JSONObject(camposJson);
            final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(AcessoSistema.getAcessoUsuarioSistema());
            final ResponseEntity<String> response = restTemplateSimple.postForEntity(URL_API, httpEntity, String.class);

            LOG.debug("URI:" + URL_API + "\tresultado recebido:|" + response.getBody() + "|" + "\thttpStatus: " + response.getStatusCode());

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                jsonResultado = (JSONObject) parser.parse(response.getBody());
                result.setAttribute(resultSuccessData, jsonResultado.get(resultSuccessData));
                result.setAttribute(resultStatus, response.getStatusCode());
            }else if(HttpStatus.CONFLICT.equals(response.getStatusCode())){
                jsonResultado = (JSONObject) parser.parse(response.getBody());
                result.setAttribute(resultErrorData, concatenarErrosEmString((JSONArray) jsonResultado.get(resultErrorData)));
                result.setAttribute(resultStatus,  response.getStatusCode());
            }else {
                throw new ZetraException("mensagem.erro.rest.consultar.email.externo.servidor.data", AcessoSistema.getAcessoUsuarioSistema());
            }
        }catch(final ZetraException e) {
            LOG.error(e.getMessage(), e);
            if(TextHelper.isNull(URL_API)) {
                result.setAttribute(resultErrorData, e.getMessage());
            }else {
                result.setAttribute(resultErrorData, e.getMessage());
            }
        } catch (final ParseException e) {
            LOG.error(e.getMessage(), e);
            result.setAttribute(resultErrorData, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema()));
        }

        return result;
    }

    private String concatenarErrosEmString(JSONArray jsonArray) {
        final StringBuilder erros = new StringBuilder();
        for(int i = 0;i < jsonArray.size(); i++) {
            erros.append(jsonArray.get(i));
            if(i < (jsonArray.size() - 1)) {
                erros.append(". ");
            }
        }
        return erros.toString();
    }


}
