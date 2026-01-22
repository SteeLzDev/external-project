package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ResponseRestRequest</p>
 * <p>Description: Response rest para upload de lote</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UploadLoteRestResponse {

    public String mensagem;
    
    public String critica;
    
    public String validacao;

    public String nomeArquivo;


    public UploadLoteRestResponse() {}

    public UploadLoteRestResponse(String nomeArquivo, String mensagem, String critica, String validacao) {
    	this.mensagem = mensagem;
    	this.critica = critica;
    	this.validacao = validacao;
        this.nomeArquivo = nomeArquivo;
    }
}
