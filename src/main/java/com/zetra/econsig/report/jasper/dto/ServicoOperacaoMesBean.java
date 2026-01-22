package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: ServicoOperacaoMesBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Operações no Mês.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServicoOperacaoMesBean implements Serializable {

    private String csaNome;
    private String csaCodigo;
    private String consignataria;
    private String servico;
    private BigDecimal ativoInicioMes;
    private BigDecimal ativoFimMes;
    private BigDecimal quitados;
    private BigDecimal renegociados;
    private BigDecimal novos;
    private BigDecimal totalValorDescontatoMes;
    private BigDecimal participacaoTotalDescontado;
    private BigDecimal participacaoTotalServidores;
    private BigDecimal retencaoGoverno;

    public String getCsaNome() {
        return csaNome;
    }
    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }
    public String getCsaCodigo() {
        return csaCodigo;
    }
    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }
    public String getConsignataria() {
        return consignataria;
    }
    public void setConsignataria(String consignataria) {
        this.consignataria = consignataria;
    }
    public String getServico() {
        return servico;
    }
    public void setServico(String servico) {
        this.servico = servico;
    }
    public BigDecimal getAtivoInicioMes() {
        return ativoInicioMes;
    }
    public void setAtivoInicioMes(BigDecimal ativoInicioMes) {
        this.ativoInicioMes = ativoInicioMes;
    }
    public BigDecimal getAtivoFimMes() {
        return ativoFimMes;
    }
    public void setAtivoFimMes(BigDecimal ativoFimMes) {
        this.ativoFimMes = ativoFimMes;
    }
    public BigDecimal getQuitados() {
        return quitados;
    }
    public void setQuitados(BigDecimal quitados) {
        this.quitados = quitados;
    }
    public BigDecimal getRenegociados() {
        return renegociados;
    }
    public void setRenegociados(BigDecimal renegociados) {
        this.renegociados = renegociados;
    }
    public BigDecimal getNovos() {
        return novos;
    }
    public void setNovos(BigDecimal novos) {
        this.novos = novos;
    }
    public BigDecimal getTotalValorDescontatoMes() {
        return totalValorDescontatoMes;
    }
    public void setTotalValorDescontatoMes(BigDecimal totalValorDescontatoMes) {
        this.totalValorDescontatoMes = totalValorDescontatoMes;
    }
    public BigDecimal getParticipacaoTotalDescontado() {
        return participacaoTotalDescontado;
    }
    public void setParticipacaoTotalDescontado(BigDecimal participacaoTotalDescontado) {
        this.participacaoTotalDescontado = participacaoTotalDescontado;
    }
    public BigDecimal getParticipacaoTotalServidores() {
        return participacaoTotalServidores;
    }
    public void setParticipacaoTotalServidores(BigDecimal participacaoTotalServidores) {
        this.participacaoTotalServidores = participacaoTotalServidores;
    }
    public BigDecimal getRetencaoGoverno() {
        return retencaoGoverno;
    }
    public void setRetencaoGoverno(BigDecimal retencaoGoverno) {
        this.retencaoGoverno = retencaoGoverno;
    }
}
