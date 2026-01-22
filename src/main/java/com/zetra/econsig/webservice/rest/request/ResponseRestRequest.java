package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;

/**
 * <p>Title: ResponseRestRequest</p>
 * <p>Description: Requisição Rest de mensagem de retorno padrão.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseRestRequest {

    public String mensagem;

    public Boolean senhaExpirada; 

    public ResponseRestRequest() {}

    public ResponseRestRequest(String mensagem, Boolean senhaExpirada) {
        this.senhaExpirada = senhaExpirada;
    	this.mensagem = mensagem;
    }

    public ResponseRestRequest(String mensagem) {
    	this.mensagem = mensagem;
    }
}
