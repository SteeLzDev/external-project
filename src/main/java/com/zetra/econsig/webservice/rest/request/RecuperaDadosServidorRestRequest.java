package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecuperaDadosServidorRestRequest {

    public String adeCodigo;
    public String tdaCodigo;
    public String dadValor;

}
