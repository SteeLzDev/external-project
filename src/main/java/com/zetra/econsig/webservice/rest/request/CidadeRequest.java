package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: CidadeRequest</p>
 * <p>Description: Requisição Rest de parâmetros de cidade.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CidadeRequest {

    public String ufCod;
    public String termo;
    
}
