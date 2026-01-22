package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: CodigoUnicoRestResponse</p>
 * <p>Description: Rest response com o código único do servidor.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CodigoUnicoRestResponse {
    public String codigoUnico;

    public String dataCriacao;

    public String dataExpiracao;

    public Short qtdOperacoes;
    
    public String msgRetorno;

    public CodigoUnicoRestResponse() {}
}
