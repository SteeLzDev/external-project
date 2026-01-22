package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p> Title: GerencialInadimplenciaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial de Inadimplência.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerencialInadimplenciaBean implements Serializable {

    private BigDecimal totalCarteira;
    private Long qtdeTotalCarteira;
    private BigDecimal inadimplenciaTotal;
    private Long qtdeInadimplenciaTotal;
    private BigDecimal totalCarteiraEmprestimo;
    private Long qtdeTotalCarteiraEmprestimo;
    private BigDecimal inadimplenciaEmprestimo;
    private Long qtdeInadimplenciaEmprestimo;
    private List<GerencialInadimplenciaCsaBean> qtdeInadimplenciaCsa;

    public BigDecimal getTotalCarteira() {
        return totalCarteira;
    }

    public void setTotalCarteira(BigDecimal totalCarteira) {
        this.totalCarteira = totalCarteira;
    }

    public Long getQtdeTotalCarteira() {
        return qtdeTotalCarteira;
    }

    public void setQtdeTotalCarteira(Long qtdeTotalCarteira) {
        this.qtdeTotalCarteira = qtdeTotalCarteira;
    }

    public BigDecimal getInadimplenciaTotal() {
        return inadimplenciaTotal;
    }

    public void setInadimplenciaTotal(BigDecimal inadimplenciaTotal) {
        this.inadimplenciaTotal = inadimplenciaTotal;
    }

    public Long getQtdeInadimplenciaTotal() {
        return qtdeInadimplenciaTotal;
    }

    public void setQtdeInadimplenciaTotal(Long qtdeInadimplenciaTotal) {
        this.qtdeInadimplenciaTotal = qtdeInadimplenciaTotal;
    }

    public BigDecimal getTotalCarteiraEmprestimo() {
        return totalCarteiraEmprestimo;
    }

    public void setTotalCarteiraEmprestimo(BigDecimal totalCarteiraEmprestimo) {
        this.totalCarteiraEmprestimo = totalCarteiraEmprestimo;
    }

    public Long getQtdeTotalCarteiraEmprestimo() {
        return qtdeTotalCarteiraEmprestimo;
    }

    public void setQtdeTotalCarteiraEmprestimo(Long qtdeTotalCarteiraEmprestimo) {
        this.qtdeTotalCarteiraEmprestimo = qtdeTotalCarteiraEmprestimo;
    }

    public BigDecimal getInadimplenciaEmprestimo() {
        return inadimplenciaEmprestimo;
    }

    public void setInadimplenciaEmprestimo(BigDecimal inadimplenciaEmprestimo) {
        this.inadimplenciaEmprestimo = inadimplenciaEmprestimo;
    }

    public Long getQtdeInadimplenciaEmprestimo() {
        return qtdeInadimplenciaEmprestimo;
    }

    public void setQtdeInadimplenciaEmprestimo(Long qtdeInadimplenciaEmprestimo) {
        this.qtdeInadimplenciaEmprestimo = qtdeInadimplenciaEmprestimo;
    }

    public List<GerencialInadimplenciaCsaBean> getQtdeInadimplenciaCsa() {
        return qtdeInadimplenciaCsa;
    }

    public void setQtdeInadimplenciaCsa(List<GerencialInadimplenciaCsaBean> csaInadimplencia) {
        qtdeInadimplenciaCsa = csaInadimplencia;
    }
}
