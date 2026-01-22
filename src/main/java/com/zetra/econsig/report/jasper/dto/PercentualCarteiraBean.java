package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: PercentualCarteiraBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Percentual Carteira.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PercentualCarteiraBean implements Serializable {

    private String csa;
    private String csaId;
    private String csaCodigo;
    private String csaIdentificador;
    private BigDecimal percTotal;
    private BigDecimal percQtde;
    private BigDecimal percParcela;

    public String getCsa() {
        return csa;
    }

    public void setCsa(String csa) {
        this.csa = csa;
    }

    public String getCsaId() {
        return csaId;
    }

    public void setCsaId(String csaId) {
        this.csaId = csaId;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public void setCsaIdentificador(String csaIdentificador) {
        this.csaIdentificador = csaIdentificador;
    }

    public BigDecimal getPercTotal() {
        return percTotal;
    }

    public void setPercTotal(BigDecimal percTotal) {
        this.percTotal = percTotal;
    }

    public BigDecimal getPercQtde() {
        return percQtde;
    }

    public void setPercQtde(BigDecimal percQtde) {
        this.percQtde = percQtde;
    }

    public BigDecimal getPercParcela() {
        return percParcela;
    }

    public void setPercParcela(BigDecimal percParcela) {
        this.percParcela = percParcela;
    }
}
