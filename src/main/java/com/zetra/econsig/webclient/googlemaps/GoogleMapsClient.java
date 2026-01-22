package com.zetra.econsig.webclient.googlemaps;

import java.math.BigDecimal;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

public class GoogleMapsClient {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GoogleMapsClient.class);

    /**
     * Consulta a API Geocoding do Google Maps Platform para retornar latitude e longitude
     * @return Array BigDecimal com a Latitude na primeira posição e Longitude na segunda posição
     * @throws GoogleMapsException
     */
    public static BigDecimal[] buscaLatitudeLongitude(String logradouro, String numero, String bairro, String municipio, String uf, String cep, AcessoSistema responsavel) throws GoogleMapsException {

        RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
        BigDecimal[] retorno = null;

        String urlGeolocation = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_GEOCODIFICACAO, responsavel);
        String apiKeyGeolocation = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CHAVE_ACESSO_SERVICO_GEOCODIFICACAO, responsavel);

        if (TextHelper.isNull(urlGeolocation)) {
            LOG.error("Erro de configuração do parâmetro de sistema URL para serviço de geocodificação (776).");
            throw new GoogleMapsException("mensagem.erro.geocodificacao.configuracao.parametro.url", responsavel);

        }

        if (TextHelper.isNull(apiKeyGeolocation)) {
            LOG.error("Erro de configuração do parâmetro de sistema chave de acesso para serviço de geocodificação (777).");
            throw new GoogleMapsException("mensagem.erro.geocodificacao.configuracao.parametro.chave", responsavel);
        }

        urlGeolocation += "?address=";

        String endereco = "";

        if (!TextHelper.isNull(logradouro)) {
            endereco += logradouro + " ";
        }

        if (!TextHelper.isNull(numero)) {
            endereco += numero + " ";
        }

        if (!TextHelper.isNull(bairro)) {
            endereco += bairro + " ";
        }

        if (!TextHelper.isNull(municipio)) {
            endereco += municipio + " ";
        }

        if (!TextHelper.isNull(uf)) {
            endereco += uf + " ";
        }

        if (!TextHelper.isNull(cep)) {
            endereco += cep;
        }

        if (TextHelper.isNull(endereco)) {
            LOG.error("Endereço não enviado");
            throw new GoogleMapsException("mensagem.erro.geocodificacao.endereco", responsavel);
        }

        urlGeolocation += endereco + "&key=" + apiKeyGeolocation;

        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplate.getForEntity(urlGeolocation, String.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new GoogleMapsException("mensagem.erro.geocodificacao.obter.latitude.longitude", responsavel);
        }
        String status = null;

        //verifica se ocorreu tudo certo na requisição
        if (responseEntity != null && responseEntity.getStatusCode() != null && responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null) {
            JSONParser parser = new JSONParser();
            Object responseObject = null;
            try {
                responseObject = parser.parse(responseEntity.getBody());
                JSONObject responseJson = (JSONObject) responseObject;
                status = (String) responseJson.get("status");
                //apesar de retornar 200 pode voltar com request_denied
                if (!TextHelper.isNull(status) && status.trim().equalsIgnoreCase("OK")) {
                    JSONArray results = (JSONArray) responseJson.get("results");
                    if (results != null && !results.isEmpty()) {
                        JSONObject result = (JSONObject) results.get(0);
                        JSONObject geometry = (JSONObject) result.get("geometry");
                        if (geometry != null) {
                            JSONObject location = (JSONObject) geometry.get("location");
                            if (location != null) {
                                Double lat = (Double) location.get("lat");
                                Double lng = (Double) location.get("lng");
                                retorno = new BigDecimal[] { BigDecimal.valueOf(lat), BigDecimal.valueOf(lng) };
                            }
                        }
                    }
                }
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new GoogleMapsException(ex);
            }
        }

        if (retorno == null) {
            LOG.error("Não obteve resultado da chamada ao Geocoding (Google Maps Platform).");
            if (!TextHelper.isNull(status)) {
                LOG.error("Status da requisição: " + status);
            }
            throw new GoogleMapsException("mensagem.erro.geocodificacao.obter.latitude.longitude", responsavel);
        }

        return retorno;
    }
}
