package com.zetra.econsig.webservice.rest.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: MobileTextoSistemaRestRequest</p>
 * <p>Description: Requisição Rest de mensagens do Mobile</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MobileMessageRestRequest {

    public String projectVersion;
    public String dataUltimaAlteracao;

    public List<TextoSistemaRestRequest> mensagensFormatadas;

    public void setMensagens(List<Map<String, String>> mensagens) {

        mensagensFormatadas =  mensagens.stream().map(o -> {return new TextoSistemaRestRequest(o.get("texChave"), o.get("texTexto"), o.get("texDataAlteracao"));})
                .collect(Collectors.toList());
    }

}
