package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name= "tb_anexo_consignataria")
public class AnexoConsignataria implements Serializable {
    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "axc_codigo", nullable = false, length = 32)
    private String axcCodigo;

    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Column(name = "tar_codigo", nullable = false, length = 32)
    private String tarCodigo;

    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Column(name = "axc_nome", nullable = false, length = 32)
    private String axcNome;

    @Column(name = "axc_ativo", nullable = false, length = 32)
    private Short axcAtivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "axc_data", nullable = false, length = 32)
    private Date axcData;

    @Column(name = "axc_ip_acesso", nullable = false, length = 32)
    private String axcIpAcesso;

    public String getAxcCodigo() {
        return axcCodigo;
    }

    //Gettters & Setters
    public void setAxcCodigo(String axcCodigo) {
        this.axcCodigo = axcCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getTarCodigo() {
        return tarCodigo;
    }

    public void setTarCodigo(String tarCodigo) {
        this.tarCodigo = tarCodigo;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getAxcNome() {
        return axcNome;
    }

    public void setAxcNome(String axcNome) {
        this.axcNome = axcNome;
    }

    public Short getAxcAtivo() {
        return axcAtivo;
    }

    public void setAxcAtivo(Short axcAtivo) {
        this.axcAtivo = axcAtivo;
    }

    public Date getAxcData() {
        return axcData;
    }

    public void setAxcData(Date axcData) {
        this.axcData = axcData;
    }

    public String getAxcIpAcesso() {
        return axcIpAcesso;
    }

    public void setAxcIpAcesso(String axcIpAcesso) {
        this.axcIpAcesso = axcIpAcesso;
    }

}
