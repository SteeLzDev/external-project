package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardCartaoResponse {
    public String cseNome;
    public String periodoAtual;
    public int diaDeCorte;
    public List<DashboardCartaoDadosProcessamentoResponse> dadosProcessamento = new ArrayList<>();
}

