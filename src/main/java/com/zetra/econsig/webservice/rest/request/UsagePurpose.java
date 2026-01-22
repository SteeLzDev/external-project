package com.zetra.econsig.webservice.rest.request;

public class UsagePurpose{
    public String treatment;
    public Boolean accepted;
    
    public UsagePurpose( String treatment, Boolean accepted){
      
        this.treatment = treatment;
        this.accepted = accepted;
    }

}