package com.zetra.econsig.dto.web;

import com.zetra.econsig.webservice.rest.request.AuditTrail;
import com.zetra.econsig.webservice.rest.request.Authentication;
import com.zetra.econsig.webservice.rest.request.Consent;
import com.zetra.econsig.webservice.rest.request.Document;

public class ConsentRequestDTO {
    
   
    
        public String data_set;
        public String provider;
        public String source;
        public String hash_connection;
        public String channel;
        public Consent consent;
        public Authentication authentication;
        public AuditTrail audit_trail;
        public String consent_date;
        public String expire_date;
        public Document document;
        
    
    
}

