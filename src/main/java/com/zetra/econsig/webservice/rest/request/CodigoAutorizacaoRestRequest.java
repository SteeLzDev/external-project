package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: CodigoAutorizacaoRestRequest</p>
 * <p>Description: Requisição Rest de geracao e validação de codigo de autorização</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CodigoAutorizacaoRestRequest {
    public String rseCodigo;
    public String codAutorizacao;




    public String getRseCodigo() {
        return rseCodigo;
    }
    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }
    public String getCodAutorizacao() {
        return codAutorizacao;
    }
    public void setCodAutorizacao(String codAutorizacao) {
        this.codAutorizacao = codAutorizacao;
    }



}
