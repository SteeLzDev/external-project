package com.zetra.econsig.helper.seguranca;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import com.zetra.econsig.config.SysConfig;
import com.zetra.econsig.helper.texto.TextHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SynchronizerToken</p>
 * <p>Description: É uma 'Helper Class' para monitorar e controlar o fluxo
 * de requisições a certos recursos.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SynchronizerToken {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SynchronizerToken.class);
	
    private static final String PROFILE_PENTEST = "pentest";
    private static final String DISABLE_PAGE_TOKEN = "disable.page.token";

    /**
     * The session attributes key under which our transaction token is
     * stored, if it is used.
     */
    public static final String TRANSACTION_TOKEN_KEY = "eConsig.page.token";

    public static String getRequestToken(HttpServletRequest request) {
        return (request.getAttribute(TRANSACTION_TOKEN_KEY) != null ? request.getAttribute(TRANSACTION_TOKEN_KEY).toString() : request.getParameter(TRANSACTION_TOKEN_KEY));
    }

    public static String getSessionToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(TRANSACTION_TOKEN_KEY);
    }

    /**
     * Generate the html code for the synchronizer token.
     *
     * @param request The request we are processing
     * @return The html code for the synchronizer token
     */
    public static String generateHtmlToken(HttpServletRequest request) {
        String saved = getSessionToken(request);
        if (saved == null) {
            return "";
        }

        StringBuilder input = new StringBuilder();
        input.append("<INPUT TYPE=\"hidden\" NAME=\"").append(TRANSACTION_TOKEN_KEY).append("\" VALUE=\"");
        input.append(saved).append("\">");

        return input.toString();
    }

    /**
     * Generate the synchronizer token pair (name=value) code for use in URL.
     *
     * @param request The request we are processing
     * @return The pair (name=valee) for the synchronizer token - XSS : Seguro pois retorna eConsig.page.token=valor onde valor é calculado de forma segura.
     */
    public static String generateToken4URL(HttpServletRequest request) {
        String saved = getSessionToken(request);
        if (saved == null) {
            return "";
        }

        StringBuilder input = new StringBuilder();
        input.append(TRANSACTION_TOKEN_KEY).append("=").append(saved);

        return input.toString();
    }

    /**
     * Repass the synchronizer token pair (name=value), received in request parameter
     * as code for use in URL.
     *
     * @param request
     * @return
     */
    public static String repassToken4URL(HttpServletRequest request) {
        String received = getRequestToken(request);
        if (received == null) {
            received = "";
        }

        StringBuilder input = new StringBuilder();
        input.append(TRANSACTION_TOKEN_KEY).append("=").append(received);

        return input.toString();
    }

    /**
     * Replace the synchronizer token pair (name=value), present int URL
     * with value from session.
     *
     * @param url - XSS : Realizar escape conforme contexto.
     * @param request
     * @return - XSS : Não é seguro, depende do parâmetro url
     */
    public static String updateTokenInURL(String url, HttpServletRequest request) {
        if (url != null) {
            String saved = getSessionToken(request);
            if (saved == null) {
                saved = "";
            }

            String action = "";
            String query  = "";

            int queryIndex = url.indexOf('?');
            if (queryIndex == -1) {
                action = url;
            } else {
                action = url.substring(0, queryIndex);
                query  = url.substring(queryIndex + 1);
            }

            StringBuilder newUrl = new StringBuilder(action + "?");
            if (!query.isEmpty()) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (!param.startsWith(TRANSACTION_TOKEN_KEY)) {
                        newUrl.append(param).append("&");
                    }
                }
            }

            // Append saved token
            newUrl.append(TRANSACTION_TOKEN_KEY).append("=").append(saved);

            return newUrl.toString();
        }
        return null;
    }

    /**
     * Generate a new transaction token, to be used for enforcing a single
     * request for a particular transaction.
     *
     * @param request The request we are processing
     * @return - XSS : Seguro pois calcula valor.
     */

    public static String generateToken() {
        try {
            SecureRandom ranGen = SecureRandom.getInstance("SHA1PRNG","SUN");
            byte[] aesKey = new byte[16];
            ranGen.nextBytes(aesKey);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(aesKey);
            return (toHex(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchProviderException e) {
            return null;
        }
    }

    /**
     * Return <code>true</code> if there is a transaction token stored in
     * the user's current session, and the value submitted as a request
     * parameter with this action matches it.  Returns <code>false</code>
     * under any of the following circumstances:
     * <ul>
     * <li>No session associated with this request</li>
     * <li>No transaction token saved in the session</li>
     * <li>No transaction token included as a request parameter</li>
     * <li>The included transaction token value does not match the
     *     transaction token in the user's session</li>
     * </ul>
     *
     * @param request The servlet request we are processing
     */
    public static boolean isTokenValid(HttpServletRequest request) {
        return (isTokenValid(request, false));
    }

    /**
     * Return <code>true</code> if there is a transaction token stored in
     * the user's current session, and the value submitted as a request
     * parameter with this action matches it.  Returns <code>false</code>
     * <ul>
     * <li>No session associated with this request</li>
     * <li>No transaction token saved in the session</li>
     * <li>No transaction token included as a request parameter</li>
     * <li>The included transaction token value does not match the
     *     transaction token in the user's session</li>
     * </ul>
     *
     * @param request The servlet request we are processing
     * @param reset Should we reset the token after checking it?
     */
    public static boolean isTokenValid(HttpServletRequest request, boolean reset) {
    	
    	// Trecho de código que desabilita o per-page-token em todo o sistema.
    	// Esse cenário deve ser usado por hora, apenas nos testes de segurnaça,
    	// porque precisamos de uma navegação "livre" pelo sistema. DESENV-21300
    	try {
    		if (!TextHelper.isNull(SysConfig.get().getActiveProfile()) && SysConfig.get().getActiveProfile().equals(PROFILE_PENTEST)) {
    			if (!TextHelper.isNull(System.getProperty(DISABLE_PAGE_TOKEN)) && System.getProperty(DISABLE_PAGE_TOKEN).equals("true")) {
    				LOG.warn("CONFIGURAÇÃO INSEGURA: A OPÇÃO disable.page.token ESTÁ HABILITADA NA INICIALIZAÇÃO E SÓ DEVE SER UTILIZADA EM AMBIENTE DE TESTES.");
    				return true;
    			}
    		}
    	} catch (Exception ex) {
    		LOG.error(ex.getMessage(), ex);
    	}
    	
        // Retrieve the current session for this request
        HttpSession session = request.getSession(false);
        if (session == null) {
            return (false);
        }

        synchronized (session) {

            // Retrieve the transaction token from this session, and
            // reset it if requested
            String saved = getSessionToken(request);

            if (saved == null) {
                return (false);
            }
            if (reset) {
                session.removeAttribute(TRANSACTION_TOKEN_KEY);
            }

            // Retrieve the transaction token included in this request
            String token = getRequestToken(request);
            if (token == null) {
                return (false);
            }

            // Do the values match?
            return (saved.equals(token));
        }
    }

    /**
     * Save a new transaction token in the user's current session, creating
     * a new session if necessary.
     *
     * @param request The servlet request we are processing
     */
    public static void saveToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = generateToken();
        if (token != null) {
            session.setAttribute(TRANSACTION_TOKEN_KEY, token);
        }
    }

    /**
     * Reset the saved transaction token in the user's session.  This
     * indicates that transactional token checking will not be needed
     * on the next request that is submitted.
     *
     * @param request The servlet request we are processing
     */
    public static void resetToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(TRANSACTION_TOKEN_KEY);
    }

    /**
     * Convert a byte array to a String of hexadecimal digits and return it.
     *
     * @param buffer The byte array to be converted
     */
    private static String toHex(byte buffer[]) {
        StringBuilder sb = new StringBuilder();
        String s = null;
        for (byte element : buffer) {
            s = Integer.toHexString(element & 0xff);
            if (s.length() < 2) {
                sb.append('0');
            }
            sb.append(s);
        }
        return (sb.toString());
    }
}