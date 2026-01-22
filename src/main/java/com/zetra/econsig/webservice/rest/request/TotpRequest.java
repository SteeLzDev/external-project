package com.zetra.econsig.webservice.rest.request;

public class TotpRequest {
    private String codeTotp;
    private int qtdTentativas;

   
    public String getCodeTotp() {
        return codeTotp;
    }

    public void setCodeTotp(String codeTotp) {
        this.codeTotp = codeTotp;
    }

	public int getQtdTentativas() {
		return qtdTentativas;
	}

	public void setQtdTentativas(int qtdTentativas) {
		this.qtdTentativas = qtdTentativas;
	}
}
