package com.zetra.econsig.helper.email;

import java.util.Date;

public class EmailAlteracaoOperacaoSensivel {
    private String usuNomeExecutor;
    private String usuLoginExecutor;
    private String usuEmailExecutor;
    private String usuNomeAutenticador;
    private String usuLoginAutenticador;
    private String operacao;
    private String motivoOperacao;
    private Date dataOperacao;
    private Date dataAlteracaoOperacao;

    public EmailAlteracaoOperacaoSensivel() {
    }

    public String getUsuNomeExecutor() {
        return usuNomeExecutor;
    }

    public void setUsuNomeExecutor(String usuNomeExecutor) {
        this.usuNomeExecutor = usuNomeExecutor;
    }

    public String getUsuLoginExecutor() {
        return usuLoginExecutor;
    }

    public void setUsuLoginExecutor(String usuLoginExecutor) {
        this.usuLoginExecutor = usuLoginExecutor;
    }

    public String getUsuEmailExecutor() {
        return usuEmailExecutor;
    }

    public void setUsuEmailExecutor(String usuEmailExecutor) {
        this.usuEmailExecutor = usuEmailExecutor;
    }

    public String getUsuNomeAutenticador() {
        return usuNomeAutenticador;
    }

    public void setUsuNomeAutenticador(String usuNomeAutenticador) {
        this.usuNomeAutenticador = usuNomeAutenticador;
    }

    public String getUsuLoginAutenticador() {
        return usuLoginAutenticador;
    }

    public void setUsuLoginAutenticador(String usuLoginAutenticador) {
        this.usuLoginAutenticador = usuLoginAutenticador;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getMotivoOperacao() {
        return motivoOperacao;
    }

    public void setMotivoOperacao(String motivoOperacao) {
        this.motivoOperacao = motivoOperacao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Date getDataAlteracaoOperacao() {
        return dataAlteracaoOperacao;
    }

    public void setDataAlteracaoOperacao(Date dataAlteracaoOperacao) {
        this.dataAlteracaoOperacao = dataAlteracaoOperacao;
    }
}
