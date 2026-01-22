package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: ContratosPorSvcBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ContratosPorSvcBean implements Serializable {

    private String cnvCodVerba;
    private String consignataria;
    private String svcDescricao;
    private Long quantidade;
    private BigDecimal vlrMensal;
    private BigDecimal vlrTotal;
    private BigDecimal vlrTotalGeral;
    private BigDecimal vlrPorcentagemSvc;
    private BigDecimal qtdPorcentagemSvc;

    public String getCnvCodVerba() {
        return cnvCodVerba;
    }

    public void setCnvCodVerba(String cnvCodVerba) {
        this.cnvCodVerba = cnvCodVerba;
    }

    public String getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(String consignataria) {
        this.consignataria = consignataria;
    }

    public String getSvcDescricao() {
        return svcDescricao;
    }

    public void setSvcDescricao(String svcDescricao) {
        this.svcDescricao = svcDescricao;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
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

    public BigDecimal getVlrTotalGeral() {
        return vlrTotalGeral;
    }

    public void setVlrTotalGeral(BigDecimal vlrTotalGeral) {
        this.vlrTotalGeral = vlrTotalGeral;
    }

    public BigDecimal getVlrPorcentagemSvc() {
        return vlrPorcentagemSvc;
    }

    public void setVlrPorcentagemSvc(BigDecimal vlrPorcentagemSvc) {
        this.vlrPorcentagemSvc = vlrPorcentagemSvc;
    }

    public BigDecimal getQtdPorcentagemSvc() {
        return qtdPorcentagemSvc;
    }

    public void setQtdPorcentagemSvc(BigDecimal long1) {
        qtdPorcentagemSvc = long1;
    }

}
