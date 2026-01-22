package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: GerencialInadimplenciaCsaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial de Inadimplência Consignatária.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerencialInadimplenciaCsaBean  implements Serializable {
    private String csaNome;
    private String csaNomeAbrev;
    private Long csaQtdeInadimplencia;
    private Double csaPercInadimplencia;
    private BigDecimal csaSumInadimplencia;
    private BigDecimal csaSumAdeVlr;
    public String getCsaNome() {
        return csaNome;
    }
    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }
    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }
    public void setCsaNomeAbrev(String csaNomeAbrev) {
        this.csaNomeAbrev = csaNomeAbrev;
    }
    public Long getCsaQtdeInadimplencia() {
        return csaQtdeInadimplencia;
    }
    public void setCsaQtdeInadimplencia(Long csaQtdeInadimplencia) {
        this.csaQtdeInadimplencia = csaQtdeInadimplencia;
    }
    public Double getCsaPercInadimplencia() {
        return csaPercInadimplencia;
    }
    public void setCsaPercInadimplencia(Double csaPercInadimplencia) {
        this.csaPercInadimplencia = csaPercInadimplencia;
    }
    public BigDecimal getCsaSumInadimplencia() {
        return csaSumInadimplencia;
    }
    public void setCsaSumInadimplencia(BigDecimal csaSumInadimplencia) {
        this.csaSumInadimplencia = csaSumInadimplencia;
    }
    public BigDecimal getCsaSumAdeVlr() {
        return csaSumAdeVlr;
    }
    public void setCsaSumAdeVlr(BigDecimal csaSumAdeVlr) {
        this.csaSumAdeVlr = csaSumAdeVlr;
    }
}
