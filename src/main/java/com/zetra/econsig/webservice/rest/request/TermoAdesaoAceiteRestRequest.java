package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: UsuarioRestRequest</p>
 * <p>Description: Requisição Rest de dados do usuário.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TermoAdesaoAceiteRestRequest {
    public String tadCodigo;

    public Boolean aceite;

    public String deviceToken;

}
