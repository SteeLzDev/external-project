package com.zetra.econsig.webclient.crm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.CRMException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.CNPJHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webclient.sso.SSOClient;
import com.zetra.econsig.webclient.util.EconsigResponseErrorHandler;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: CRMClient</p>
 * <p>Description: Implementação para consumir microserviço CRM.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @uthor Fagner Luiz, Leonel Martins
 */
@Controller
public class CRMClient {

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CRMClient.class);

    @Autowired
    private CRMConfigProperties properties;

    /**
     *
     * @param csaNome
     * @param csaCnpj
     * @param csaIdInterno
     * @return
     * @throws CRMException
     */
    @SuppressWarnings({ "java:S2259" })
    public String updateServiceProvider(String csaNome, String csaCnpj, String csaIdInterno) throws CRMException {
    	final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

    	try {

			final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_CRM, responsavel);

			if (TextHelper.isNull(urlBase)) {
			    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
			    final CRMException exception = new CRMException("mensagem.usuarioSenhaInvalidos", responsavel);
			    exception.setCrmError(CRMErrorCodeEnum.GENERIC_ERROR);
			    throw exception;
			}

            final String loginAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_LOGIN, responsavel);
            final String senhaAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_SENHA, responsavel);

            if (TextHelper.isNull(loginAdm) || TextHelper.isNull(senhaAdm)) {
                throw new SSOException("mensagem.informe.sso.usuario.senha.nao.configurado", responsavel);
            }

            // Logar usuário admin para inclusão de novo usuário
			final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
            final SSOToken token = ssoClient.autenticar(loginAdm, senhaAdm);

			final RestTemplate restTemplate = buildRestTemplate(!properties.getSslClientAuth().trim().equalsIgnoreCase("none"), responsavel);

			final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
			final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
			final String cseIdInterno = cse.getIdentificadorInterno();

	        final HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+ token.access_token);

            final String cnpjNormalizado = CNPJHelper.getCnpjSemMascara(csaCnpj);

            final HashMap<String, String> additionalDetails = new HashMap<>();
            additionalDetails.put("name", csaNome);
            additionalDetails.put("registrationNumber", cnpjNormalizado);
            additionalDetails.put("integrationCode", csaIdInterno);

            final JSONObject jsonObject = new JSONObject(additionalDetails);
            final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            final ResponseEntity<ServiceProviderPojo> response = restTemplate.exchange(urlBase + "/crm/external/v0/serviceprovider?id=" + cseIdInterno, HttpMethod.POST, httpEntity, ServiceProviderPojo.class);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
	            throw new CRMException("mensagem.usuario.erro.autenticacao.servico", responsavel);
            }

            return response.getBody().integrationCode;


    	} catch (final RestClientException | NullPointerException ex) {
    	    LOG.error(ex);
    	    final CRMException exception = new CRMException("mensagem.usuario.erro.autenticacao.servico", responsavel);
    	    exception.setCrmError(CRMErrorCodeEnum.GENERIC_ERROR);
    	    throw exception;
		} catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
            throw new CRMException("mensagem.erro.consignante.nao.encontrado", responsavel, ex);
		} catch (final Exception ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
            final CRMException exception = new CRMException("mensagem.erro.certificado.invalido", responsavel);
            exception.setCrmError(CRMErrorCodeEnum.GENERIC_ERROR);
            throw exception;
		}
    }

   

    /**
     * Constroi o RestTemplate a ser usado pelo CRMClient.
     * @param custom
     * @param responsavel
     * @return o RestTemmplate conforme configuração do sisema
     * @throws CRMException
     */
    private RestTemplate buildRestTemplate(boolean custom, AcessoSistema responsavel) throws CRMException {
        RestTemplate restTemplate;
        if (custom) {
            try {
                final KeyStore clientStore = KeyStore.getInstance(properties.getSslKeyStoreType());
                final File file = ResourceUtils.getFile(properties.getSslKeyStore());
                try (InputStream fileInputStream = new FileInputStream(file)) {
                    clientStore.load(fileInputStream, properties.getSslKeyStorePassword().toCharArray());

                    final SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                                                                            .setProtocol(properties.getSslKeyStoreProtocol())
                                                                            .loadKeyMaterial(clientStore, properties.getSslKeyStorePassword().toCharArray())
                                                                            .loadTrustMaterial(new TrustSelfSignedStrategy());

                    final SSLContext sslcontext = sslContextBuilder.build();

                    final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                                                                                                         .setSslContext(sslcontext)
                                                                                                         .build();

                    final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                                                                                                    .setSSLSocketFactory(sslSocketFactory)
                                                                                                    .build();

                    final CloseableHttpClient httpClient = HttpClients.custom()
                                                                      .setConnectionManager(cm)
                                                                      .evictExpiredConnections()
                                                                      .build();

                    final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

                    return new RestTemplate(requestFactory);
                } finally {
                    // ok
                }
            } catch (final Exception e) {
                LOG.error(e);
                final CRMException exception = new CRMException("mensagem.erro.certificado.invalido", responsavel);
                exception.setCrmError(CRMErrorCodeEnum.GENERIC_ERROR);
                throw exception;
            }

        } else {
            restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
            restTemplate.setErrorHandler(new EconsigResponseErrorHandler(AcessoSistema.getAcessoUsuarioSistema()));
        }

        return restTemplate;
    }
}
