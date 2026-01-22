package com.zetra.econsig.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_funcao_altera_margem_ade")
@IdClass(FuncaoAlteraMargemAdeId.class)
public class FuncaoAlteraMargemAde {

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "fun_codigo", nullable = false, length = 32)
    private String funCodigo;

    @Id
    @Column(name = "pap_codigo", nullable = false, length = 32)
    private String papCodigo;

    @Id
    @Column(name = "mar_codigo_origem", nullable = false)
    private Short marCodigoOrigem;

    @Id
    @Column(name = "mar_codigo_destino", nullable = false)
    private Short marCodigoDestino;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_codigo", referencedColumnName = "fun_codigo", insertable = false, updatable = false)
    private Funcao funcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pap_codigo", referencedColumnName = "pap_codigo", insertable = false, updatable = false)
    private Papel papel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mar_codigo_origem", referencedColumnName = "mar_codigo", insertable = false, updatable = false)
    private Margem margemOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mar_codigo_destino", referencedColumnName = "mar_codigo", insertable = false, updatable = false)
    private Margem margemDestino;

    public String getFunCodigo() {
        return funCodigo;
    }

    public void setFunCodigo(String funCodigo) {
        this.funCodigo = funCodigo;
    }

    public String getPapCodigo() {
        return papCodigo;
    }

    public void setPapCodigo(String papCodigo) {
        this.papCodigo = papCodigo;
    }

    public Short getMarCodigoOrigem() {
        return marCodigoOrigem;
    }

    public void setMarCodigoOrigem(Short marCodigoOrigem) {
        this.marCodigoOrigem = marCodigoOrigem;
    }

    public Short getMarCodigoDestino() {
        return marCodigoDestino;
    }

    public void setMarCodigoDestino(Short marCodigoDestino) {
        this.marCodigoDestino = marCodigoDestino;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
        setFunCodigo(funcao != null ? funcao.getFunCodigo() : null);
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
        setPapCodigo(papel != null ? papel.getPapCodigo() : null);
    }

    public Papel getPapel() {
        return papel;
    }

    public void setMargemOrigem(Margem margemOrigem) {
        this.margemOrigem = margemOrigem;
        setMarCodigoOrigem(margemOrigem != null ? margemOrigem.getMarCodigo() : null);
    }

    public Margem getMargemOrigem() {
        return margemOrigem;
    }

    public void setMargemDestino(Margem margemDestino) {
        this.margemDestino = margemDestino;
        setMarCodigoDestino(margemDestino != null ? margemDestino.getMarCodigo() : null);
    }

    public Margem getMargemDestino() {
        return margemDestino;
    }


    public void setId(FuncaoAlteraMargemAdeId id) {
        setFunCodigo(id != null ? id.getFunCodigo() : null);
        setPapCodigo(id != null ? id.getPapCodigo() : null);
        setMarCodigoOrigem(id != null ? id.getMarCodigoOrigem() : null);
        setMarCodigoDestino(id != null ? id.getMarCodigoDestino() : null);
    }

}