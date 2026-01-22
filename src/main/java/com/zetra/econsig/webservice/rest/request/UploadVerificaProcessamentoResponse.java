package com.zetra.econsig.webservice.rest.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadVerificaProcessamentoResponse {
    public String mensagem;

    public String criticaBase64;
}
