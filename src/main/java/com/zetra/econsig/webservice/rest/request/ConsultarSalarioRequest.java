package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultarSalarioRequest {
 
    public String cpf;
    public ConsentPayload consentPayload;

}
