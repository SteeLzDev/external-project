package com.zetra.econsig.webservice.rest.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Setter;
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutorizarConsignatariaResponse {
	public String csaCodigo;
	public String csaNome;
	public String csaIdentificador;
	public String csaNomeAbrev;
	public String dataInicio;
	public String dataFim;
}
