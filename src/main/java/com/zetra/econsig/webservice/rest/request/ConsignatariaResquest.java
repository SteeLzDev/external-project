package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConsignatariaResquest {
    public String csaCodigo;
    public String csaIdentificador;
    public String csaNome;
    public String csaNatureza;
    public String csaCnvCodVerba;
    public String csaBloqueada;
}
