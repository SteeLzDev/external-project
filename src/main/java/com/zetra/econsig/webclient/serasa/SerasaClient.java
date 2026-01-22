package com.zetra.econsig.webclient.serasa;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.zetra.econsig.dto.web.ConsentRequestDTO;
import com.zetra.econsig.dto.web.SerasaToken;
import com.zetra.econsig.exception.SerasaException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
/**
 * <p>Title: SerasaClient</p>
 * <p>Description: Interface para métodos de acesso ao serviço da Serasa.</p>
 * <p>Company: Salt</p>
 * @author Davi
 */

public interface SerasaClient {

    public ResponseEntity<SerasaToken> autenticar(String serasaTokenUrl, String clientId, String clientSecret, AcessoSistema responsavel) throws SerasaException;

    public boolean enviarConsentimento(ConsentRequestDTO consentData, AcessoSistema responsavel, SerasaToken token) throws SerasaException;

    public default SerasaException handleError(HttpStatusCode status, AcessoSistema responsavel) {
    	SerasaException serasaException = null;
        if (status.value() == HttpStatus.BAD_REQUEST.value()) {
            serasaException = new SerasaException("mensagem.usuarioSenhaInvalidos", responsavel);
        } else if (status.is5xxServerError() || status.is4xxClientError()) {
            serasaException = new SerasaException("mensagem.usuario.erro.autenticacao.servico", responsavel);
        }

        return serasaException;
    }
}
