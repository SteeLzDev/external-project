package com.zetra.econsig.dto.web;

import java.io.Serializable;

/**
 * <p>Title: SSOToken</p>
 * <p>Description: Objeto que representa response de autenticação SSO.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public class SSOToken implements Serializable {

        public String access_token;
        public String token_type;
        public String refresh_token;
        public String expires_in;
        public String scope;
        public String error;
        public String error_description;
        public String error_code;

        public SSOToken() {
        }

        public SSOToken(String access_token) {
            this.access_token = access_token;
        }

}