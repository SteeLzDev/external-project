package com.zetra.econsig.webclient.sso;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webclient.sso.response.UserDetailResponse;

/**
 * <p>Title: SSOClient</p>
 * <p>Description: Interface para métodos de acesso ao serviço SSO.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public interface SSOClient {

    public final String USERNAME_PARAM_NAME = "username";

    public final String CLIENT_ID_PARAM_NAME = "client";

    public SSOToken autenticar(String usuarioId, String senha) throws SSOException;

    public void logout(String accessToken) throws SSOException;

    public void addUsuarioSSO(UsuarioTransferObject usuario, String usuSenha, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws SSOException;

    public boolean isTokenValido(String usuarioId, String clientId, String token) throws SSOException;

    public boolean isTokenValido(String usuarioId, String clientId, String token, boolean checkTokenInCache) throws SSOException;

    public boolean updatePassword(SSOToken token, String password, String verifyPassword) throws SSOException;

    public boolean updateExpiredPassword(String username, String currentPassword, String newPassword) throws SSOException;

    public boolean updatePasswordAsAdmin(String usuarioId, String newPassword) throws SSOException;

    public boolean removeServiceProviderFromUser(String usuarioId, String tipoEntidade, String codigoEntidade) throws SSOException;

    public String getDataExpiracao(SSOToken ssoToken) throws SSOException;

    public UserDetailResponse getUserDetailUsingAdmin(String username, SSOToken ssoToken) throws SSOException;

    public void updateUserDetailUsingAdmin(String username, String emailIdentIntern, SSOToken ssoToken) throws SSOException; 

    public default SSOException handleError(HttpStatusCode status, AcessoSistema responsavel) {
    	SSOException ssoException = null;
        // até este momento o retorno de autenticação inválida retorna BAD_REQUEST
        if (status.value() == HttpStatus.BAD_REQUEST.value()) {
            ssoException = new SSOException("mensagem.usuarioSenhaInvalidos", responsavel);
        } else if (status.is5xxServerError() || status.is4xxClientError()) {
            ssoException = new SSOException("mensagem.usuario.erro.autenticacao.servico", responsavel);
        }

        return ssoException;
    }
}
