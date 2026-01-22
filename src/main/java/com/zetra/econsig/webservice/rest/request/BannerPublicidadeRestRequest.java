package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BannerPublicidadeRestRequest {

    public String bpuCodigo;

    public String bpuData;

}
