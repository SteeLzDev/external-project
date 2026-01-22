package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ShowRestRequest</p>
 * <p>Description: Requisição Rest Genérica para um show de uma entidade.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ShowRestRequest {

    public String id;

    public Integer offset;

    public Integer size;

}
