package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ServicoRestRequest {

    public String nseCodigo;
    public String orgCodigo;
    public String csaCodigo;

}
