package com.zetra.econsig.report.jasper.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>Title: RegrasConvenioParametrosBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados do relatório parcelas processadas e futuras.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */


public class ParcelasProcessadasFuturasBean {

    private Long adeNumero;
    private Date dataInclusao;
    private String statusParcela;
    private String usuNome;
    private String tipoPrazo;
    private int numParcela;
    private int prazo;
    private BigDecimal valorParcela;
    private String periodoParcela;
    private String cnvCodVerba;

    public ParcelasProcessadasFuturasBean(Long adeNumero, Date dataInclusao, String statusParcela, String usuNome, String tipoPrazo, int numParcela, int prazo, BigDecimal valorParcela, String periodoParcela, String cnvCodVerba) {
        this.adeNumero = adeNumero;
        this.dataInclusao = dataInclusao;
        this.statusParcela = statusParcela;
        this.usuNome = usuNome;
        this.tipoPrazo = tipoPrazo;
        this.numParcela = numParcela;
        this.prazo = prazo;
        this.valorParcela = valorParcela;
        this.periodoParcela = periodoParcela;
        this.cnvCodVerba = cnvCodVerba;
    }

    public int getPrazo() {
        return prazo;
    }


    public void setPrazo(int prazo) {
        this.prazo = prazo;
    }


    public Long getAdeNumero() {
        return adeNumero;
    }


    public void setAdeNumero(Long adeNumero) {
        this.adeNumero = adeNumero;
    }


    public Date getDataInclusao() {
        return dataInclusao;
    }


    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }


    public String getStatusParcela() {
        return statusParcela;
    }


    public void setStatusParcela(String statusParcela) {
        this.statusParcela = statusParcela;
    }


    public String getUsuNome() {
        return usuNome;
    }


    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }


    public String getTipoPrazo() {
        return tipoPrazo;
    }


    public void setTipoPrazo(String tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
    }


    public int getNumParcela() {
        return numParcela;
    }


    public void setNumParcela(int numParcela) {
        this.numParcela = numParcela;
    }


    public BigDecimal getValorParcela() {
        return valorParcela;
    }


    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    public String getPeriodoParcela() {
        return periodoParcela;
    }

    public void setPeriodoparcela(String periodoParcela) {
        this.periodoParcela = periodoParcela;
    }

	public String getCnvCodVerba() {
		return cnvCodVerba;
	}

	public void setCnvCodVerba(String cnvCodVerba) {
		this.cnvCodVerba = cnvCodVerba;
	}
}
