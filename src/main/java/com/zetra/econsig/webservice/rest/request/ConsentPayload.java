package com.zetra.econsig.webservice.rest.request;

import java.time.LocalDateTime;

public class ConsentPayload {
    
    public String hashConnection;
    public Integer termVersion;  
    public LocalDateTime consentDate;
    public String dataSet;
}
