package com.zetra.econsig.webservice.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: AutorizarMargemRestRequest</p>
 * <p>Description: Requisição Rest para autorizar margem sem senha</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class AutorizarMargemRequest {
    private List<String> csaCodigos;

    public List<String> getCsaCodigos() {
        return csaCodigos;
    }

    public void setCsaCodigos(List<String> csaCodigos) {
        this.csaCodigos = csaCodigos;
    }
}

