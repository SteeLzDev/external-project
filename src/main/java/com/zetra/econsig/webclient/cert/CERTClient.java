package com.zetra.econsig.webclient.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.util.Base64;
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

import com.zetra.econsig.exception.CERTException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.CertificadoDigital;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Title: CERTClient</p>
 * <p>Description: Implementação para consumir microserviço CERT.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Alexandre Fernandes
 */
@Controller
public class CERTClient {

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CERTClient.class);

    @Autowired
    private CERTConfigProperties properties;

    /**
     *
     * @param request
     * @param responsavel
     * @return
     * @throws CERTException
     */
    @SuppressWarnings({ "java:S2259" })
    public Boolean validateToken(String token, AcessoSistema responsavel) throws CERTException {
    	try {
			final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_CERT, responsavel);

			if (TextHelper.isNull(urlBase)) {
			    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.cert", responsavel));
			    final CERTException exception = new CERTException("mensagem.usuarioSenhaInvalidos", responsavel);
			    exception.setCertError(CERTErrorCodeEnum.GENERIC_ERROR);
			    throw exception;
			}

            // Logar usuário admin para inclusão de novo usuário
			final RestTemplate restTemplate = buildRestTemplate(responsavel);

	        final HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

            final HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            final ResponseEntity<CertTokenPojo> response = restTemplate.exchange(urlBase + "/cert/response?token=" + token, HttpMethod.GET, httpEntity, CertTokenPojo.class);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
	            throw new CERTException("mensagem.erro.certificado.digital.obrigatorio", responsavel);
            }
            
            return CertificadoDigital.getInstance().validarCertificado(response.getBody().getData(), responsavel);

    	} catch (final RestClientException | NullPointerException ex) {
    	    LOG.error(ex);
    	    final CERTException exception = new CERTException("mensagem.usuario.erro.autenticacao.servico", responsavel);
    	    exception.setCertError(CERTErrorCodeEnum.GENERIC_ERROR);
    	    throw exception;
		} catch (final Exception ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
            final CERTException exception = new CERTException("mensagem.erro.certificado.invalido", responsavel);
            exception.setCertError(CERTErrorCodeEnum.GENERIC_ERROR);
            throw exception;
		}
    }

    /**
     * Constroi o RestTemplate a ser usado pelo CERTClient.
     * @param custom
     * @param responsavel
     * @return o RestTemmplate conforme configuração do sisema
     * @throws CERTException
     */
    private RestTemplate buildRestTemplate(AcessoSistema responsavel) throws CERTException {
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
            final CERTException exception = new CERTException("mensagem.erro.certificado.invalido", responsavel);
            exception.setCertError(CERTErrorCodeEnum.GENERIC_ERROR);
            throw exception;
        }
    }

    public static String prepareState(HttpServletRequest request, AcessoSistema responsavel) {
        final KeyPair kp = RSA.generateKeyPair(CodedValues.RSA_KEY_SIZE);

        request.getSession().removeAttribute(CodedValues.CERT_PUBLIC_KEY);
        request.getSession().setAttribute(CodedValues.CERT_PUBLIC_KEY, kp);

        final HashMap<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put(CodedValues.CERT_USU_CODIGO, responsavel.getUsuCodigo());
        additionalDetails.put(CodedValues.CERT_DATA, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy HH:mm:ss"));
        additionalDetails.put(CodedValues.CERT_IP_ACESSO, responsavel.getIpUsuario());

        final JSONObject jsonObject = new JSONObject(additionalDetails);
        String encrypted = Base64.getEncoder().encodeToString(RSA.encrypt(jsonObject.toJSONString(), kp.getPublic()).getBytes());

        return encrypted;
    }
}
