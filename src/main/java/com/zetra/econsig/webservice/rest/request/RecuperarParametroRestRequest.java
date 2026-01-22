package com.zetra.econsig.webservice.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecuperarParametroRestRequest {

    public String csaIdentificadorInterno;

    public List<RecuperarParametroRestResponse> listParametros;

    public String csaCodigo;

    public String svcCodigo;

    public String cnvCodigo;
}