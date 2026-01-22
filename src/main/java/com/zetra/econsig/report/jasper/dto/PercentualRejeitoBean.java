package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: PercentualRejeitoBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Percentual Rejeito.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PercentualRejeitoBean implements Serializable {

    private String periodo;
    private String csaCodigo;
    private String csaId;
    private String csaIdentificador;
    private String csa;
    private BigDecimal liquidadas;
    private BigDecimal rejeitadas;
    private BigDecimal percRejeito;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCsa() {
        return csa;
    }

    public void setCsa(String csa) {
        this.csa = csa;
    }

    public BigDecimal getLiquidadas() {
        return liquidadas;
    }

    public void setLiquidadas(BigDecimal liquidadas) {
        this.liquidadas = liquidadas;
    }

    public BigDecimal getRejeitadas() {
        return rejeitadas;
    }

    public void setRejeitadas(BigDecimal rejeitadas) {
        this.rejeitadas = rejeitadas;
    }

    public BigDecimal getPercRejeito() {
        return percRejeito;
    }

    public void setPercRejeito(BigDecimal percRejeito) {
        this.percRejeito = percRejeito;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCsaId() {
        return csaId;
    }

    public void setCsaId(String csaId) {
        this.csaId = csaId;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public void setCsaIdentificador(String csaIdentificador) {
        this.csaIdentificador = csaIdentificador;
    }
}
