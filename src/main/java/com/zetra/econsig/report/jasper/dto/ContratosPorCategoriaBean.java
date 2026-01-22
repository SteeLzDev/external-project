package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;

/**
 * <p> Title: ContratosPorCategoriaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ContratosPorCategoriaBean implements Serializable {

    private String rseTipo;
    private Long quantidade;

    public String getRseTipo() {
        return rseTipo;
    }

    public void setRseTipo(String rseTipo) {
        this.rseTipo = rseTipo;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

}
