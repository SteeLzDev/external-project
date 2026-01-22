package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "tb_credenciamento_csa")
public class CredenciamentoCsa implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "cre_codigo", nullable = false, length = 32)
    private String creCodigo;

    //--- ENTITY DATA FIELDS
    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Column(name = "scr_codigo", nullable = false, length = 32)
    private String scrCodigo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cre_data_ini", nullable = false)
    private Date creDataIni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cre_data_fim")
    private Date creDataFim;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scr_codigo", referencedColumnName = "scr_codigo", insertable = false, updatable = false)
    private StatusCredenciamento statusCredenciamento;

    /**
     * Constructor
     */
    public CredenciamentoCsa() {
        super();
    }

    public CredenciamentoCsa(String creCodigo) {
        this();
        this.creCodigo = creCodigo;
    }

    //--- GETTERS & SETTERS FOR FIELDS

    public String getCreCodigo() {
        return creCodigo;
    }

    public void setCreCodigo(String creCodigo) {
        this.creCodigo = creCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getScrCodigo() {
        return scrCodigo;
    }

    public void setScrCodigo(String scrCodigo) {
        this.scrCodigo = scrCodigo;
    }

    public Date getCreDataIni() {
        return creDataIni;
    }

    public void setCreDataIni(Date creDataIni) {
        this.creDataIni = creDataIni;
    }

    public Date getCreDataFim() {
        return creDataFim;
    }

    public void setCreDataFim(Date creDataFim) {
        this.creDataFim = creDataFim;
    }

    public StatusCredenciamento getStatusCredenciamento() {
        return statusCredenciamento;
    }

    public void setStatusCredenciamento(StatusCredenciamento statusCredenciamento) {
        this.statusCredenciamento = statusCredenciamento;
    }

    public Consignataria getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(Consignataria consignataria) {
        this.consignataria = consignataria;
    }
}
