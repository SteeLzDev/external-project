package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: DadosColaboradorRestRequest</p>
 * <p>Description: Requisição Rest de dados do colaborador.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DadosColaboradorRestRequest {

    // Dados do servidor
    public String cpf;
    public String nomeColaborador;
    public String nomeMae;
    public String dataNascimento;
    public String telefoneCelular;

    public String otp;

}
