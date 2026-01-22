package com.zetra.econsig.webservice.rest.request;

import java.util.List;

public class Consent {
    public Integer term_version;
    public List<UsagePurpose> usage_purpose;
    public String consent_url;
}
