package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsaListInfoRequest {
    @JsonProperty("csaNome")
    private String csaNome;

    @JsonProperty("ranking")
    private int ranking;

    @JsonProperty("csaEmail")
    private String csaEmail;

    @JsonProperty("csaWhatsapp")
    private String csaWhatsapp;

    @JsonProperty("csaTxt")
    private String csaTxt;
}
