package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: MensagemRestRequest</p>
 * <p>Description: Requisição Rest de mensagens do sistema.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MensagemRestRequest {
    public String menCodigo;
}
