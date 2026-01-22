package com.zetra.econsig.web.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.zetra.econsig.webservice.soap.util.ApiVersionMapper;
import com.zetra.econsig.webservice.soap.util.VersionInfo;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>Title: SoapNamespaceReplaceFilter</p>
 * <p>Description: Altera o namespace do envelope SOAP para o definido na URL.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Component
public class SoapNamespaceReplaceFilter implements Filter {

    @Autowired
    ApiVersionMapper apiVersionMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI().contains("/services/")) {
            final boolean contemWsdl = httpRequest.getParameterMap().containsKey("wsdl");
            final boolean contemXsd = httpRequest.getRequestURI().contains("/xsd");
            if (contemWsdl || contemXsd) {
                String path = "/wsdl/";
                path += contemWsdl ? httpRequest.getPathInfo() + ".wsdl" : httpRequest.getPathInfo();
                final ClassPathResource cpr = new ClassPathResource(path);
                if (cpr.exists()) {
                    cpr.getInputStream().transferTo(response.getOutputStream());
                } else {
                    ((HttpServletResponse) response).setStatus(HttpStatus.NOT_FOUND.value());
                }
            } else {
                final SoapVersionRequestWrapper wrappedRequest = new SoapVersionRequestWrapper(apiVersionMapper, httpRequest);
                final ByteArrayResponseWrapper wrappedResponse = new ByteArrayResponseWrapper((HttpServletResponse) response);

                chain.doFilter(wrappedRequest, wrappedResponse);
                final byte[] bytes = wrappedResponse.getByteArray();
                final String contentType = wrappedResponse.getContentType();

                if ((contentType != null) && contentType.contains("text/xml")) {
                    final StringBuilder out = new StringBuilder(new String(bytes));
                    final int size = out.length();
                    final String namespace = httpRequest.getPathInfo().replace("/", "");
                    final VersionInfo info = new VersionInfo(namespace);
                    final int index = out.indexOf(info.getService());
                    if (index >= 0) {
                        final int indexV = out.indexOf("\"", index);
                        out.delete(index, indexV);
                        out.insert(index, info.getService());
                    }
                    while (size > out.length()) {
                        out.append(" ");
                    }
                    response.getOutputStream().write(out.toString().getBytes());
                } else {
                    response.getOutputStream().write(bytes);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
