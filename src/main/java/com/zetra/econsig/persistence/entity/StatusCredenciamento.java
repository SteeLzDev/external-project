package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity class for "StatusCredenciamento"
 *
 * @author Telosys
 *
 */
@Entity
@Table(name = "tb_status_credenciamento")
public class StatusCredenciamento implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "scr_codigo", nullable = false, length = 32)
    private String scrCodigo;

    //--- ENTITY DATA FIELDS
    @Column(name = "scr_descricao", nullable = false, length = 40)
    private String scrDescricao;

    /**
     * Constructor
     */
    public StatusCredenciamento() {
        super();
    }

    //--- GETTERS & SETTERS FOR FIELDS
    public String getScrCodigo() {
        return scrCodigo;
    }

    public void setScrCodigo(String scrCodigo) {
        this.scrCodigo = scrCodigo;
    }

    public String getScrDescricao() {
        return scrDescricao;
    }

    public void setScrDescricao(String scrDescricao) {
        this.scrDescricao = scrDescricao;
    }
}
