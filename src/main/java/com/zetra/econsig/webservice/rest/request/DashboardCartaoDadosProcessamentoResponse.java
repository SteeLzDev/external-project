package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Setter;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardCartaoDadosProcessamentoResponse {
    public String harQtdLinhas;
    public String harResultadoProc;
    public String harDataProc;
    public String harNomeArquivo;
}
