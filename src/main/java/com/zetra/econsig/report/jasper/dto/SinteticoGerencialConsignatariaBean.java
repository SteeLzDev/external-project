package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p> Title: ContratosPorCsaBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SinteticoGerencialConsignatariaBean implements Serializable {

    private String consignataria;
    private String status;
    private String simbolo;
    private Long quantidade;
    private Long quantidadeApi;
    private BigDecimal vlrMensal;
    private BigDecimal vlrTotal;
    private BigDecimal percentual;
    private Date dataInicio;
    private Date dataFim;
    private String tipoContrato;
    private String nome;
    private BigDecimal enviadoDesconto;
    private BigDecimal descontoEfetuado;
    private BigDecimal descontoParcial;
    private BigDecimal descontoNaoEfetuado;
    private BigDecimal inadimplencia;
    private String natureza;
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
    
    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Long getQuantidadeApi() {
        return quantidadeApi;
    }

    public void setQuantidadeApi(Long quantidadeApi) {
        this.quantidadeApi = quantidadeApi;
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

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getEnviadoDesconto() {
        return enviadoDesconto;
    }

    public void setEnviadoDesconto(BigDecimal enviadoDesconto) {
        this.enviadoDesconto = enviadoDesconto;
    }

    public BigDecimal getDescontoEfetuado() {
        return descontoEfetuado;
    }

    public void setDescontoEfetuado(BigDecimal descontoEfetuado) {
        this.descontoEfetuado = descontoEfetuado;
    }

    public BigDecimal getDescontoParcial() {
        return descontoParcial;
    }

    public void setDescontoParcial(BigDecimal descontoParcial) {
        this.descontoParcial = descontoParcial;
    }

    public BigDecimal getDescontoNaoEfetuado() {
        return descontoNaoEfetuado;
    }

    public void setDescontoNaoEfetuado(BigDecimal descontoNaoEfetuado) {
        this.descontoNaoEfetuado = descontoNaoEfetuado;
    }

	public BigDecimal getInadimplencia() {
		return inadimplencia;
	}

	public void setInadimplencia(BigDecimal inadimplencia) {
		this.inadimplencia = inadimplencia;
	}

	public String getNatureza() {
		return natureza;
	}

	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}
