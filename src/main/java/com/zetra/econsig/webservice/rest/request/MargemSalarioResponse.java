package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MargemSalarioResponse {
    public BigDecimal margemMedia;
    public String margemDescricao;
    public BigDecimal margemPorcentagem;
    public BigDecimal margemRest;
    public BigDecimal margem;
    public BigDecimal margemUsada;
}
