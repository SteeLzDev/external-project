package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ContraChequeRestRequest {

    public String periodo;

    public String periodoNome;

    public String arquivo;
    
    public String dataInicio;
    
    public String dataFim;
}
