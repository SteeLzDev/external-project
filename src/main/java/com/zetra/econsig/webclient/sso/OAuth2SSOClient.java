package com.zetra.econsig.webclient.sso;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_AUTH_SSO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.CNPJHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.sso.response.UserDetailResponse;
import com.zetra.econsig.webclient.util.EconsigResponseErrorHandler;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: OAuth2SSOClient</p>
 * <p>Description: Implementação OAuth2 de SSO.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Fagner Luiz, Leonel Martins
 */
@Component
public class OAuth2SSOClient implements SSOClient {
    private static final String PASSWORD = "password";

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OAuth2SSOClient.class);

    private static Map<String, String> ssoTokenCache = null;

    private static final String ssoTokenExternalCacheKey = "externalSsoToken";

    private static final String SSO_TOKEN_HASH = "ssoTokenHash";

    private static volatile boolean justCached = false;

    @Lazy
    @Autowired
    @Qualifier("getRestTemplateOAuthAutentication")
    RestTemplate restOAuthConnTemplate;

    @Lazy
    @Autowired
    @Qualifier("simpleRestemplate")
    RestTemplate simpleRestTemplate;

    @Override
    public SSOToken autenticar(String usuarioId, String senha) throws SSOException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
            final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("username", usuarioId);
        map.add(PASSWORD, senha);
        map.add("grant_type", PASSWORD);

        final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);


        ResponseEntity<SSOToken> sso = null;
		try {
            sso = restOAuthConnTemplate.postForEntity(urlBase + "/sso/oauth/token", httpEntity, SSOToken.class);

            if (sso.getStatusCode() != HttpStatus.OK) {
                throw handleError(sso.getStatusCode(), responsavel);
            }
        } catch (final RestClientException res) {
            LOG.error(res);
            final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }
        return sso.getBody();
    }

    @Override
    public void logout(String accessToken) throws SSOException {

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
            throw new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
        }

        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer "+ accessToken);
            final HttpEntity<String> entity = new HttpEntity<>(headers);

            final ResponseEntity<String> sso = restOAuthConnTemplate.exchange(urlBase + "/sso/v0/oauth/user/logout", HttpMethod.GET, entity, String.class);

            if (sso.getStatusCode() != HttpStatus.OK) {
                throw handleError(sso.getStatusCode(), responsavel);
            }
        } catch (final Exception res) {
            LOG.error(res);
            throw new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
        }

    }

    @Override
    public void addUsuarioSSO(UsuarioTransferObject usuario, String usuSenha, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws SSOException {

        try {

            final boolean realizaCadastroSSO = UsuarioHelper.usuarioAutenticaSso(usuario, tipoEntidade, responsavel);

            if (realizaCadastroSSO) {

                final String usuEmail = usuario.getUsuEmail();

                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                final String identificadorInterno = cse.getIdentificadorInterno();

                String csaIdentificadorInterno = "";
                String csaCnpj = "";
                if (!TextHelper.isNull(tipoEntidade) && tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                	final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                	final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(codigoEntidade, responsavel);

                	csaIdentificadorInterno = csa.getCsaIdentificadorInterno();
                    csaCnpj = csa.getCsaCnpj();
                    
                }

                final String loginAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_LOGIN, responsavel);
                final String senhaAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_SENHA, responsavel);

                if (TextHelper.isNull(loginAdm) || TextHelper.isNull(senhaAdm)) {
                    throw new SSOException("mensagem.informe.sso.usuario.senha.nao.configurado", responsavel);
                }

                // Logar usuário admin para inclusão de novo usuário
                final SSOToken token = autenticar(loginAdm, senhaAdm);

                final String accessToken = token.access_token;

                final User u = new User();
                u.username = usuEmail;
                u.password = usuSenha;
                u.companyIntegrationCode = identificadorInterno;
                u.serviceProviderIntegrationCode = csaIdentificadorInterno;
                u.serviceProviderRegistrationNumber = CNPJHelper.getCnpjSemMascara(csaCnpj);
                u.paper = UsuarioHelper.getPapCodigo(tipoEntidade);
                u.ignoreDuplicateError = true;

                postUserSSO(accessToken, u, responsavel);

                try {
                    logout(accessToken);
                } catch (final SSOException e) {
                    // Se não conseguiu fazer o logout, apenas ignora porque pode já ter sido realizado
                    LOG.error(e.getLocalizedMessage(), e);
                    throw new SSOException(e);
                }
            }

        } catch (final Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new SSOException(e);
        }

    }

    public void postUserSSO (String accessToken, Object request, AcessoSistema responsavel) throws SSOException {

        try {

            final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

            final RestTemplate restTemplateSimple = new RestTemplate();
            restTemplateSimple.setErrorHandler(new EconsigResponseErrorHandler(responsavel));

            final HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+ accessToken);

            final String requestJson = new ObjectMapper().writeValueAsString(request);
            final HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            try {
                final ResponseEntity<String> resp = restTemplateSimple.postForEntity(urlBase + "/sso/v0/oauth/admin/user", entity, String.class);

                if (resp.getStatusCode() != HttpStatus.CREATED) {
                    throw handleError(resp.getStatusCode(), responsavel);
                }
            } catch (final RestClientException res) {
                LOG.error(res);
                throw new ZetraException("mensagem.usuarioSenhaInvalidos", responsavel);
            }

        } catch (final Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new SSOException(e);
        }

    }

    @Override
    public boolean isTokenValido(String usuarioId, String clientId, String token) throws SSOException {
        return this.isTokenValido(usuarioId, clientId, token, false);
    }

    @Override
    public boolean isTokenValido(String usuarioId, String clientId, String token, boolean checkTokenInCache) throws SSOException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
            final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }

        if (checkTokenInCache) {
            String cachedToken = null;

            if (ExternalCacheHelper.hasExternal() && ssoTokenCache == null) {
                synchronized(this) {
                    if (ssoTokenCache == null) {
                        ssoTokenCache = new ExternalMap<>(ssoTokenExternalCacheKey);
                    }
                }
            } else if (ssoTokenCache == null) {
                synchronized(this) {
                    if (ssoTokenCache == null) {
                        ssoTokenCache = new HashMap<>();
                    }
                }
            }

            synchronized(this) {
                cachedToken = ssoTokenCache.get(SSO_TOKEN_HASH);
            }

            if (TextHelper.isNull(cachedToken)) {
                if (justCached) {
                    return justCachedTokenCheck(usuarioId, clientId, token, responsavel, urlBase);
                }
                return checkAndSaveTokenInCache(usuarioId, clientId, token, responsavel, urlBase);
            } else {
                if (!cachedToken.equals(token)) {
                    if (justCached) {
                        return justCachedTokenCheck(usuarioId, clientId, token, responsavel, urlBase);
                    }

                    return checkAndSaveTokenInCache(usuarioId, clientId, token, responsavel, urlBase);
                } else {
                    return true;
                }
            }
        }

        return checkValidTokenSSO(usuarioId, clientId, token, responsavel, urlBase);
    }

    private boolean justCachedTokenCheck(String usuarioId, String clientId, String token, final AcessoSistema responsavel, final String urlBase) throws SSOException {
        String cachedToken;
        synchronized(this) {
            cachedToken = ssoTokenCache.get(SSO_TOKEN_HASH);
        }
        justCached = false;

        boolean isValid = false;
        if (!cachedToken.equals(token)) {
            isValid = checkAndSaveTokenInCache(usuarioId, clientId, token, responsavel, urlBase);
        } else {
            isValid = true;
        }

        return isValid;
    }

    private boolean checkAndSaveTokenInCache(String usuarioId, String clientId, String token, final AcessoSistema responsavel, final String urlBase) throws SSOException {
        boolean isValid = checkValidTokenSSO(usuarioId, clientId, token, responsavel, urlBase);

        if (isValid) {
            synchronized(this) {
                ssoTokenCache.put(SSO_TOKEN_HASH, token);
            }
            justCached = true;
        }

        return isValid;
    }

    private boolean checkValidTokenSSO(String usuarioId, String clientId, String token, final AcessoSistema responsavel, final String urlBase) throws SSOException {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
            final String encodedUsername = URLEncoder.encode(usuarioId, StandardCharsets.UTF_8.toString());
            final ResponseEntity<?> response = simpleRestTemplate.exchange(urlBase + "/sso/v0/oauth/token/verify?username=" + encodedUsername + "&token=" + token + "&client=" + clientId, HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw handleError(response.getStatusCode(), responsavel);
            }

            return (response.getStatusCode().equals(HttpStatus.OK));
        } catch (RestClientException | UnsupportedEncodingException res) {
            LOG.error(res);
            final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }
    }

    @Override
    public boolean updatePassword(SSOToken token, String password, String verifyPassword) throws SSOException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
            final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token.access_token);

        try {
            final HashMap<String, String> additionalDetails = new HashMap<>();
            additionalDetails.put(PASSWORD, password);
            additionalDetails.put("verifyPassword", verifyPassword);

            final JSONObject jsonObject = new JSONObject(additionalDetails);
            final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(responsavel);

            final ResponseEntity<?> response = restTemplateSimple.exchange(urlBase + "/sso/v0/oauth/user", HttpMethod.PUT, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw handleError(response.getStatusCode(), responsavel);
            }

            return (response.getStatusCode().equals(HttpStatus.OK));
        } catch (final RestClientException res) {
            LOG.error(res);
            final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }
    }

    @Override
    public String getDataExpiracao(SSOToken ssoToken) throws SSOException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
            final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + ssoToken.access_token);

        try {

            final HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(responsavel);

            final ResponseEntity<String> response = restTemplateSimple.exchange(urlBase + "/sso/v0/oauth/user", HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw handleError(response.getStatusCode(), responsavel);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("passwordExpirationDate").asText();
        } catch (final RestClientException res) {
            LOG.error(res);
            final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateExpiredPassword(String username, String newPassword, String currentPassword) throws SSOException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        try {
            final String loginAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_LOGIN, responsavel);
            final String senhaAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_SENHA, responsavel);
            final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

            if (TextHelper.isNull(urlBase)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
                final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
                ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
                throw ssoException;
            }

            if (TextHelper.isNull(loginAdm) || TextHelper.isNull(senhaAdm)) {
                throw new SSOException("mensagem.informe.sso.usuario.senha.nao.configurado", responsavel);
            }

            // Logar usuário admin para alteração de senha expirada
            final SSOToken token = autenticar(loginAdm, senhaAdm);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token.access_token);

            String userNameEncoded = "";
            try {
                userNameEncoded = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            } catch (final UnsupportedEncodingException e) {
                LOG.error("Error trying to encode username for sending it to SSO when trying to update expired password", e);
                return false;
            }

            final HashMap<String, String> additionalDetails = new HashMap<>();
            additionalDetails.put("currentPassword", currentPassword);
            additionalDetails.put(PASSWORD, newPassword);
            additionalDetails.put("verifyPassword", newPassword);

            final JSONObject jsonObject = new JSONObject(additionalDetails);
            final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(responsavel);

            final ResponseEntity<Object> response = restTemplateSimple.postForEntity(urlBase + "/sso/v0/oauth/expired-pass?username=" + userNameEncoded, httpEntity, Object.class);
            if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw handleError(response.getStatusCode(), responsavel);
            }

            try {
                logout(token.access_token);
            } catch (final SSOException e) {
                // Se não conseguiu fazer o logout, apenas ignora porque pode já ter sido realizado
                LOG.error(e.getLocalizedMessage(), e);
                throw new SSOException(e);
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOG.error(e);
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new SSOException("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel);
            } else if (e.getStatusCode().equals(HttpStatus.EXPECTATION_FAILED)) {
                throw new SSOException("mensagem.erro.senha.invalida", responsavel);
            } else if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                throw new SSOException("mensagem.erro.senha.atual.invalida", responsavel);
            }

            return false;
        } catch (final Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new SSOException(e);
        }

        return true;
    }

    @Override
    public boolean updatePasswordAsAdmin(String usuarioId, String newPassword) throws SSOException {
    	final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

    	final String loginAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_LOGIN, responsavel);
    	final String senhaAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_SENHA, responsavel);
    	final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

    	if (TextHelper.isNull(urlBase)) {
    		LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
    		final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
    		ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
    		throw ssoException;
    	}

    	if (TextHelper.isNull(loginAdm) || TextHelper.isNull(senhaAdm)) {
    		throw new SSOException("mensagem.informe.sso.usuario.senha.nao.configurado", responsavel);
    	}

    	// Logar usuário admin para alteração de senha expirada
    	final SSOToken token = autenticar(loginAdm, senhaAdm);

    	final HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.set("Authorization", "Bearer " + token.access_token);

    	final HashMap<String, String> requestBody = new HashMap<>();
    	requestBody.put("username", usuarioId);
    	requestBody.put(PASSWORD, newPassword);

    	final JSONObject jsonObject = new JSONObject(requestBody);
    	final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);


		try {
            final ResponseEntity<String> response = restOAuthConnTemplate.exchange(urlBase + "/sso/v0/oauth/admin/user", HttpMethod.PUT, httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw handleError(response.getStatusCode(), responsavel);
            }

            return true;
        } catch (final RestClientException res) {
            LOG.error(res);
            final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
            ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
            throw ssoException;
        }
	}

    @Override
    public boolean removeServiceProviderFromUser(String usuarioId, String tipoEntidade, String codigoEntidade) throws SSOException {
    	try {
			final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

			final String loginAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_LOGIN, responsavel);
			final String senhaAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_SENHA, responsavel);
			final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

			if (TextHelper.isNull(urlBase)) {
				LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
				final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
				ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
				throw ssoException;
			}

			if (TextHelper.isNull(loginAdm) || TextHelper.isNull(senhaAdm)) {
				throw new SSOException("mensagem.informe.sso.usuario.senha.nao.configurado", responsavel);
			}

			String csaIdentificadorInterno = "";
			if (!TextHelper.isNull(tipoEntidade) && tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
				final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
				final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(codigoEntidade, responsavel);
				csaIdentificadorInterno = csa.getCsaIdentificadorInterno();
			}

			// Logar usuário admin para alteração de senha expirada
			final SSOToken token = autenticar(loginAdm, senhaAdm);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + token.access_token);

			final HashMap<String, String> requestBody = new HashMap<>();
			requestBody.put("username", usuarioId);
			requestBody.put("serviceProvider", csaIdentificadorInterno);

			final JSONObject jsonObject = new JSONObject(requestBody);
			final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

			try {
			    final ResponseEntity<String> response = restOAuthConnTemplate.exchange(urlBase + "/sso/v0/oauth/admin/user/removeServiceProvider", HttpMethod.POST, httpEntity, String.class);

			    if (response.getStatusCode() != HttpStatus.OK) {
			        throw handleError(response.getStatusCode(), responsavel);
			    }

			    return true;
			} catch (final RestClientException res) {
			    LOG.error(res);
			    final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
			    ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
			    throw ssoException;
			}
        } catch (final Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new SSOException(e);
		}
	}

    @Override
    public UserDetailResponse getUserDetailUsingAdmin(String username, SSOToken ssoToken) throws SSOException {

       try {
			final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

			final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

			if (TextHelper.isNull(urlBase)) {
				LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
				final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
				ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
				throw ssoException;
			}

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + ssoToken.access_token);

			final HashMap<String, String> requestBody = new HashMap<>();
			requestBody.put("username", username);
			
			final JSONObject jsonObject = new JSONObject(requestBody);
			final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

			try {
			    final ResponseEntity<UserDetailResponse> response = restOAuthConnTemplate.exchange(urlBase + "/sso/v0/oauth/admin/user/userDetail", HttpMethod.POST, httpEntity, UserDetailResponse.class);

			    if (response.getStatusCode() != HttpStatus.OK) {
			        throw handleError(response.getStatusCode(), responsavel);
			    }

			    return response.getBody();
			} catch (final RestClientException res) {
			    LOG.error(res);
			    final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
			    ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
			    throw ssoException;
			}

        } catch (final Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new SSOException(e);
		}
    }
   
    @Override
    public void updateUserDetailUsingAdmin(String username, String emailIdentIntern, SSOToken ssoToken) throws SSOException {
       

       try {
			final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

            final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

			if (TextHelper.isNull(urlBase)) {
				LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", responsavel));
				final SSOException ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
				ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
				throw ssoException;
			}

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + ssoToken.access_token);

			final HashMap<String, String> requestBody = new HashMap<>();
			requestBody.put("username", username);
            requestBody.put("emailIdentInternEconsig", emailIdentIntern);
			
			final JSONObject jsonObject = new JSONObject(requestBody);
			final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

			try {
			    final ResponseEntity<UserDetailResponse> response = restOAuthConnTemplate.exchange(urlBase + "/sso/v0/oauth/admin/user/updateUserDetail", HttpMethod.PUT, httpEntity, UserDetailResponse.class);

			    if (response.getStatusCode() != HttpStatus.OK) {
			        throw handleError(response.getStatusCode(), responsavel);
			    }

			} catch (final RestClientException res) {
			    LOG.error(res);
			    final SSOException ssoException = new SSOException(MENSAGEM_ERRO_AUTH_SSO, responsavel);
			    ssoException.setSsoError(SSOErrorCodeEnum.GENERIC_ERROR);
			    throw ssoException;
			}

        } catch (final Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new SSOException(e);
		}
    
        
    }

	public class User {
        public String username;
        public String password;
        public String companyIntegrationCode;
        public String serviceProviderIntegrationCode;
        public String serviceProviderRegistrationNumber;
        public String paper;
        public Boolean ignoreDuplicateError;
    }

}
