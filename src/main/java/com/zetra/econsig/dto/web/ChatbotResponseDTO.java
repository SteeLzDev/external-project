package com.zetra.econsig.dto.web;

/**
 * <p>Title: ChatbotResponseDTO</p>
 * <p>Description: DTO que mapeia a resposta do agente do chatbot</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ChatbotResponseDTO {

    private String response;
    private boolean fallback;

    public ChatbotResponseDTO() {
        super();
    }

    public ChatbotResponseDTO(String response) {
        this();
        this.response = response;
    }

    public ChatbotResponseDTO(String response, boolean fallback) {
        this();
        this.response = response;
        this.fallback = fallback;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

}
