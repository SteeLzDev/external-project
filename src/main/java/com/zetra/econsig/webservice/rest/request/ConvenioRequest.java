package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ConvenioRequest</p>
 * <p>Description: Requisição Rest de parâmetros de convênio.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConvenioRequest {

    public String svcCodigo;

    public String orgCodigo;

    public String csaCodigo;
}
