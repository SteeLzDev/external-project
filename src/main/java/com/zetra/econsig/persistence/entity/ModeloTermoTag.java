package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_modelo_termo_tag")
public class ModeloTermoTag implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "mtt_codigo", nullable = false, length = 32)
    private String mttCodigo;

    @Column(name = "mta_codigo", nullable = false, length = 32)
    private String mtaCodigo;

    @Column(name = "mtt_tag", nullable = false, length = 100)
    private String mttTag;

    @Column(name = "mtt_valor", nullable = false, length = 255)
    private String mttValor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mta_codigo", referencedColumnName = "mta_codigo", insertable = false, updatable = false)
    private ModeloTermoAditivo modeloTermoAditivo;

    public ModeloTermoTag() {
        super();
    }

    public String getMttCodigo() {
        return mttCodigo;
    }

    public void setMttCodigo(String mttCodigo) {
        this.mttCodigo = mttCodigo;
    }

    public String getMtaCodigo() {
        return mtaCodigo;
    }

    public void setMtaCodigo(String mtaCodigo) {
        this.mtaCodigo = mtaCodigo;
    }

    public String getMttTag() {
        return mttTag;
    }

    public void setMttTag(String mttTag) {
        this.mttTag = mttTag;
    }

    public String getMttValor() {
        return mttValor;
    }

    public void setMttValor(String mttValor) {
        this.mttValor = mttValor;
    }

    public ModeloTermoAditivo getModeloTermoAditivo() {
        return modeloTermoAditivo;
    }

    public void setModeloTermoAditivo(ModeloTermoAditivo modeloTermoAditivo) {
        this.modeloTermoAditivo = modeloTermoAditivo;
    }
}
