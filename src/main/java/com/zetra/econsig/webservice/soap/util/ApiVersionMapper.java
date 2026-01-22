package com.zetra.econsig.webservice.soap.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

/**
 * <p>Title: ApiVersionMapper</p>
 * <p>Description: Classe auxiliar para os mapear os métodos com as versões da API.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ApiVersionMapper implements ApplicationContextAware, InitializingBean {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ApiVersionMapper.class);

    private ApplicationContext applicationContext;

    private final Map<String, Map<String, String>> services = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Endpoint.class);

        final Map<String, Map<String, Set<String>>> debugServices = new HashMap<>();
        for (final Object myFoo : beans.values()) {
            final Class<? extends Object> fooClass = myFoo.getClass();
            final Endpoint annotation = fooClass.getAnnotation(Endpoint.class);
            if (annotation != null) {
                for (final Method method : fooClass.getMethods()) {
                    final PayloadRoot methodAnnotation = method.getAnnotation(PayloadRoot.class);
                    if (methodAnnotation != null) {
                        final VersionInfo info = new VersionInfo(methodAnnotation.namespace());
                        final Map<String, String> methods = services.computeIfAbsent(info.getService(), k -> new HashMap<>());
                        final String key = buildKey(methodAnnotation.namespace(), methodAnnotation.localPart());
                        methods.put(key, methodAnnotation.namespace());
                        final String[] keyParts = key.split("/");
                        final Map<String, Set<String>> debugMethods = debugServices.computeIfAbsent(info.getService(), k -> new TreeMap<>());
                        final Set<String> versions = debugMethods.computeIfAbsent(keyParts[1], k -> new HashSet<>());
                        versions.add(keyParts[0]);
                    }
                }

            }
        }
        final StringBuilder found = new StringBuilder();
        LOG.info("SOAP ENDPOINTS FOUND:");
        for (final Map.Entry<String, Map<String, Set<String>>> entry1 : debugServices.entrySet()) {
            found.append(" - " + entry1.getKey() + ": {");
            for (final Map.Entry<String, Set<String>> entry2 : entry1.getValue().entrySet()) {
                found.append(" ").append(entry2.getKey()).append(": ").append(entry2.getValue()).append(",");
            }
            if (found.charAt(found.length() - 1 ) == ',') {
                found.deleteCharAt(found.length() - 1);
            }
            found.append(" }");
            LOG.info(found);
            found.setLength(0);
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
    }

    private String buildKey(String namespace, String localPart) {
        final VersionInfo info = new VersionInfo(namespace);
        return info.getVersion() + "/" + localPart;
    }

    /**
     * Retorna o namespace que possui a implementação do method informado, considerando a versão informada e as anterioes.
     * @param namespace Namespace base onde o método será executado
     * @param method    Método a ser mapeado
     * @return          O namespace que poussui o método implementado para a versão informada. Ou null se não encontrar nenhuma versão.
     */
    public String getNamespace(String namespace, String method) {
        final VersionInfo info = new VersionInfo(namespace);
        final String key = buildKey(namespace, method);
        final Map<String, String> methods = services.get(info.getService());
        String mapped = null;
        if (methods != null) {
            mapped = methods.get(key);
            if (mapped == null) {
                final String suffix = key.replaceFirst(String.valueOf(info.getMajor()), "");
                for (int i = info.getMajor(); i > 0; --i) {
                    mapped = methods.get(i + suffix);
                    if (mapped != null) {
                        break;
                    }
                }
            }
        }
        return mapped;
    }
}
