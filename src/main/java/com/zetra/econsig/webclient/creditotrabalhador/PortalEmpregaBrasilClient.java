package com.zetra.econsig.webclient.creditotrabalhador;

import com.zetra.econsig.exception.PebException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class PortalEmpregaBrasilClient {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PortalEmpregaBrasilClient.class);

    public List<EmprestimoCreditoTrabalhadorPojo> getContratosPortalEmpregaBrasil(String cnpj, String competencia) throws PebException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        try {
            final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_PORTAL_EMPREGA_BRASIL, responsavel);
            if (TextHelper.isNull(urlBase)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.url.peb", responsavel));
                final PebException exception = new PebException("mensagem.erro.configuracao.url.peb", responsavel);
                exception.setPebError(PebErrorCodeEnum.GENERIC_ERROR);
                throw exception;
            }

            if (TextHelper.isNull(cnpj) || TextHelper.isNull(competencia)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.url.parametros.peb", responsavel));
                final PebException exception = new PebException("mensagem.erro.configuracao.url.parametros.peb", responsavel);
                exception.setPebError(PebErrorCodeEnum.GENERIC_ERROR);
                throw exception;
            }

            final String url = urlBase + "?" + "numeroInscricao=" + cnpj + "&competencia=" + competencia;

            final RestTemplate restTemplate = new RestTemplate();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            restTemplate.setRequestFactory(requestFactory);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
            final ResponseEntity<List<EmprestimoCreditoTrabalhadorPojo>> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<EmprestimoCreditoTrabalhadorPojo>>() {});

            List<EmprestimoCreditoTrabalhadorPojo> listaEmprestimos = response.getBody();

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new PebException("mensagem.erro.response.peb", responsavel);
            }

            return listaEmprestimos;
        }catch(final RestClientException | NullPointerException ex){
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.generico.peb", responsavel));
            final PebException exception = new PebException("mensagem.erro.generico.peb", responsavel);
            exception.setPebError(PebErrorCodeEnum.GENERIC_ERROR);
            throw exception;
        } catch (final Exception ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.generico.peb", responsavel));
            final PebException exception = new PebException("mensagem.erro.generico.peb", responsavel);
            exception.setPebError(PebErrorCodeEnum.GENERIC_ERROR);
            throw exception;
        }
    }
 }
