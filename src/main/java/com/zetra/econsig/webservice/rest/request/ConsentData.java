package com.zetra.econsig.webservice.rest.request;
import java.time.LocalDateTime;

public class ConsentData {

    public String data_set;
    public String provider;
    public String source;
    public String hash_connection;
    public String channel;
    public Consent consent;
    public Authentication authentication;
    public AuditTrail audit_trail;
    public LocalDateTime consent_date;
    public LocalDateTime expire_date;
    public Document document;
    
}
