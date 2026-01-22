package com.zetra.econsig.webservice.rest.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Priority;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * <p>Title: XssRestFilter</p>
 * <p>Description: Filtro de requisição utilizado para prevenir ataques
 * de Cross Site Scripting (XSS) para chamadas REST através da inclusão de comandos
 * HTML em campos texto.</p>
 * <p>Copyright: Copyright (c) 2002-2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class XssRestFilter implements ContainerRequestFilter {

    private static final String PARAMETRO_SENHA = "senha";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(requestContext.getEntityStream()));
        String primeiraLinha = null;
        final StringBuilder corpo = new StringBuilder();

        do {
            primeiraLinha = in.readLine();
            if (!TextHelper.isNull(primeiraLinha)) {
                if (primeiraLinha.contains(PARAMETRO_SENHA)) {
                    try {
                        final JSONParser parser = new JSONParser();

                        if (parser.parse(primeiraLinha) instanceof JSONObject) {
                            final JSONObject jsonOriginal = (JSONObject) parser.parse(primeiraLinha);

                             corpo.append(extracted(primeiraLinha, parser, jsonOriginal));

                        } else if (parser.parse(primeiraLinha) instanceof JSONArray) {
                        	final JSONArray jsonArrayOriginal = (JSONArray) parser.parse(primeiraLinha);

                			final JSONArray jsonArrayTratado = new JSONArray();
                        	for (final Object json : jsonArrayOriginal) {
                                final JSONObject jsonOriginal = (JSONObject) parser.parse(json.toString());

                                jsonArrayTratado.add(parser.parse(extracted(json.toString(), parser, jsonOriginal)));
							}

                        	corpo.append(jsonArrayTratado.toJSONString());
						}

                    } catch (final ParseException e) {
                        // Caso não dê certo o parse para Json, tratamos o campo como se não fosse um json, pois o objetivo é tratar somente o campo senha
                        corpo.append(XSSPreventionFilter.stripXSS(primeiraLinha));
                    }
                } else {
                    corpo.append(XSSPreventionFilter.stripXSS(primeiraLinha));
                }
            }
        } while (primeiraLinha != null);

        final InputStream inSanitizado = new ByteArrayInputStream(corpo.toString().getBytes());

        requestContext.setEntityStream(inSanitizado);
    }

	private String extracted(String primeiraLinha, JSONParser parser, JSONObject jsonOriginal) throws ParseException {
		final String senhaOriginal = (String) jsonOriginal.get(PARAMETRO_SENHA);
		if (senhaOriginal != null && !senhaOriginal.isEmpty()) {
		    final String conteudoTratado = XSSPreventionFilter.stripXSS(primeiraLinha);
		    final JSONObject jsonTratado = (JSONObject) parser.parse(conteudoTratado);
		    jsonTratado.put(PARAMETRO_SENHA, senhaOriginal);
		    return jsonTratado.toString();
		} else {
		    return XSSPreventionFilter.stripXSS(primeiraLinha);
		}
	}
}
