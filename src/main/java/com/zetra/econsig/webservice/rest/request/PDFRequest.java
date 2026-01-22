package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDFRequest {
    // Getter e Setter
    private List<String> chunks;
    private List<CsaListInfoRequest> inforCsas;
    private String serEmail;
}
