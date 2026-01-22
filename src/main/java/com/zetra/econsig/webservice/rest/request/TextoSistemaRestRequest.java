package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: TextoSistemaRestRequest</p>
 * <p>Description: Representação Rest de TextoSistema vindo do Mobile</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class TextoSistemaRestRequest {

    public String texChave;
    public String texTexto;
    public String texDataAlteracao;

    public TextoSistemaRestRequest(String texChave, String texTexto, String texDataAlteracao) {
        super();
        this.texChave = texChave;
        this.texTexto = texTexto;
        this.texDataAlteracao = texDataAlteracao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextoSistemaRestRequest)) {
            return false;
        }
        TextoSistemaRestRequest other = (TextoSistemaRestRequest)o;

        return texChave.equals(other.texChave) && texTexto.equals(other.texTexto);
    }

}
