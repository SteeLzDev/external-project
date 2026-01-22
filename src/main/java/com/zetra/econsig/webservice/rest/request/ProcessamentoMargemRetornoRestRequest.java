package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ProcessamentoMargemRetornoRestRequest {

    public String orgCodigo;
    public String estCodigo;

}
