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
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Title: SaldoDevedorServidor</p>
 * <p>Description: Entidade de saldo devedor servidor
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

@Entity
@Table(name = "tb_saldo_devedor_rse")
@IdClass(SaldoDevedorServidorId.class)
@Getter
@Setter
public class SaldoDevedorServidor implements Serializable{

    private static final long serialVersionUID = 2L;

    //--- ENTITY DATA FIELDS
    @Id
    @Column(name = "rse_codigo", nullable = false, length = 32)
    private String rseCodigo;

    @Column(name = "sdr_valor", nullable = false)
    private BigDecimal sdrValor;

    @Id
    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sdr_data", nullable = false)
    private Date sdrData;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rse_codigo", referencedColumnName = "rse_codigo", insertable = false, updatable = false)
    private RegistroServidor registroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    public void setId(SaldoDevedorServidorId id) {
        setRseCodigo(id != null ? id.getRseCodigo() : null);
        setCsaCodigo(id != null ? id.getCsaCodigo() : null);
    }

}
