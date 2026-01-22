package com.zetra.econsig.config;

import java.util.List;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import com.zetra.econsig.webservice.soap.endpoint.interceptor.SoapEnvelopeInterceptor;
import com.zetra.econsig.webservice.soap.util.ApiVersionMapper;

/**
 * <p>Title: WebServiceConfig</p>
 * <p>Description: Configuração do Webservice SOAP para o Spring.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    @Bean
    ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/services/*");
    }

    @Bean
    ApiVersionMapper apiVersionMapper() {
        return new ApiVersionMapper();
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
      interceptors.add(new SoapEnvelopeInterceptor());
    }
}