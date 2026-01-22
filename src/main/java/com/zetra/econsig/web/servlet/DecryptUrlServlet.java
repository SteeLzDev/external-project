package com.zetra.econsig.web.servlet;

import static com.zetra.econsig.web.filter.UrlCryptFilter.AES_INIT_VECTOR_SESSION_ATTRIBUTE;
import static com.zetra.econsig.web.filter.UrlCryptFilter.AES_KEY_SESSION_ATTRIBUTE;

import java.io.IOException;
import java.util.Base64;

import org.apache.commons.lang3.StringEscapeUtils;

import com.zetra.econsig.helper.criptografia.AES;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: DecryptUrlServlet</p>
 * <p>Description: Servlet para descriptografar URLs.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DecryptUrlServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGetPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGetPost(request, response);
    }

    private void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String uri = new String(Base64.getDecoder().decode(JspHelper.verificaVarQryStr(request, "xyz")));

        final HttpSession session = request.getSession();
        final byte[] key = (byte[]) session.getAttribute(AES_KEY_SESSION_ATTRIBUTE);
        final byte[] iv = (byte[]) session.getAttribute(AES_INIT_VECTOR_SESSION_ATTRIBUTE);

        if (key != null && iv != null) {
            String plainUrl = StringEscapeUtils.unescapeHtml4(AES.decryptText(key, iv, uri));

            if (!TextHelper.isNull(plainUrl)) {
            	while (plainUrl.charAt(0) == '.') {
            		plainUrl = plainUrl.substring(1);
            	}

            	if (plainUrl != null && plainUrl.indexOf("/v3/carregarPrincipal") >= 0) {
            		// No v4 não há frames, então eventualmente pode recriar a chave. Neste caso, a chave será recriada
            		// sempre que o usuário voltar à página principal do sistema. Não foi possível trocar a cada request
            		// porque há alguns pop-ups que consomem a chave, e deixam os links inválido
            		session.removeAttribute(AES_KEY_SESSION_ATTRIBUTE);
            		session.removeAttribute(AES_INIT_VECTOR_SESSION_ATTRIBUTE);
            	}

            	request.getRequestDispatcher(plainUrl).forward(request, response);
            	return;
            }
        }

        request.getRequestDispatcher("/v3/expirarSistema?acao=iniciar").forward(request, response);
    }
}
