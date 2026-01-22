package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ParcelaRestRequest</p>
 * <p>Description: Requisição Rest de consulta de parcela.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ParcelaRestRequest {

    public String status;

    public String periodo;

}
