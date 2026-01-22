package com.zetra.econsig.webservice.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;

import com.zetra.econsig.webservice.rest.filter.AuthenticationFilter;
import com.zetra.econsig.webservice.rest.filter.CORSFilter;
import com.zetra.econsig.webservice.rest.filter.ErrorHandlerResponseFilter;
import com.zetra.econsig.webservice.rest.filter.XssRestFilter;

/**
 * <p>Title: RestEasyApplication</p>
 * <p>Description: Classe do RESTEasy para registro de Services e Providers.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
* $Author$
 * $Revision$
 * $Date$
 */
@Component
@ApplicationPath("/rest/")
public class RestEasyApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> classes = new HashSet<>();

    public RestEasyApplication() {
        // Services
        /**
         * Adiciona todas as classes que terminarem com Service no mesmo pacote da classe atual.
         **/
        // create scanner and disable default filters (that is the 'false' argument)
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        // add include filters which matches all the classes that finishes with Service
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Service")));

        String basePackage = this.getClass().getPackage().getName();

        // get matching classes defined in the package
        final Set<BeanDefinition> _classes = provider.findCandidateComponents(basePackage);

        // this is how you can load the class type from BeanDefinition instance
        for (BeanDefinition bean: _classes) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                singletons.add(clazz.getDeclaredConstructor().newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        // Providers
        classes.add(AuthenticationFilter.class);
        classes.add(CORSFilter.class);
        classes.add(XssRestFilter.class);
        classes.add(ErrorHandlerResponseFilter.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}