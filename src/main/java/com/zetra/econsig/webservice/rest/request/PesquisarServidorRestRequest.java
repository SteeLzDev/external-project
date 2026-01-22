package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: PesquisarServidorRestRequest</p>
 * <p>Description: Requisição Rest para consulta de servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PesquisarServidorRestRequest {

    public String rseMatricula;

    public String serCpf;

    public String estIdentificador;

    public String orgIdentificador;

}
