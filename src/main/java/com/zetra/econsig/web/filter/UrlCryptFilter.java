package com.zetra.econsig.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import com.zetra.econsig.helper.criptografia.AES;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: UrlCryptFilter</p>
 * <p>Description: Filtro de resposta para criptografar URLs.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class UrlCryptFilter extends EConsigFilter {

    private static final List<String> pgNaoCriptografadas = new ArrayList<>();

	public static final String AES_KEY_SESSION_ATTRIBUTE = "UrlCryptFilter.AES_KEY";
	public static final String AES_INIT_VECTOR_SESSION_ATTRIBUTE = "UrlCryptFilter.AES_INIT_VECTOR";

	static {
		pgNaoCriptografadas.add("/v3/downloadArquivo");
		pgNaoCriptografadas.add("/img/view.jsp");
		pgNaoCriptografadas.add("/js/mensagens.jsp");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		final boolean criptografiaUrl = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CRIPTOGRAFIA_URL,
				CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

		if (servletRequest instanceof final HttpServletRequest request) {
			final String recurso = getRecurso(request);
			final String contextPath = request.getContextPath();
			if (criptografiaUrl && !pgNaoCriptografadas.contains(recurso)) {
				final ByteArrayResponseWrapper wrappedResponse = new ByteArrayResponseWrapper((HttpServletResponse) servletResponse);

				chain.doFilter(servletRequest, wrappedResponse);

				// Senão, faz o parse do resultado para criptografar URLs
				final byte[] bytes = wrappedResponse.getByteArray();
				final String out = new String(bytes);

				final Pattern p = Pattern.compile("([\"\'])(\\.\\.|[\\\\]{0,}" + contextPath + ")([\\\\]{0,}\\/v3[\\\\]{0,}\\/[^\"\'\\?]+)([\"\'\\?])");
				final Matcher m = p.matcher(out);

				byte[] key = null;
				byte[] iv = null;

				// Cria nova chave de criptografia
				final HttpSession session = (request).getSession();
				synchronized (session) {
					key = (byte[]) session.getAttribute(AES_KEY_SESSION_ATTRIBUTE);
					iv = (byte[]) session.getAttribute(AES_INIT_VECTOR_SESSION_ATTRIBUTE);

					if (key == null) {
						key = AES.generateNewAESKey();
						iv = AES.generateInitVector();

						session.setAttribute(AES_KEY_SESSION_ATTRIBUTE, key);
						session.setAttribute(AES_INIT_VECTOR_SESSION_ATTRIBUTE, iv);
					}
				}

				// Procura pelos padrões e criptografa as URLs
				final StringBuilder newContent = new StringBuilder();
				int begin = 0;
                while (m.find()) {
                    newContent.append(out.substring(begin, m.start()));
                    newContent.append(m.group(1));
                    final String url = StringEscapeUtils.unescapeEcmaScript(m.group(3));
                    if (url.indexOf("/v3/verificarOperacao") != -1 || url.indexOf("/v3/autorizarOperacao") != -1) {
                        newContent.append(contextPath).append(url).append(m.group(4));
                    } else {
                        newContent.append(contextPath)
                                  .append("/url/get?xyz=")
                                  .append(Base64.getEncoder().encodeToString(AES.encryptText(key, iv, url).getBytes()))
                                  .append(m.group(4).replace('?', '&'));
                    }
                    begin = m.end();
                }
				newContent.append(out.substring(begin));

				servletResponse.getWriter().write(newContent.toString());
			} else {
				chain.doFilter(servletRequest, servletResponse);
			}
		} else {
			chain.doFilter(servletRequest, servletResponse);
		}
	}
}
