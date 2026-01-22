package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AnexoAutDescontoRestRequest {

    public String adeCodigo;

    public String nome;

    public String tarCodigo;

    public String descricao;

    public String conteudo;


}
