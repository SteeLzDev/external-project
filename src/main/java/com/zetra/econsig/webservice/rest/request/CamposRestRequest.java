package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: CamposRestRequest</p>
 * <p>Description: Rest response com dados do campo sistema.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CamposRestRequest {
    public String casChave;
    public boolean somenteCamposEditaveis;
}
