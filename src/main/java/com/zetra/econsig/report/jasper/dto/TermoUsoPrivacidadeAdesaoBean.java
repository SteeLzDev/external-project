package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;

public class TermoUsoPrivacidadeAdesaoBean implements Serializable {
    
    private String usuNome;
    private String usuLogin;
    private String usuEmail;
    private String usuTel;
    private String usuCPF;
    private String stuDescricao;
    private String entidade;
    private String data;
    private String ipAcesso;
    private String aceitacaoVia;
    private String documentoAceito;
    
    public String getUsuNome() {
        return usuNome;
    }
    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }
    public String getUsuLogin() {
        return usuLogin;
    }
    public void setUsuLogin(String usuLogin) {
        this.usuLogin = usuLogin;
    }
    public String getUsuEmail() {
        return usuEmail;
    }
    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
    }
    public String getUsuTel() {
        return usuTel;
    }
    public void setUsuTel(String usuTel) {
        this.usuTel = usuTel;
    }
    public String getUsuCPF() {
        return usuCPF;
    }
    public void setUsuCPF(String usuCPF) {
        this.usuCPF = usuCPF;
    }
    public String getStuDescricao() {
        return stuDescricao;
    }
    public void setStuDescricao(String stuDescricao) {
        this.stuDescricao = stuDescricao;
    }
    public String getEntidade() {
        return entidade;
    }
    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getIpAcesso() {
        return ipAcesso;
    }
    public void setIpAcesso(String ipAcesso) {
        this.ipAcesso = ipAcesso;
    }
    public String getAceitacaoVia() {
        return aceitacaoVia;
    }
    public void setAceitacaoVia(String aceitacaoVia) {
        this.aceitacaoVia = aceitacaoVia;
    }
    public String getDocumentoAceito() {
        return documentoAceito;
    }
    public void setDocumentoAceito(String documentoAceito) {
        this.documentoAceito = documentoAceito;
    }
}
