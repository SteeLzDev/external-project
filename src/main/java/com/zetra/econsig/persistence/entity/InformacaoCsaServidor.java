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

/**
 * <p>Title: InformacaoCsaServidor</p>
 * <p>Description: Entidade de informacao csa servidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

@Entity
@Table(name = "tb_informacao_csa_servidor")
public class InformacaoCsaServidor implements Serializable{

    private static final long serialVersionUID = 1L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "ics_codigo", nullable = false, length = 32)
    private String icsCodigo;

    //--- ENTITY DATA FIELDS
    @Column(name = "ics_valor", nullable = false, length = 65535)
    private String icsValor;

    @Column(name = "ics_data", nullable = false)
    private Date icsData;

    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Column(name = "ser_codigo", nullable = false, length = 32)
    private String serCodigo;

    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Column(name = "ics_ip_acesso", length = 32)
    private String icsIpAcesso;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ser_codigo", referencedColumnName = "ser_codigo", insertable = false, updatable = false)
    private Servidor servidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    public String getIcsCodigo() {
        return icsCodigo;
    }

    public void setIcsCodigo(String icsCodigo) {
        this.icsCodigo = icsCodigo;
    }

    public String getIcsValor() {
        return icsValor;
    }

    public void setIcsValor(String icsValor) {
        this.icsValor = icsValor;
    }

    public Date getIcsData() {
        return icsData;
    }

    public void setIcsData(Date icsData) {
        this.icsData = icsData;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getSerCodigo() {
        return serCodigo;
    }

    public void setSerCodigo(String serCodigo) {
        this.serCodigo = serCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getIcsIpAcesso() {
        return icsIpAcesso;
    }

    public void setIcsIpAcesso(String icsIpAcesso) {
        this.icsIpAcesso = icsIpAcesso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Servidor getServidor() {
        return servidor;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    public Consignataria getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(Consignataria consignataria) {
        this.consignataria = consignataria;
    }



}
