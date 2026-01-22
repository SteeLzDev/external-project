package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: OAuth2UrlResponse</p>
 * <p>Description: Rest response com dados do usuario a partir do OAuth2.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class OAuth2UrlResponse {
    public String urlAuthentication;
    public String redirectUri;
    public String tokenUrl;

    public OAuth2UrlResponse() {}
}
