package com.zetra.econsig.webclient.margem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: ConsultarMargemServiceClient</p>
 * <p>Description: Cliente para o serviço de consulta de margem externa.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarMargemServiceClient {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarMargemServiceClient.class);

    public static List<MargemTO> consultarMargemExterna(String rseMatricula, AcessoSistema responsavel) throws UsuarioControllerException {

        String urlService = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_CONSULTAR_MARGEM, responsavel);

        if (TextHelper.isNull(urlService)) {
            // Erro parâmetros não configurados
            LOG.error("Parâmetro de sistema " + CodedValues.TPC_URL_SERVICO_CONSULTAR_MARGEM + " deve estar configurado para integração de consulta de margem.");
            throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel);
        }

        List<MargemTO> retorno = new ArrayList<>();
        RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<?> response = restTemplate.exchange(urlService + "?matricula=" + rseMatricula, HttpMethod.GET, httpEntity, String.class);

        if (response != null && response.getStatusCode() != null) {
            LOG.debug("Consulta de margem externa: retorno " + response.getStatusCode().value());

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                JSONParser parser = new JSONParser();
                Object responseObject = null;
                try {
                    responseObject = response.getBody();
                    responseObject = parser.parse((String) response.getBody());
                    JSONArray responseJson = (JSONArray) responseObject;

                    if (responseJson != null && !responseJson.isEmpty()) {
                        for (Object element : responseJson) {
                            JSONObject obj = (JSONObject) element;
                            Short codMargem = ((Long) obj.get("codMargem")).shortValue();
                            String descricaoMargem = (String) obj.get("descMargem");
                            BigDecimal vlrMargem = new BigDecimal((Double) obj.get("vlrMargem"));

                            MargemTO margem = new MargemTO();
                            margem.setMarCodigo(codMargem);
                            margem.setMarDescricao(descricaoMargem);
                            margem.setMrsMargemRest(vlrMargem);
                            retorno.add(margem);
                        }
                    }

                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } else {
            // Não obteve sucesso na integração para consulta de margem
            LOG.error("Não obteve sucesso na integração para consulta de margem.");
            throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel);
        }

        return retorno;
    }
}
