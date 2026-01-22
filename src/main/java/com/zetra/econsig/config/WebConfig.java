package com.zetra.econsig.config;

import org.apache.commons.fileupload2.jakarta.JakartaFileCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zetra.econsig.web.CustomMessageResolver;
import com.zetra.econsig.web.filter.RecuperaMargemServidorFilter;
import com.zetra.econsig.web.filter.RestricaoAcessoFilter;
import com.zetra.econsig.web.filter.SecurityFilter;
import com.zetra.econsig.web.filter.SessionFilter;
import com.zetra.econsig.web.filter.SistemaBloqueadoCheckFilter;
import com.zetra.econsig.web.filter.SoapNamespaceReplaceFilter;
import com.zetra.econsig.web.filter.StaticResourcesFilter;
import com.zetra.econsig.web.filter.UrlCryptFilter;
import com.zetra.econsig.web.filter.UrlRewriteFilter;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.DecryptUrlServlet;
import com.zetra.econsig.web.servlet.FileUploadServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.web.servlet.QRCodeServlet;
import com.zetra.econsig.web.servlet.ViewImageServlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContextListener;


@Configuration
@EnableAutoConfiguration
@ComponentScan({ "com.zetra.econsig" })
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RecuperaMargemServidorFilter recuperaMargemServidorFilter;

    @Autowired
    private RestricaoAcessoFilter restricaoAcessoFilter;

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private SessionFilter sessionFilter;

    @Autowired
    private SistemaBloqueadoCheckFilter sistemaBloqueadoCheckFilter;

    @Autowired
    private UrlCryptFilter urlCryptFilter;

    @Autowired
    private UrlRewriteFilter urlRewriteFilter;

    @Autowired
    private XSSPreventionFilter xssPreventionFilter;

    @Autowired
    private StaticResourcesFilter staticResourcesFilter;

    @Bean
    FilterRegistrationBean<UrlCryptFilter> registerUrlCryptFilter() {
        final FilterRegistrationBean<UrlCryptFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(urlCryptFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*", "/url/get");
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setName("UrlCryptFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    FilterRegistrationBean<XSSPreventionFilter> registerXSSPreventionFilter() {
        final FilterRegistrationBean<XSSPreventionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(xssPreventionFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("XSSPreventionFilter");
        registration.setOrder(2);
        return registration;
    }
/*
    @Bean
    FilterRegistrationBean<CodificarISO88591Filter> registerCodificarISO88591Filter() {
        FilterRegistrationBean<CodificarISO88591Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(codificarISO88591Filter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("CodificarISO88591Filter");
        registration.setOrder(3);
        return registration;
    }
*/
    @Bean
    FilterRegistrationBean<UrlRewriteFilter> registerUrlRewriteFilter() {
        final FilterRegistrationBean<UrlRewriteFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(urlRewriteFilter);
        registration.addUrlPatterns("/*");
        registration.setName("UrlRewriteFilter");
        // Invoke UrlRewriteFilter before the security filter chain
        registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
        return registration;
    }

    @Bean
    FilterRegistrationBean<SessionFilter> registerSessionFilter() {
        final FilterRegistrationBean<SessionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(sessionFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("SessionFilter");
        registration.setOrder(5);
        return registration;
    }

    @Bean
    FilterRegistrationBean<SistemaBloqueadoCheckFilter> registerSistemaBloqueadoCheckFilter() {
        final FilterRegistrationBean<SistemaBloqueadoCheckFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(sistemaBloqueadoCheckFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("SistemaBloqueadoCheckFilter");
        registration.setOrder(6);
        return registration;
    }

    @Bean
    FilterRegistrationBean<SecurityFilter> registerSecurityFilter() {
        final FilterRegistrationBean<SecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(securityFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("SecurityFilter");
        registration.setOrder(7);
        return registration;
    }

    @Bean
    FilterRegistrationBean<RestricaoAcessoFilter> registerRestricaoAcessoFilter() {
        final FilterRegistrationBean<RestricaoAcessoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(restricaoAcessoFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("RestricaoAcessoFilter");
        registration.setOrder(8);
        return registration;
    }

    @Bean
    FilterRegistrationBean<SoapNamespaceReplaceFilter> registerSoapNamespaceReplaceFilter(@Autowired SoapNamespaceReplaceFilter soapNamespaceReplaceFilter) {
        final FilterRegistrationBean<SoapNamespaceReplaceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(soapNamespaceReplaceFilter);
        registration.addUrlPatterns("/services/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("SoapNamespaceReplaceFilter");
        registration.setOrder(9);
        return registration;
    }

    @Bean
    FilterRegistrationBean<RecuperaMargemServidorFilter> registerRecuperaMargemServidorFilter() {
        final FilterRegistrationBean<RecuperaMargemServidorFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(recuperaMargemServidorFilter);
        registration.addUrlPatterns("*.jsp", "/v3/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setName("RecuperaMargemServidorFilter");
        registration.setOrder(10);
        return registration;
    }

    @Bean
    FilterRegistrationBean<StaticResourcesFilter> registerStaticResourcesFilter() {
        final FilterRegistrationBean<StaticResourcesFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(staticResourcesFilter);
        registration.addUrlPatterns("*.gif", "*.jpg", "*.png", "*.svg", "*.css");
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setName("StaticResourcesFilter");
        registration.setOrder(11);
        return registration;
    }


    @Bean
    ServletRegistrationBean<ViewImageServlet> registerViewImageServlet() {
        final ServletRegistrationBean<ViewImageServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new ViewImageServlet());
        registration.addUrlMappings("/img/view.jsp");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    ServletRegistrationBean<ImageCaptchaServlet> registerKaptchaServlet() {
        final ServletRegistrationBean<ImageCaptchaServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new ImageCaptchaServlet());
        registration.addUrlMappings("/captcha.jpg");
        registration.setLoadOnStartup(2);
        return registration;
    }

    @Bean
    ServletRegistrationBean<QRCodeServlet> registerQRCodeServlet() {
        final ServletRegistrationBean<QRCodeServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new QRCodeServlet());
        registration.addUrlMappings("/img/qrcode.jsp");
        registration.setLoadOnStartup(3);
        return registration;
    }

    @Bean
    ServletRegistrationBean<FileUploadServlet> registerFileUploadServlet() {
        final ServletRegistrationBean<FileUploadServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new FileUploadServlet());
        registration.addUrlMappings("/arquivos/upload_anexo.jsp");
        registration.setLoadOnStartup(4);
        return registration;
    }

    @Bean
    ServletRegistrationBean<AudioCaptchaServlet> registerZetraSimpleCaptcha() {
        final ServletRegistrationBean<AudioCaptchaServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new AudioCaptchaServlet());
        registration.addUrlMappings("/audio.wav");
        registration.setLoadOnStartup(4);
        return registration;
    }

    @Bean
    ServletRegistrationBean<DecryptUrlServlet> registerDecryptUrlServlet() {
        final ServletRegistrationBean<DecryptUrlServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new DecryptUrlServlet());
        registration.addUrlMappings("/url/get");
        registration.setLoadOnStartup(6);
        return registration;
    }

    @Bean
    ServletListenerRegistrationBean<ServletContextListener> registerFileCleanerListener() {
        final ServletListenerRegistrationBean<ServletContextListener> registration = new ServletListenerRegistrationBean<>();
        registration.setListener(new JakartaFileCleaner());
        return registration;
    }

    @Bean
    MessageSource messageSource() {
        return new CustomMessageResolver();
    }
//
//    @Bean
//    UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer() {
//        return new UndertowDeploymentInfoCustomizer() {
//
//            /**
//             * Customizações necessárias para execução do Undertow em modo WAR.
//             */
//            @Override
//            public void customize(DeploymentInfo deploymentInfo) {
//                try {
//                    ServletInfo servlet = JspServletBuilder.createServlet("Default Jsp Servlet", "*.jsp");
//
//                    // Copia os parâmetros do application.properties para a definição do servlet
//                    MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
//                    StreamSupport.stream(propSrcs.spliterator(), false)
//                            .filter(ps -> ps instanceof EnumerablePropertySource)
//                            .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
//                            .flatMap(Arrays::<String>stream)
//                            .filter(n -> n.startsWith(WEB_INIT_PARAM_PREFIX))
//                            .map(n -> n.substring(WEB_INIT_PARAM_PREFIX.length()))
//                            .forEach(propName -> servlet.addInitParam(propName, env.getProperty(WEB_INIT_PARAM_PREFIX + propName)));
//
//                    deploymentInfo.addServlet(servlet);
//
//                    HashMap<String, TagLibraryInfo> tagLibraryInfo = null;
//                    File realPath = deploymentInfo.getResourceManager().getResource("/").getFile();
//                    if (realPath != null) {
//                        tagLibraryInfo = TldLocator.createTldInfosFromFile(realPath);
//                    } else {
//                        deploymentInfo.setResourceManager(new CustomClassPathResourceManager(WebConfig.class));
//
//                        URL url = deploymentInfo.getResourceManager().getResource("/").getUrl();
//                        tagLibraryInfo = TldLocator.createTldInfosFromURL(url);
//                    }
//                    JspServletBuilder.setupDeployment(deploymentInfo, new HashMap<>(), tagLibraryInfo, new HackInstanceManager());
//                } catch (IOException ex) {
//                    LOG.error(ex.getMessage(), ex);
//                }
//            }
//
//        };
//    }
}
