package com.zetra.econsig.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name= "tb_vinculo_consignataria")
public class VinculoConsignataria implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "vcs_codigo", nullable = false, length = 32)
    private String vcsCodigo;

    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Column(name = "vcs_identificador", nullable = false, length = 40)
    private String vcsIdentificador;

    @Column(name = "vcs_descricao", nullable = false)
    private String vcsDescricao;

    @Column(name = "vcs_ativo", nullable = false, length = 32 )
    private Short vcsAtivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vcs_data_criacao", nullable = false, length = 32)
    private Date vcsDataCriacao;

    public String getVcsCodigo() {
        return vcsCodigo;
    }

    public void setVcsCodigo(String vcsCodigo) {
        this.vcsCodigo = vcsCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getVcsIdentificador() {
        return vcsIdentificador;
    }

    public void setVcsIdentificador(String vcsIdentificador) {
        this.vcsIdentificador = vcsIdentificador;
    }

    public String getVcsDescricao() {
        return vcsDescricao;
    }

    public void setVcsDescricao(String vcsDescricao) {
        this.vcsDescricao = vcsDescricao;
    }

    public Short getVcsAtivo() {
        return vcsAtivo;
    }

    public void setVcsAtivo(Short vcsAtivo) {
        this.vcsAtivo = vcsAtivo;
    }

    public Date getVcsDataCriacao() {
        return vcsDataCriacao;
    }

    public void setVcsDataCriacao(Date vcsDataCriacao) {
        this.vcsDataCriacao = vcsDataCriacao;
    }
}

