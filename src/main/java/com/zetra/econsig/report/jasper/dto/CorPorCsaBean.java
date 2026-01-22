package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;

/**
 * <p> Title: CorPorCsaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CorPorCsaBean implements Serializable {

    private String consignataria;
    private Long quantidade;

    public String getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(String consignataria) {
        this.consignataria = consignataria;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

}
