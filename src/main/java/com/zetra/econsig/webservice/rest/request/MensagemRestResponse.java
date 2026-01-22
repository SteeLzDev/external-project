package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zetra.econsig.dto.TransferObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: UsuarioRestResponse</p>
 * <p>Description: Rest response com dados do usuario.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class MensagemRestResponse {
    public String menCodigo;
    public String funCodigo;
    public String menTitulo;
    public String menTexto;
    public String usuCodigo;
    public String menData;
    public Short menSequencia;
    public boolean menExibeCse;
    public boolean menExibeOrg;
    public boolean menExibeCsa;
    public boolean menExibeCor;
    public boolean menExibeSer;
    public boolean menExibeSup;
    public boolean menExigeLeitura;
    public boolean menHtml;
    public boolean menPermiteLerDepois;
    public boolean menNotificarCseLeitura;
    public boolean menBloqCsaSemLeitura;
    public boolean menPublica;
    public boolean menLidaIndividualmente;
    public boolean menLida;

    public MensagemRestResponse() {}
}
