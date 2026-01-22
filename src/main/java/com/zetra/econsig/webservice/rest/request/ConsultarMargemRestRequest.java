package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ConsultarMargemRestRequest</p>
 * <p>Description: Requisição Rest de consutar margem.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConsultarMargemRestRequest {

    public String nseCodigo;

    public String serCpf;

    public String rseCodigo;

    public String matricula;

    public String dataFimRetorno;
}
