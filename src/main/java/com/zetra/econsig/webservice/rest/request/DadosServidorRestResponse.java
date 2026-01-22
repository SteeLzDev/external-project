package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: DadosServidorRestResponse</p>
 * <p>Description: Rest response com dados do servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: andrea.giorgini $
 * $Revision: 26618 $
 * $Date: 2019-04-29 $
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DadosServidorRestResponse {

    // Dados do servidor
    public String telefone;
    public String email;

    public DadosServidorRestResponse() {}
}
