package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BoletoServidorRestResponse {

    public String codigo;

    public String consignataria;

    public String dataUpload;

    public String dataDownload;

    public String conteudo;
}
