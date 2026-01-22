package com.zetra.econsig.webservice.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResultadoConciliacaoRestRequest {

    public String orgIdentificador;
    public String periodo;
    public List<String> cpf;
    public List<Long> adeNumero;
    public List<String> adeIdentificador;
    public String statusPagamento;

}
