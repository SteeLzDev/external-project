package com.zetra.econsig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.filter.EConsigAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new EConsigAuthenticationEntryPoint()));

        http
            // exigir certificado de cliente para urls sensíveis
            .authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(new AntPathRequestMatcher("/v3/autenticarUsuarioCertificadoDigital"))
                        .authenticated()
                    .anyRequest()
                        .permitAll()
            )
            // configurações para validação do certificado
            .x509(x509 ->
                x509.subjectPrincipalRegex("(.*)")
                    .userDetailsService(userDetailsService()))


            // ignorar o csrf token para todo o sistema
            .csrf(AbstractHttpConfigurer::disable)
            //DESENV-19136: Alteração no header X-Frame-Option para SAMEORIGIN,
            //permitindo o uso de tags <object> para exibição de arquivos pdf no proprio html.
            .headers(headers -> {
                headers.permissionsPolicyHeader(permissionsPolicy -> permissionsPolicy.policy("accelerometer=(), ambient-light-sensor=(), autoplay=(),"
            		+ "battery=(), cross-origin-isolated=(), display-capture=(), document-domain=(),"
            		+ "encrypted-media=(), execution-while-not-rendered=(), execution-while-out-of-viewport=(),"
            		+ "fullscreen=(), geolocation=(), gyroscope=(), keyboard-map=(), magnetometer=(), microphone=(),"
            		+ "midi=(), navigation-override=(), payment=(), picture-in-picture=(), publickey-credentials-get=(),"
            		+ "screen-wake-lock=(), sync-xhr=(), usb=(), web-share=(), xr-spatial-tracking=()"));
                headers.frameOptions(frameOptions -> {
                    frameOptions.disable();
                    frameOptions.sameOrigin();
                });
                headers.addHeaderWriter(new ContentSecurityPolicyHeaderWriter("frame-ancestors 'self'"))
                       .crossOriginEmbedderPolicy(crossOriginEmbedderPolicy -> crossOriginEmbedderPolicy.policy(CrossOriginEmbedderPolicy.UNSAFE_NONE))
                       .crossOriginResourcePolicy(crossOriginResourcePolicy -> crossOriginResourcePolicy.policy(CrossOriginResourcePolicy.SAME_ORIGIN))
                       .crossOriginOpenerPolicy(crossOriginOpenerPolicy -> crossOriginOpenerPolicy.policy(CrossOriginOpenerPolicy.SAME_ORIGIN))
                       .addHeaderWriter(new ReferrerPolicyHeaderWriter(ReferrerPolicy.SAME_ORIGIN))
                       .addHeaderWriter(new XContentTypeOptionsHeaderWriter())
                       .contentSecurityPolicy(contentSecurityPolicy ->
                           contentSecurityPolicy.policyDirectives(JspHelper.getContentSecurityPolicyHeader(null))
                                                .reportOnly());
            });

		return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        // Aceita qualquer certificado, a validação será feita no WebController específico

        return (UserDetailsService) username -> new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}