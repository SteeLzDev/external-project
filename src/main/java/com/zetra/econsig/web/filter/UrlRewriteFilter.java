package com.zetra.econsig.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: UrlRewriteFilter</p>
 * <p>Description: Filtro de requisição para fazer reescrita de URL.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class UrlRewriteFilter implements Filter {

    private static final Map<String, String> REWRITE_MAP = new HashMap<>();

    static {
        REWRITE_MAP.put("/servidor",        "/v3/autenticar");
        REWRITE_MAP.put("/servidorpublico", "/v3/autenticar");
        REWRITE_MAP.put("/empregado",       "/v3/autenticar");
        REWRITE_MAP.put("/funcionario",     "/v3/autenticar");
        REWRITE_MAP.put("/colaborador",     "/v3/autenticar");
        REWRITE_MAP.put("/militar",         "/v3/autenticar");
        REWRITE_MAP.put("/empleado",        "/v3/autenticar");
        REWRITE_MAP.put("/employee",        "/v3/autenticar");

        REWRITE_MAP.put("/login/login_servidor.jsp", "/v3/autenticar");
        REWRITE_MAP.put("/login/login.jsp",          "/v3/autenticarUsuario");
        REWRITE_MAP.put("/index.jsp",                "/");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestURI = request.getRequestURI();
        if (requestURI.indexOf("//") >= 0) {
            // DESENV-17497 : força que o browser não tente usar o cache para as URLs de login
            response.addHeader("Cache-Control", "no-store");
            // DESENV-19977 : substitui a dupla barra por barra simples evitando erro do Spring Security
            response.sendRedirect(requestURI.replaceAll("//", "/"));
            return;
        }

        String context = request.getContextPath();
        String uri = requestURI.substring(requestURI.indexOf(context) + context.length());

        if (uri.endsWith("/")) {
            // Remove a última barra
            uri = uri.substring(0, uri.lastIndexOf('/'));
        } else if (!uri.endsWith(".jsp")) {
            // Se não termina em barra e está no mapa de reescrita,
            // redireciona primeiro para o recurso com barra
            if (REWRITE_MAP.containsKey(uri)) {
                // DESENV-17497 : força que o browser não tente usar o cache para as URLs de login
                response.addHeader("Cache-Control", "no-store");
                // DESENV-17458 : adiciona um fake id para garantir que o navegador do usuário não usará o que está em cache.
                response.sendRedirect(request.getRequestURI() + "/?__CID__=" + DateHelper.getSystemDatetime().getTime());
                return;
            }
        }

        if (REWRITE_MAP.containsKey(uri)) {
            // DESENV-17497 : força que o browser não tente usar o cache para as URLs de login
            response.addHeader("Cache-Control", "no-store");
            req.getRequestDispatcher(REWRITE_MAP.get(uri)).forward(req, res);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
        //
    }
}
