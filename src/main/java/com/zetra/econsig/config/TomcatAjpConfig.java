package com.zetra.econsig.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Title: TomcatAjpConfig</p>
 * <p>Description: Configuração do conector AJP.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 */
@Configuration
@ConditionalOnProperty("tomcat.ajp.enabled")
@ConfigurationProperties(prefix = "ajp")
public class TomcatAjpConfig {

    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.remoteauthentication}")
    String remoteAuthentication;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

    @Value("${tomcat.ajp.max.threads}")
    int maxThreads;

    @Value("${tomcat.ajp.connection.timeout}")
    int connectionTimeout;

    @Value("${tomcat.ajp.packet.size:65536}")
    int packetSize;

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory && tomcatAjpEnabled) {
                server.addAdditionalTomcatConnectors(redirectConnector());
            }
        };
    }

    private Connector redirectConnector() {
        final Connector ajpConnector = new Connector("AJP/1.3");
        ajpConnector.setScheme("http");
        ajpConnector.setPort(ajpPort);
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);

        final AjpNioProtocol protocol = (AjpNioProtocol) ajpConnector.getProtocolHandler();
        protocol.setSecretRequired(false);
        protocol.setMaxThreads(maxThreads);
        protocol.setConnectionTimeout(connectionTimeout);
        protocol.setPacketSize(packetSize);
        try {
            protocol.setAddress(InetAddress.getByName("0.0.0.0"));
        } catch (final UnknownHostException ex) {
            ex.printStackTrace();
        }

        return ajpConnector;
    }
}
