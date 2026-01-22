package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * <p> Title: GerencialGeralTaxasBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Data
public class GerencialTaxasBean implements Serializable {

    private String consignataria;
    private BigDecimal vlr12;
    private BigDecimal vlr24;
    private BigDecimal vlr36;
    private BigDecimal vlr48;
    private BigDecimal vlr60;
    private BigDecimal vlr72;
    private BigDecimal vlr84;
    private BigDecimal vlr96;
    private BigDecimal vlr108;
    private BigDecimal vlr120;
    private BigDecimal vlr132;
    private BigDecimal vlr144;
    private String maxPrazo;

}
