package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;

/**
 * <p> Title: InadimplenciaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Inadimplência.</p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class InadimplenciaBean implements Serializable {

    private String descricao;
    private Long quantidade;
    private Double valor1;

    public InadimplenciaBean() {
        descricao = new String();
        quantidade = Long.valueOf(0);
        valor1 = Double.valueOf(0);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValor1() {
        return valor1;
    }

    public void setValor1(Double valor1) {
        this.valor1 = valor1;
    }
}
