package com.zetra.econsig.webservice.rest.request;

import java.util.List;

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
public class ListarConsigacaoRestRequest extends ShowRestRequest {
    public String nseCodigo;
    public String usuCodigo;
    public String IniData;
    public String FimData;
    public String serCpf;
    public Long adeNumero;
    public String rseCodigo;
    public String matricula;
    public List<String> sadCodigos;
}
