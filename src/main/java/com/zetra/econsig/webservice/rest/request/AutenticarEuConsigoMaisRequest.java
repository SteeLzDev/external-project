package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: AutenticarEuConsigoMaisRequest</p>
 * <p>Description: Requisição Rest do fluxo de single signon do euconsigomais.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AutenticarEuConsigoMaisRequest {

    public String idConsignante;

    public String email;

    public String token;

    public boolean sucesso;

    public String mensagem;

    public AutenticarEuConsigoMaisRequest() {}

    public AutenticarEuConsigoMaisRequest(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getIdConsignante() {
        return idConsignante;
    }

    public void setIdConsignante(String idConsignante) {
        this.idConsignante = idConsignante;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

}
