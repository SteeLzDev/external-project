package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UploadLoteRestRequest {

    public String orgao;
    
    public String estabelecimento;

    public String periodo;

    public String arqNome;
    
    public boolean validaLote = true;
    
    public String conteudo;
    
    public String leiaute = "imp_consignacao";
    
}
