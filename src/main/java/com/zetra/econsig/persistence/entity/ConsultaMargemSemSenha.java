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

@Entity
@Table(name = "tb_consulta_margem_sem_senha")
public class ConsultaMargemSemSenha implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "css_codigo", nullable = false, length = 32)
    private String cssCodigo;

    //--- ENTITY DATA FIELDS
    @Column(name = "rse_codigo", nullable = false, length = 32)
    private String rseCodigo;

    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Column(name = "css_data_ini", nullable = false)
    private Date cssDataIni;

    @Column(name = "css_data_fim", nullable = false)
    private Date cssDataFim;

    @Column(name = "css_data_revogacao_sup")
    private Date cssDataRevogacaoSup;

    @Column(name = "css_data_revogacao_ser")
    private Date cssDataRevogacaoSer;

    @Column(name = "css_data_alerta")
    private Date cssDataAlerta;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rse_codigo", referencedColumnName = "rse_codigo", insertable = false, updatable = false)
    private RegistroServidor registroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    /**
     * Constructor
     */
    public ConsultaMargemSemSenha() {
    }

    public ConsultaMargemSemSenha(String cssCodigo) {
        this();
        this.cssCodigo = cssCodigo;
    }

    //--- GETTERS & SETTERS FOR FIELDS
    public String getCssCodigo() {
        return cssCodigo;
    }

    public void setCssCodigo(String cssCodigo) {
        this.cssCodigo = cssCodigo;
    }

    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCcodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public Date getCssDataIni() {
        return cssDataIni;
    }

    public void setCssDataIni(Date cssDataIni) {
        this.cssDataIni = cssDataIni;
    }

    public Date getCssDataFim() {
        return cssDataFim;
    }

    public void setCssDataFim(Date cssDataFim) {
        this.cssDataFim = cssDataFim;
    }

    public Date getCssDataRevogacaoSup() {
        return cssDataRevogacaoSup;
    }

    public void setCssDataRevogacaoSup(Date cssDataRevogacaoSup) {
        this.cssDataRevogacaoSup = cssDataRevogacaoSup;
    }

    public Date getCssDataRevogacaoSer() {
        return cssDataRevogacaoSer;
    }

    public void setCssDataRevogacaoSer(Date cssDataRevogacaoSer) {
        this.cssDataRevogacaoSer = cssDataRevogacaoSer;
    }

    public Date getCssDataAlerta() {
        return cssDataAlerta;
    }

    public void setCssDataAlerta(Date cssDataAlerta) {
        this.cssDataAlerta = cssDataAlerta;
    }

    public RegistroServidor getRegistroServidor() {
        return registroServidor;
    }

    public void setRegistroServidor(RegistroServidor registroServidor) {
        this.registroServidor = registroServidor;
    }

    public Consignataria getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(Consignataria consignataria) {
        this.consignataria = consignataria;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
