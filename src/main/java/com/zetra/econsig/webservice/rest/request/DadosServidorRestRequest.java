package com.zetra.econsig.webservice.rest.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: DadosServidorRestRequest</p>
 * <p>Description: Requisição Rest de dados do servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: andrea.giorgini $
 * $Revision: 26618 $
 * $Date: 2019-04-29 $
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DadosServidorRestRequest implements Serializable {

    private static final long serialVersionUID = 1772662355832818236L;

	// Dados do servidor
    public String telefone;
    public String email;
    public String celular;
    public String cpf;

    @Override
    public String toString() {
    	final StringBuilder retorno = new StringBuilder();
    	retorno.append(cpf != null ? cpf : "").append(";");
    	retorno.append(email != null ? email : "").append(";");
    	retorno.append(telefone != null ? telefone : "").append(";");
    	retorno.append(celular != null ? celular : "");
    	return retorno.toString();
    }
}
