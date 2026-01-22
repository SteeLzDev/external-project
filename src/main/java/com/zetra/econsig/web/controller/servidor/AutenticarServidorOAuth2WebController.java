package com.zetra.econsig.web.controller.servidor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.MetodoSenhaExternaEnum;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutenticarServidorOAuth2WebController</p>
 * <p>Description: Controlador Web para o caso de uso Autenticar Servidor via Senha Externa usando OAuth2.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class AutenticarServidorOAuth2WebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutenticarServidorOAuth2WebController.class);

    @RequestMapping(value = { "/v3/redirecionarOAuth2" }, params = { "acao=entrar" })
    public String redirecionarOAuth2Entrar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return redirecionarOAuth2(CodedValues.OAUTH2_ACAO_LOGIN, request, response, session, model);
    }

    @RequestMapping(value = { "/v3/redirecionarOAuth2" }, params = { "acao=autorizar" })
    public String redirecionarOAuth2Autorizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return redirecionarOAuth2(CodedValues.OAUTH2_ACAO_AUTORIZAR_OPERACAO, request, response, session, model);
    }

    private String redirecionarOAuth2(String acao, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String url = getOAuth2UriAuthentication(acao, request, responsavel);
        request.setAttribute("url64", TextHelper.encode64(url));
        request.setAttribute("doGet", Boolean.TRUE);

        // Relaxa a regra de segurança de Cross-Origin-Opener-Policy de modo que a janela aberta possa manipular a janela pai
        disableCrossOriginOpenerPolicy(response);

        // DESENV-21335 : força o envio do cookie de sessão para o servidor remoto
        // Os navegadores devem devem passar a não enviar os cookies mesmo quando for https.
        // Neste caso, será necessário rever a configuração de SameSite no SpringSessionConfiguration
        String sessionId = session.getId();
        for (final Cookie cookie : request.getCookies()) {
            if ("sbsessionid".equalsIgnoreCase(cookie.getName())) {
                sessionId = cookie.getValue();
                break;
            }
        }
        response.addCookie(new Cookie("sbsessionid", sessionId));

        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(value = { "/v3/autenticarOAuth2" })
    public String autenticarOAuth2(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String acao = (String) request.getSession().getAttribute(CodedValues.OAUTH2_ACAO_ATTIBUTE_NAME);
        final boolean isStateValid = validateState(request);

        if (!ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) ||
            !MetodoSenhaExternaEnum.OAUTH2.getMetodo().equals(ParamSenhaExternaEnum.METODO.getValor()) ||
            !isStateValid) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        request.getSession().removeAttribute(CodedValues.OAUTH2_ACAO_ATTIBUTE_NAME);

        final String code = request.getParameter(ParamSenhaExternaEnum.OAUTH2_PARAM_CODE.getValor());
        final String oAuth2Method = ParamSenhaExternaEnum.OAUTH2_METHOD.getValor();
        final Map<String, String> authValues = "JWTS".equalsIgnoreCase(oAuth2Method) ?
                getOAuth2Jwts(code, request.getParameter("state"), false, responsavel) : getOAuth2Token(code, false, responsavel);

        String cpf = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_CPF.getValor());
        final String token = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_TOKEN.getValor());

        try {
            final String email = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_EMAIL.getValor());
            if (TextHelper.isNull(cpf) && !TextHelper.isNull(email)) {
                cpf = UsuarioHelper.getCPFByUsuEmailSer(email, responsavel);
            }
        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroLoginServidor.oauth2.token.invalido", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (TextHelper.isNull(cpf) || TextHelper.isNull(token)) {
            request.setAttribute("linkRet", "../v3/autenticar");
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroLoginServidor.oauth2.token.invalido", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Repassa os parâmetros para autenticação
        request.setAttribute("cpf", TextHelper.format(cpf, LocaleHelper.getCpfMask()));
        request.setAttribute("OAuth2TokenValido", Boolean.TRUE);
        request.setAttribute("OAuth2Token", token);

        // Armazena id_token para realizar o logout
        session.setAttribute(CodedValues.OAUTH2_ID_TOKEN, authValues.get("id_token"));

        // Somente deve considerar que é login tiver a marca de login.
        // Assim evita que outros usuário consigam enganar o servidor e logar em sua conta de forma indevida.
        if (CodedValues.OAUTH2_ACAO_LOGIN.equals(acao)) {
            return "forward:/v3/autenticar?acao=autenticar";
        }

        // Relaxa a regra de segurança de Cross-Origin-Opener-Policy de modo que a janela aberta possa manipular a janela pai
        disableCrossOriginOpenerPolicy(response);

        // Se não for login, será considerado que está sendo usado o processo de autorização de operação.
        return "jsp/receberToken/receberTokenOAuth2";
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getOAuth2Jwts(String code, String codeVerifier, boolean encodeUrlAutenticacao, AcessoSistema responsavel) {
        final String oAuth2UriTokenValidation = ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_2_TOKEN.getValor();
        final String oAuth2ClientId = ParamSenhaExternaEnum.OAUTH2_CLIENT_ID.getValor();
        final String oAuth2ClientKey = ParamSenhaExternaEnum.OAUTH2_CLIENT_KEY.getValor();
        final String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        String urlAutenticacao = urlSistema + (urlSistema.endsWith("/") ? "v3" : "/v3") + "/autenticarOAuth2";

        try {
            if (encodeUrlAutenticacao) {
                urlAutenticacao = URLEncoder.encode(urlAutenticacao, StandardCharsets.UTF_8.name());
            }
        } catch (final UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(oAuth2UriTokenValidation);
        urlBuilder.append(oAuth2UriTokenValidation.indexOf('?') == -1 ? "?" : "&");
        urlBuilder.append("grant_type=").append("authorization_code");
        urlBuilder.append("&client_id=").append(oAuth2ClientId);
        urlBuilder.append("&redirect_uri=").append(urlAutenticacao);
        urlBuilder.append("&code=").append(code);
        urlBuilder.append("&code_verifier=").append(codeVerifier);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(String.valueOf(oAuth2ClientId + ":" + oAuth2ClientKey).getBytes()));
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        final HttpEntity<?> entity = new HttpEntity<>(body, headers);

        final Map<String, String> authValues = new HashMap<>();
        final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
        try {
            final ResponseEntity<?> restResponse = restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, entity, HashMap.class);
            LOG.debug("OAuth2-Jws: retorno " + restResponse.getStatusCode().value());
            LOG.debug(restResponse.getBody());
            if (HttpStatus.OK.equals(restResponse.getStatusCode())) {
                final String key = ParamSenhaExternaEnum.OAUTH2_REMOTE_PUBLIC_KEY.getValor();
                final byte[] publicBytes = Base64.getDecoder().decode(key);
                final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final PublicKey pubKey = keyFactory.generatePublic(keySpec);

                authValues.putAll((HashMap<String, String>)restResponse.getBody());
                final String token = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_TOKEN.getValor());

                if (!TextHelper.isNull(token)) {
                    final Claims claims = verifyToken(token, pubKey);
                    claims.entrySet().forEach(c -> authValues.put(c.getKey(), c.getValue().toString()));
                }
            } else  if (!encodeUrlAutenticacao) {
                // DESENV-15757 : se deu erro, tenta usar a URL codificada pois o servidor de senhas pode ou não ter o tratamento da URL.
                return getOAuth2Token(code, true, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
        }

        return authValues;

    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getOAuth2Token(String code, boolean encodeUrlAutenticacao, AcessoSistema responsavel) {
        final String oAuth2UriTokenValidation = ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_2_TOKEN.getValor();
        final String oAuth2ClientId = ParamSenhaExternaEnum.OAUTH2_CLIENT_ID.getValor();
        final String oAuth2ClientKey = ParamSenhaExternaEnum.OAUTH2_CLIENT_KEY.getValor();
        final String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        String urlAutenticacao = urlSistema + (urlSistema.endsWith("/") ? "v3" : "/v3") + "/autenticarOAuth2";

        try {
            if (encodeUrlAutenticacao) {
                urlAutenticacao = URLEncoder.encode(urlAutenticacao, StandardCharsets.UTF_8.name());
            }
        } catch (final UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(String.valueOf(oAuth2ClientId + ":" + oAuth2ClientKey).getBytes()));
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", oAuth2ClientId);
        body.add("redirect_uri", urlAutenticacao);
        body.add("code", code);
        body.add("response_type", "id_token token");
        final HttpEntity<?> entity = new HttpEntity<>(body, headers);

        final Map<String, String> authValues = new HashMap<>();
        final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
        try {
            final ResponseEntity<?> restResponse = restTemplate.exchange(oAuth2UriTokenValidation, HttpMethod.POST, entity, HashMap.class);
            LOG.debug("OAuth2: retorno " + restResponse.getStatusCode().value());
            LOG.debug(restResponse.getBody());
            if (HttpStatus.OK.equals(restResponse.getStatusCode())) {
                authValues.putAll((HashMap<String, String>)restResponse.getBody());
                String cpf = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_CPF.getValor());
                final String token = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_TOKEN.getValor());

                try {
                    final String email = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_EMAIL.getValor());
                    if (TextHelper.isNull(cpf) && !TextHelper.isNull(email)) {
                        cpf = UsuarioHelper.getCPFByUsuEmailSer(email, responsavel);
                    }
                } catch (final UsuarioControllerException ex) {
                    LOG.error(ex.getLocalizedMessage(), ex);
                }

                if (TextHelper.isNull(cpf) && !TextHelper.isNull(token)) {
                    final String oAuth2UriUserInfo = ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_3_USERINFO.getValor();
                    if (!TextHelper.isNull(oAuth2UriUserInfo)) {
                        final HttpHeaders headers2 = new HttpHeaders();
                        headers2.set("Accept", "application/json");
                        headers2.set("Authorization", "Bearer " + token);
                        headers2.set("Content-Type", "application/x-www-form-urlencoded");
                        final HttpEntity<?> entity2 = new HttpEntity<>(headers2);
                        boolean utilizarGET = false;
                        ResponseEntity<?> restResponse2 = null;

                        try {
                            restResponse2 = restTemplate.exchange(oAuth2UriUserInfo, HttpMethod.POST, entity2, HashMap.class);
                            LOG.debug("OAuth2: retorno2 (POST) " + restResponse2.getStatusCode().value());
                            LOG.debug(restResponse2.getBody());
                        } catch(final UnknownContentTypeException ex) {
                            LOG.debug("OAuth2: Tentando (GET) ");
                            utilizarGET = true;
                        } catch(final RestClientException ex) {
                            LOG.error(ex.getLocalizedMessage(), ex);
                            LOG.debug("OAuth2: Tentando (GET) ");
                            utilizarGET = true;
                        }

                        if (!utilizarGET && HttpStatus.OK.equals(restResponse2.getStatusCode())) {
                            authValues.putAll((HashMap<String, String>)restResponse2.getBody());
                        } else {
                            restResponse2 = restTemplate.exchange(oAuth2UriUserInfo, HttpMethod.GET, entity2, HashMap.class);
                            LOG.debug("OAuth2: retorno2 (GET) " + restResponse2.getStatusCode().value());
                            LOG.debug(restResponse2.getBody());
                            if (HttpStatus.OK.equals(restResponse2.getStatusCode())) {
                                authValues.putAll((HashMap<String, String>)restResponse2.getBody());
                            }
                        }
                    }
                    // access_token
                    authValues.putAll(decode(token));
                    // id_token
                    authValues.putAll(decode(authValues.get("id_token")));
                }
            } else  if (!encodeUrlAutenticacao) {
                // DESENV-15757 : se deu erro, tenta usar a URL codificada pois o servidor de senhas pode ou não ter o tratamento da URL.
                return getOAuth2Token(code, true, responsavel);
            }
        } catch(final Exception ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
        }

        return authValues;
    }

    /**
     * Decodifica o token padrão OAuth2 em um mapa com os valores presentes no Token.
     * @param token Token a ser codificado
     * @return Um Map com os valores presentes no token padrão OAuth2.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> decode(String token) {
        Map<String, String> map = new HashMap<>();
        if (!TextHelper.isNull(token)) {
            final String[] parts = token.split("\\.");

            if (parts.length == 3) {
                final byte[] bytes = Base64.getDecoder().decode(parts[1]);
                LOG.debug(new String(bytes));
                try {
                    map = new ObjectMapper().readValue(bytes, HashMap.class);
                } catch (final IOException e) {
                    // DESENV-15757 : optou-se por apenas mostrar o erro no log for possível fazer o decode.
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return map;
    }

    public static boolean isOAuth2TokenValid(String cpf, String token, AcessoSistema responsavel) {
        // VALIDAÇÃO USANDO OS DADOS NO JWTS
        final String oAuth2Method = ParamSenhaExternaEnum.OAUTH2_METHOD.getValor();
        if ((cpf != null) && "JWTS".equalsIgnoreCase(oAuth2Method)) {
            final String key = ParamSenhaExternaEnum.OAUTH2_REMOTE_PUBLIC_KEY.getValor();
            final byte[] publicBytes = Base64.getDecoder().decode(key);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            try {
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final PublicKey pubKey = keyFactory.generatePublic(keySpec);
                final Claims claims = verifyToken(token, pubKey);
                final String cpfParam = TextHelper.dropSeparator(cpf);
                final String cpfToken = String.valueOf(claims.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_CPF.getValor()));
                return cpfParam.equals(cpfToken);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                LOG.error(ex.getMessage(), ex);
                return false;
            }
        }
        // VALIDAÇÃO USANDO UMA URL DO GESTOR
        final String oAuth2UriTokenValidation = ParamSenhaExternaEnum.OAUTH2_URI_TOKEN_VALIDATION.getValor();
        boolean tokenValido = false;

        if (!TextHelper.isNull(oAuth2UriTokenValidation)) {
            final HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("Authorization", "Bearer " + token);
            final HttpEntity<?> entity = new HttpEntity<>(headers);

            if (cpf != null) {
                cpf = TextHelper.dropSeparator(cpf);
            }
            final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
            final ResponseEntity<?> restResponse = restTemplate.exchange(oAuth2UriTokenValidation + cpf, HttpMethod.GET, entity, String.class);
            LOG.debug("OAuth2: retorno " + restResponse.getStatusCode().value());
            if (HttpStatus.OK.equals(restResponse.getStatusCode())) {
                tokenValido = true;
            }
        } else {
            final String oAuth2UriUserInfo = ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_3_USERINFO.getValor();
            if (!TextHelper.isNull(oAuth2UriUserInfo)) {
                final HttpHeaders headers2 = new HttpHeaders();
                headers2.set("Accept", "application/json");
                headers2.set("Authorization", "Bearer " + token);
                headers2.set("Content-Type", "application/x-www-form-urlencoded");
                final HttpEntity<?> entity2 = new HttpEntity<>(headers2);
                final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
                ResponseEntity<?> restResponse2 = null;

                try {
                    restResponse2 = restTemplate.exchange(oAuth2UriUserInfo, HttpMethod.POST, entity2, HashMap.class);
                    LOG.debug("OAuth2: retorno (POST) " + restResponse2.getStatusCode().value());
                } catch(final RestClientException ex) {
                    LOG.warn(ex.getMessage());
                    LOG.debug("OAuth2: tentando (GET) ");
                    restResponse2 = restTemplate.exchange(oAuth2UriUserInfo, HttpMethod.GET, entity2, HashMap.class);
                }
                LOG.debug("OAuth2: retorno " + restResponse2.getStatusCode().value());
                LOG.debug(restResponse2.getBody());
                if (HttpStatus.OK.equals(restResponse2.getStatusCode())) {
                    @SuppressWarnings("unchecked")
                    final Map<String, String> authValues = (HashMap<String, String>)restResponse2.getBody();
                    if (authValues != null) {
                        String cpfToken = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_CPF.getValor());

                        try {
                            final String email = authValues.get(ParamSenhaExternaEnum.OAUTH2_RESPONSE_EMAIL.getValor());
                            if (TextHelper.isNull(cpfToken) && !TextHelper.isNull(email)) {
                                cpfToken = UsuarioHelper.getCPFByUsuEmailSer(email, responsavel);
                            }
                        } catch (final UsuarioControllerException ex) {
                            LOG.error(ex.getLocalizedMessage(), ex);
                        }

                        if ((cpf != null) && (cpfToken != null)) {
                            final String cpfFormatado = TextHelper.format(cpf, LocaleHelper.getCpfMask());
                            final String cpfTokenFormatado = TextHelper.format(cpfToken, LocaleHelper.getCpfMask());
                            tokenValido = cpfTokenFormatado.equals(cpfFormatado);
                        }
                    }
                }
            }
        }
        return tokenValido;
    }

    private static String getOAuth2UriAuthentication(String acao, HttpServletRequest request, AcessoSistema responsavel) {
        final String oAuth2UriAuthentication = LoginHelper.getOAuth2UriAuthentication(acao, responsavel);
        final String state = prepareState(acao, request);

        final StringBuilder uri = new  StringBuilder();
        uri.append(oAuth2UriAuthentication).append("&state=").append(state);
        final String oAuth2Method = ParamSenhaExternaEnum.OAUTH2_METHOD.getValor();
        if ("JWTS".equalsIgnoreCase(oAuth2Method)) {
            uri.append("&code_challenge=").append(state).append("&code_challenge_method=plain");
        }

        return uri.toString();
    }

    private static String prepareState(String acao, HttpServletRequest request) {
        final KeyPair kp = RSA.generateKeyPair(CodedValues.RSA_KEY_SIZE);
        final String token = SynchronizerToken.generateToken();
        request.getSession().setAttribute(CodedValues.OAUTH2_ACAO_ATTIBUTE_NAME, acao);
        request.getSession().setAttribute(CodedValues.OAUTH2_STATE_KEYPAIR_ATTIBUTE_NAME, kp);
        request.getSession().setAttribute(CodedValues.OAUTH2_STATE_TOKEN_ATTIBUTE_NAME, token);

        return Base64.getEncoder().encodeToString(RSA.encrypt(acao + "\n" + token, kp.getPublic()).getBytes());
    }

    public static Claims verifyToken(String token, PublicKey publicKey)  {
        final Jws<Claims> claims = Jwts.parserBuilder()
                                       .setSigningKey(publicKey)
                                       .build()
                                       .parseClaimsJws(token);
        return claims.getBody();
    }

    private static boolean validateState(HttpServletRequest request) {
        String state = request.getParameter("state");
        if (!TextHelper.isNull(state)) {
            final String acao = (String) request.getSession().getAttribute(CodedValues.OAUTH2_ACAO_ATTIBUTE_NAME);
            final KeyPair kp = (KeyPair) request.getSession().getAttribute(CodedValues.OAUTH2_STATE_KEYPAIR_ATTIBUTE_NAME);
            final String savedToken = (String) request.getSession().getAttribute(CodedValues.OAUTH2_STATE_TOKEN_ATTIBUTE_NAME);
            request.getSession().removeAttribute(CodedValues.OAUTH2_STATE_TOKEN_ATTIBUTE_NAME);

            if ((acao != null) && (kp != null) && (savedToken != null)) {
                try {
                    String decrypted;
                    try {
                        state = new String(Base64.getDecoder().decode(state.getBytes()));
                        decrypted = RSA.decrypt(URLDecoder.decode(state, StandardCharsets.UTF_8.name()), kp.getPrivate());
                    } catch (BadPaddingException | UnsupportedEncodingException e) {
                        decrypted = RSA.decrypt(state, kp.getPrivate());
                    }
                    if (decrypted != null) {
                        final String[] values = TextHelper.split(decrypted, "\n");
                        if ((values.length > 1) && (acao.equals(values[0]))) {
                            final String token = values[1];
                            if (!TextHelper.isNull(token)) {
                                return token.equals(savedToken);
                            }
                        }
                    }
                } catch (final BadPaddingException e) {
                    LOG.debug(e.getLocalizedMessage(), e);
                }
            } else {
                final String oAuth2Method = ParamSenhaExternaEnum.OAUTH2_METHOD.getValor();
                return "JWTS".equalsIgnoreCase(oAuth2Method);
            }
        }
        return false;
    }

    public static void disableCrossOriginOpenerPolicy(HttpServletResponse response) {
        // Relaxa a regra de segurança de Cross-Origin-Opener-Policy de modo que a janela aberta possa manipular a janela pai
        response.addHeader("Cross-Origin-Opener-Policy", "unsafe-none");
    }
}
