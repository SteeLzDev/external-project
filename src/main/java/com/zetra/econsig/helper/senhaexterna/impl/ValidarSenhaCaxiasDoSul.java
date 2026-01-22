package com.zetra.econsig.helper.senhaexterna.impl;

import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_SENHA;

import java.util.Collections;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.webclient.util.RestTemplateFactory;


/**
 * <p>Title: ValidarSenhaRestJson</p>
 * <p>Description: Validação da senha de servidor para um serviço REST
 *    (get ou post) que retorno uma mensagem "combinada" previamente</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarSenhaCaxiasDoSul implements ValidarSenhaExterna {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarSenhaCaxiasDoSul.class);

    private String url                    = null; // URL
    private String resultCampo            = null; // Nome do campo que será procurado o valor esperado para o resultado
    private String resultValor            = null; // Valor que indica que a senha está OK
    private String xApiKey				  = null; //

    public ValidarSenhaCaxiasDoSul() {
        try {
            url                  = ParamSenhaExternaEnum.REST_URL.getValor();
            resultCampo          = ParamSenhaExternaEnum.REST_RESULT_CAMPO.getValor("message");   // Obrigatório, default: message
            resultValor          = ParamSenhaExternaEnum.REST_RESULT_VALOR.getValor("OK");        // Obrigatório, default: OK
            xApiKey              = ParamSenhaExternaEnum.X_API_KEY.getValor();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public CustomTransferObject validarSenha(String[] parametros, Properties messages) {
        final CustomTransferObject result = new CustomTransferObject();
        final String rseMatricula     = parametros[1];
        final String serSenha         = parametros[2];

        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("x-api-key", xApiKey);
            headers.setBasicAuth(rseMatricula, serSenha);

            final HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(AcessoSistema.getAcessoUsuarioSistema());
            final ResponseEntity<String> response = restTemplateSimple.exchange(url, HttpMethod.GET, httpEntity, String.class);

            boolean resultadoConformeEsperado = false;
            LOG.debug("URI:" + url + "\tresultado recebido:|" + response.getBody() + "|");

            if (HttpStatus.OK.equals(response.getStatusCode())) {
            	 final JSONParser parser = new JSONParser();
                 final JSONObject jsonResultado = (JSONObject) parser.parse(response.getBody());

                 final String jsonCampoResultado = (String) jsonResultado.get(resultCampo);
                 resultadoConformeEsperado = jsonCampoResultado.equals(resultValor);
            }

            if (resultadoConformeEsperado) {
                result.setAttribute(KEY_SENHA, serSenha);
            } else {
                result.setAttribute(KEY_SENHA, null);
            }
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            result.setAttribute(KEY_SENHA, null);
        }
        return result;
    }
}
