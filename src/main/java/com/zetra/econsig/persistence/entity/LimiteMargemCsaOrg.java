package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "tb_limite_margem_csa_org")
@IdClass(LimiteMargemCsaOrgId.class)
public class LimiteMargemCsaOrg implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "mar_codigo", nullable = false)
    private Short marCodigo;

    @Id
    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Id
    @Column(name = "org_codigo", nullable = false, length = 32)
    private String orgCodigo;

    @Column(name = "lmc_valor", nullable = false)
    private BigDecimal lmcValor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lmc_data", nullable = false)
    private Date lmcData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mar_codigo", referencedColumnName = "mar_codigo", insertable = false, updatable = false)
    private Margem margem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_codigo", referencedColumnName = "org_codigo", insertable = false, updatable = false)
    private Orgao orgao;

    public LimiteMargemCsaOrg() {
        super();
    }

    public Short getMarCodigo() {
        return marCodigo;
    }

    public void setMarCodigo(Short marCodigo) {
        this.marCodigo = marCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    public BigDecimal getLmcValor() {
        return lmcValor;
    }

    public void setLmcValor(BigDecimal lmcValor) {
        this.lmcValor = lmcValor;
    }

    public Date getLmcData() {
        return lmcData;
    }

    public void setLmcData(Date lmcData) {
        this.lmcData = lmcData;
    }

    public Margem getMargem() {
        return margem;
    }

    public void setMargem(Margem margem) {
        this.margem = margem;
    }

    public Consignataria getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(Consignataria consignataria) {
        this.consignataria = consignataria;
    }

    public Orgao getOrgao() {
        return orgao;
    }

    public void setOrgao(Orgao orgao) {
        this.orgao = orgao;
    }

    public void setId(LimiteMargemCsaOrgId id) {
        setMarCodigo(id != null ? id.getMarCodigo() : null);
        setCsaCodigo(id != null ? id.getCsaCodigo() : null);
        setOrgCodigo(id != null ? id.getOrgCodigo() : null);
    }
}
