package com.zetra.econsig.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.util.EconsigResponseErrorHandler;

/**
 * <p> Title: RestTemplateConfig</p>
 * <p> Description: Template para chamadas Rest.</p>
 * <p> Copyright: Copyright (c) 2011-2023</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * @author Leonel Martins
 */
@Lazy
@Configuration
public class RestTemplateConfig {

	@Value("${http.proxy.enabled}")
    private String httpProxyEnabled;

	@Value("${http.proxy.host}")
    private String httpProxyHost;

	@Value("${http.proxy.port}")
    private int httpProxyPort;

	@Value("${http.proxy.user}")
    private byte [] httpProxyUser;

	@Value("${http.proxy.password}")
    private byte [] httpProxyPassword;

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RestTemplateConfig.class);

    @Bean("getRestTemplateOAuthAutentication")
	RestTemplate getRestTemplateOAuthAutentication() throws ZetraException {
    	if (TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, AcessoSistema.getAcessoUsuarioSistema()))) {
    		return null;
    	}

    	final String clientId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_CLIENT_ID, AcessoSistema.getAcessoUsuarioSistema());
        final String clientSecret = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_CLIENT_SECRET, AcessoSistema.getAcessoUsuarioSistema());

        if (TextHelper.isNull(clientId) || TextHelper.isNull(clientSecret)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", AcessoSistema.getAcessoUsuarioSistema()));
            throw new ZetraException("mensagem.usuarioSenhaInvalidos", AcessoSistema.getAcessoUsuarioSistema());
        }

        // A RestTemplate se nao for configurada usara a versao 1.0 do TLS e essa versao nao eh suportada pelo Tomcat do SSO
        // Por isso vamos explicitamente definir para que ela use a versao 1.2
        SSLContext context = null;
		try {
			context = SSLContext.getInstance("TLSv1.2");
			context.init(null, null, null);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", AcessoSistema.getAcessoUsuarioSistema()));
            throw new ZetraException("mensagem.usuarioSenhaInvalidos", AcessoSistema.getAcessoUsuarioSistema());
		}


		final HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
		        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create().setSslContext(context).build())
		        .build();

		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setConnectionManager(connectionManager);

		if (!TextHelper.isNull(httpProxyEnabled) && httpProxyEnabled.trim().equalsIgnoreCase("true")) {
			httpClientBuilder.setRoutePlanner(proxyRoutePlanner()).setDefaultCredentialsProvider(proxyCredentialsProvider());
		}

        final CloseableHttpClient httpClient = httpClientBuilder.build();

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        final RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.setErrorHandler(new EconsigResponseErrorHandler(AcessoSistema.getAcessoUsuarioSistema()));
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));

        return restTemplate;
    }

    @Bean("simpleRestemplate")
    RestTemplate getRestTemplate () {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setErrorHandler(new EconsigResponseErrorHandler(AcessoSistema.getAcessoUsuarioSistema()));
        return restTemplate;
    }

    @Bean
    HttpRoutePlanner proxyRoutePlanner() {
    	if (TextHelper.isNull(httpProxyHost) || TextHelper.isNull(httpProxyPort)) {
    		LOG.warn("Http Proxy habilitado no eConsig, porém não há configuração de proxy host e/ou port definido nas propriedades do sistema.");
    		return null;
    	}

    	final HttpHost proxy = new HttpHost(httpProxyHost.trim(), httpProxyPort);
    	return new DefaultProxyRoutePlanner(proxy) {
            @Override
            public HttpHost determineProxy(HttpHost target, HttpContext context) throws HttpException {
                return super.determineProxy(target, context);
            }
        };
    }

    @Bean
    CredentialsProvider proxyCredentialsProvider() {
    	if (TextHelper.isNull(httpProxyHost) || TextHelper.isNull(httpProxyPort)) {
    		LOG.warn("Http Proxy habilitado no eConsig, porém não há configuração de proxy host e/ou port definido nas propriedades do sistema.");
    		return null;
    	}

    	if (TextHelper.isNull(httpProxyUser) || TextHelper.isNull(httpProxyPassword)) {
    		LOG.info("Http Proxy configurado sem usuário e senha.");
    		return null;
    	}

    	final CredentialsStore credentialsProvider = new BasicCredentialsProvider();
    	credentialsProvider.setCredentials(
    	    new AuthScope(httpProxyHost.trim(), httpProxyPort),
    	    // mantendo a declaração com construtor new String de modo às credenciais não ficarem
    	    // no string pool da JVM
    	    new UsernamePasswordCredentials(new String(httpProxyUser), new String(httpProxyPassword).toCharArray()));

    	return credentialsProvider;
    }

    @Bean
    TaskScheduler taskScheduler() {
    	final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    	scheduler.setThreadNamePrefix("poolScheduler");
    	scheduler.setPoolSize(50);
    	return scheduler;
    }

}
