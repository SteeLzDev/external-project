package com.zetra.econsig.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheControlConfig implements WebMvcConfigurer {
	
	public void addResourceHandlers(ResourceHandlerRegistry registry, @Autowired Environment env) {

		// Método que seta no cache por 30 dias para os arquivos do sistema. As páginas "JSP" ficaram de fora da lista
		// para evitarmos a nevegação em cache, após o logout do sistema. (DESENV-20436)
		// Configuração duplicada para resolver path para ambiente WAR e ambiente docker (DESENV-20762).
		registry.addResourceHandler("/img/**", "css/**", "/js/**", "/bootstrap-5.2.3-dist/**",
				"/wNumb 1.2.0/**", "/datatables/**", "/noUiSlider-dist/**", "/viewer/**" )
		.addResourceLocations(
				"classpath:/img/",
				"classpath:/css/",
				"classpath:/js/",
				"classpath:/bootstrap-5.2.3-dist/",
				"classpath:/wNumb 1.2.0/",
				"classpath:/datatables/",
				"classpath:/noUiSlider-dist/",
				"classpath:/viewer/",
				"file:./src/main/webapp/img/",
				"file:./src/main/webapp/css/",
				"file:./src/main/webapp/js/",
				"file:./src/main/webapp/bootstrap-5.2.3-dist/",
				"file:./src/main/webapp/wNumb 1.2.0/",
				"file:./src/main/webapp/datatables/",
				"file:./src/main/webapp/noUiSlider-dist/",
				"file:./src/main/webapp/viewer/"
				)
		.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePrivate());

	}

}
