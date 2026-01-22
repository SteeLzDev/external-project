package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "tb_historico_login")
public class HistoricoLogin implements Serializable{

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hlo_codigo", nullable = false)
    private Long hloCodigo;

    //--- ENTITY DATA FIELDS
    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "hlo_data", nullable = false)
    private Date hloData;

    @Column(name = "hlo_canal", nullable = false, length = 1)
    private String hloCanal;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;

    /**
     * Constructor
     */
    public HistoricoLogin() {
        super();
    }

    //--- GETTERS & SETTERS FOR FIELDS
    public Long getHloCodigo() {
        return hloCodigo;
    }

    public void setHloCodigo(Long hloCodigo) {
        this.hloCodigo = hloCodigo;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public Date getHloData() {
        return hloData;
    }

    public void setHloData(Date hloData) {
        this.hloData = hloData;
    }

    public String getHloCanal() {
        return hloCanal;
    }

    public void setHloCanal(String hloCanal) {
        this.hloCanal = hloCanal;
    }

    //--- GETTERS FOR LINKS
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        setUsuCodigo(usuario != null ? usuario.getUsuCodigo() : null);
    }

}