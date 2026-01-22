package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_modelo_termo_aditivo")
public class ModeloTermoAditivo implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "mta_codigo", nullable = false, length = 32)
    private String mtaCodigo;

    @Column(name = "mta_descricao", nullable = false, length = 100)
    private String mtaDescricao;

    @Column(name = "mta_texto", nullable = false, length = 65535)
    private String mtaTexto;

    public ModeloTermoAditivo() {
        super();
    }

    public String getMtaCodigo() {
        return mtaCodigo;
    }

    public void setMtaCodigo(String mtaCodigo) {
        this.mtaCodigo = mtaCodigo;
    }

    public String getMtaDescricao() {
        return mtaDescricao;
    }

    public void setMtaDescricao(String mtaDescricao) {
        this.mtaDescricao = mtaDescricao;
    }

    public String getMtaTexto() {
        return mtaTexto;
    }

    public void setMtaTexto(String mtaTexto) {
        this.mtaTexto = mtaTexto;
    }

}
