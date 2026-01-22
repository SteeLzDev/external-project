package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: ContratosPorCsaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ContratosPorCsaBean implements Serializable {

    private String consignataria;
    private String status;
    private Long quantidade;
    private BigDecimal vlrMensal;
    private Long quantidadeTotal;
    private BigDecimal vlrTotal;
    private String observacao;

    public String getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(String consignataria) {
        this.consignataria = consignataria;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Long getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Long quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public BigDecimal getVlrMensal() {
        return vlrMensal;
    }

    public void setVlrMensal(BigDecimal vlrMensal) {
        this.vlrMensal = vlrMensal;
    }

    public BigDecimal getVlrTotal() {
        return vlrTotal;
    }

    public void setVlrTotal(BigDecimal vlrTotal) {
        this.vlrTotal = vlrTotal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
