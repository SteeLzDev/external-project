package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AnexoRseRestResponse {

    public String rseCodigo;

    public String arqCodigo;

    public String usuCodigo;

    public String dataCriacao;

    public String arqNome;
    
    public String ipAcesso;
    
    public String conteudo;
}
