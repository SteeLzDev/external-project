package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BannerPublicidadeRestResponse {

    public String bpuCodigo;
    public String nseCodigo;
    public String bpuDescricao;
    public String arqConteudo;
    public String bpuUrlSaida;
    public String bpuData;
    public Short bpuOrdem;

}

