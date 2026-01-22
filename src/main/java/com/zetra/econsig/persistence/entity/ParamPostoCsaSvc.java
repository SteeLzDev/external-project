package com.zetra.econsig.persistence.entity;


import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tb_param_posto_csa_svc")
@IdClass(ParamPostoCsaSvcId.class)
public class ParamPostoCsaSvc implements Serializable {

    private static final long serialVersionUID = 2L;
    @Id
    @Column(name = "pos_codigo", nullable = false, length = 32)
    private String posCodigo;
    @Id
    @Column(name = "tps_codigo", nullable = false, length = 32)
    private String tpsCodigo;
    @Id
    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;
    @Id
    @Column(name = "svc_codigo", nullable = false, length = 32)
    private String svcCodigo;

    @Column(name = "ppo_vlr", nullable = false, length = 32)
    private String ppoVlr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_codigo", referencedColumnName = "pos_codigo", insertable = false, updatable = false)
    private PostoRegistroServidor postoRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tps_codigo", referencedColumnName = "tps_codigo", insertable = false, updatable = false)
    private TipoParamSvc tipoParamSvc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "svc_codigo", referencedColumnName = "svc_codigo", insertable = false, updatable = false)
    private Servico servico;

    public PostoRegistroServidor getPostoRegistroServidor() {
        return postoRegistroServidor;
    }

    public void setPostoRegistroServidor(PostoRegistroServidor postoRegistroServidor) {
        this.postoRegistroServidor = postoRegistroServidor;
    }

    public TipoParamSvc getTipoParamSvc() {
        return tipoParamSvc;
    }

    public void setTipoParamSvc(TipoParamSvc tipoParamSvc) {
        this.tipoParamSvc = tipoParamSvc;
    }

    public Consignataria getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(Consignataria consignataria) {
        this.consignataria = consignataria;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public ParamPostoCsaSvc() {
        super();
    }

    public String getPosCodigo() {
        return posCodigo;
    }

    public void setPosCodigo(String posCodigo) {
        this.posCodigo = posCodigo;
    }

    public String getTpsCodigo() {
        return tpsCodigo;
    }

    public void setTpsCodigo(String tpsCodigo) {
        this.tpsCodigo = tpsCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public String getPpoVlr() {
        return ppoVlr;
    }

    public void setPpoVlr(String pcsValor) {
        this.ppoVlr = pcsValor;
    }

}
