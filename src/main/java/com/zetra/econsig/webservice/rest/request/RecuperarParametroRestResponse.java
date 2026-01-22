package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: RecuperarParametroRestResponse</p>
 * <p>Description: Rest response com o valor do parâmetro recuperado.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecuperarParametroRestResponse {
    public String codigoParametro;

    public String valorParametro;

    public RecuperarParametroRestResponse() {}
}
