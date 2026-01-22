package com.zetra.econsig.webclient.util;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RestTemplateFactory</p>
 * <p>Description: Retorna instancias de RestTemplate.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public class RestTemplateFactory {

    public static RestTemplate getRestTemplate (AcessoSistema responsavel) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setErrorHandler(new EconsigResponseErrorHandler(responsavel));
        return restTemplate;
    }
}
