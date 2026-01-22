package com.zetra.econsig.dto.web;

import java.math.BigDecimal;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.financeiro.CDCHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ResultadoSimulacao</p>
 * <p>Description: POJO para armazenar o resultado da simulação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ResultadoSimulacao {

    /* Código da Consignatária que oferta o empréstimo nesta taxa */
    private String csaCodigo;

    /* Nome da Consignatária que oferta o empréstimo nesta taxa */
    private String csaNome;

    /* Posição no ranking de simulação deste registro */
    private String ranking;

    /* Valor liberado informado ou calculado na simulação */
    private BigDecimal valorLiberado;

    /* Valor da parcela informado ou calculado na simulação */
    private BigDecimal valorParcela;

    /* Taxa de Juros, Coeficiente ou CET */
    private BigDecimal valorTaxaJuros;

    /* TAC (BR) ou CAT (MX) */
    private BigDecimal valorTaxaExtra;

    /* IOF (BR) ou IVA (MX) */
    private BigDecimal valorImposto;

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public BigDecimal getValorLiberado() {
        return valorLiberado;
    }

    public void setValorLiberado(BigDecimal valorLiberado) {
        this.valorLiberado = valorLiberado;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    public BigDecimal getValorTaxaJuros() {
        return valorTaxaJuros;
    }

    public void setValorTaxaJuros(BigDecimal valorTaxaJuros) {
        this.valorTaxaJuros = valorTaxaJuros;
    }

    public BigDecimal getValorTaxaExtra() {
        return valorTaxaExtra;
    }

    public void setValorTaxaExtra(BigDecimal valorTaxaExtra) {
        this.valorTaxaExtra = valorTaxaExtra;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public ResultadoSimulacao carregarValores(TransferObject to) {
        csaCodigo = (String) to.getAttribute(Columns.CSA_CODIGO);
        csaNome = (String) to.getAttribute("TITULO");

        valorLiberado = (BigDecimal) to.getAttribute("VLR_LIBERADO");
        valorParcela = (BigDecimal) to.getAttribute("VLR_PARCELA");
        ranking = (String) to.getAttribute("RANKING");
        valorTaxaJuros = (BigDecimal) to.getAttribute(Columns.CFT_VLR);

        if (ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, AcessoSistema.getAcessoUsuarioSistema())) {
            valorTaxaExtra = (BigDecimal) to.getAttribute("CAT");
            valorImposto = (BigDecimal) to.getAttribute("IVA");
        } else if (ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, AcessoSistema.getAcessoUsuarioSistema())) {
            valorTaxaExtra = (BigDecimal) to.getAttribute("TAC_FINANCIADA");
            valorImposto = (BigDecimal) to.getAttribute("IOF");
        }

        return this;
    }


    public String getTextoValorLiberado() {
        return NumberHelper.format(valorLiberado != null ? valorLiberado.doubleValue() : 0, NumberHelper.getLang(), true);
    }

    public String getTextoValorParcela() {
        return NumberHelper.format(valorParcela != null ? valorParcela.doubleValue() : 0, NumberHelper.getLang(), true);
    }

    public String getTextoValorTaxaJuros() {
        return NumberHelper.format(valorTaxaJuros != null ? valorTaxaJuros.doubleValue() : 0, NumberHelper.getLang(), 2, 8);
    }

    public String getTextoValorTaxaJurosAnual() {
        return CDCHelper.getStrTaxaEquivalenteAnual(getTextoValorTaxaJuros());
    }

    public String getTextoValorTaxaExtra() {
        return NumberHelper.format(valorTaxaExtra != null ? valorTaxaExtra.doubleValue() : 0, NumberHelper.getLang(), true);
    }

    public String getTextoValorImposto() {
        return NumberHelper.format(valorImposto != null ? valorImposto.doubleValue() : 0, NumberHelper.getLang(), true);
    }

}
