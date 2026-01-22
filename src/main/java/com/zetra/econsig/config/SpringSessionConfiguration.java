package com.zetra.econsig.config;

import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringSessionConfiguration {

	// SameSite é uma propriedade que pode ser definida em cookies HTTP para evitar ataques de
	// CSRF (falsificação de solicitação entre sites) em aplicativos Web:

	// Quando SameSite é definido como Lax, o cookie é enviado em solicitações no mesmo site e em
	// solicitações GET de outros sites. Ele não é enviado em solicitações GET entre domínios.
	// Um valor Strict garante que o cookie seja enviado em solicitações somente dentro do mesmo site.

    // DESENV-21335 : os navegadores estão querendo bloquear o envio de cookies mesmo quando for HTTPS.
    // Neste caso, será necessário rever a configuração abaixo.
	@Bean
	CookieSameSiteSupplier applicationCookieSameSiteSupplier() {
		return CookieSameSiteSupplier.ofStrict();
	}

}