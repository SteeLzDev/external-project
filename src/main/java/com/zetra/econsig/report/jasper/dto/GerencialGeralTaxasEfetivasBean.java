package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: GerencialGeralTaxasEfetivasBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerencialGeralTaxasEfetivasBean implements Serializable {

    private String csaCodigo;
    private String csaNomeAbrev;
    private String csaNome;
    private BigDecimal taxaMedia;

    public String getCsaCodigo() {
        return csaCodigo;
    }
    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }

    public void setCsaNomeAbrev(String csaNomeAbrev) {
        this.csaNomeAbrev = csaNomeAbrev;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public BigDecimal getTaxaMedia() {
        return taxaMedia;
    }

    public void setTaxaMedia(BigDecimal taxaMedia) {
        this.taxaMedia = taxaMedia;
    }
}