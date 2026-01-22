package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: SolicitacaoLeilaoRestRequest</p>
 * <p>Description: Requisição Rest para serviços de leilão</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class SolicitacaoLeilaoRestRequest {

    public String adeCodigo;
}
