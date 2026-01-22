package com.zetra.econsig.web.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.zetra.econsig.helper.sistema.RecursoSistemaHelper;

/**
 * <p>Title: StaticResourcesFilter</p>
 * <p>Description: Filtro que verifica imagens e arquivos CSS foram customizados
 * para o sistema, através da localização destes em banco de dados.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class StaticResourcesFilter extends EConsigFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            ByteArrayResponseWrapper newResponse = new ByteArrayResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, newResponse);

            byte[] bytes = newResponse.getByteArray();
            if (bytes != null) {
                String uriRecurso = getRecurso(httpRequest);
                byte[] bytesNew = RecursoSistemaHelper.getRecurso(uriRecurso);
                if (bytesNew != null) {
                    bytes = bytesNew;
                }

                response.setContentLength(bytes.length);
                response.getOutputStream().write(bytes);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}