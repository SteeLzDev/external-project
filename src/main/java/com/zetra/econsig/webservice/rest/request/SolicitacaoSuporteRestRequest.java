package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: SolicitacaoSuporteRestRequest</p>
 * <p>Description: Requisição Rest de mensagem de retorno padrão.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class SolicitacaoSuporteRestRequest {

    public String sosServico;
    public String sosSumario;
    public String sosDescricao;
    public String usuarioSuporte;
    public String matricula;
    public String arquivo;
}
