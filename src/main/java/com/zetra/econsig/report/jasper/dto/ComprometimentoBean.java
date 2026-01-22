package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: ComprometimentoBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComprometimentoBean implements Serializable {

    private String faixa;
    private BigDecimal qtde;
    private BigDecimal percentual;
    private BigDecimal margemUsada;
    private BigDecimal margemTotal;

    public String getFaixa() {
        return faixa;
    }

    public void setFaixa(String faixa) {
        this.faixa = faixa;
    }

    public BigDecimal getQtde() {
        return qtde;
    }

    public void setQtde(BigDecimal qtde) {
        this.qtde = qtde;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getMargemUsada() {
        return margemUsada;
    }

    public void setMargemUsada(BigDecimal margemUsada) {
        this.margemUsada = margemUsada;
    }

    public BigDecimal getMargemTotal() {
        return margemTotal;
    }

    public void setMargemTotal(BigDecimal margemTotal) {
        this.margemTotal = margemTotal;
    }
}
