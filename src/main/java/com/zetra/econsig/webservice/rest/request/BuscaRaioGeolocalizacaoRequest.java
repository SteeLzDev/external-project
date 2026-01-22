package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: BuscaRaioGeolocalizacaoRequest</p>
 * <p>Description: Requisição Rest com parâmetros de busca de geolocalização</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo $
 * $Revision: 25143 $
 * $Date: 2018-08-01 11:02:19 -0300 (qua, 01 ago 2018) $
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BuscaRaioGeolocalizacaoRequest {

    public Float latitude;

    public Float longitude;

    public String filtroTextoEntidade;

}
