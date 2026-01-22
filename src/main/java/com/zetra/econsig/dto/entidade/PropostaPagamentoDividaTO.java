package com.zetra.econsig.dto.entidade;

import java.math.BigDecimal;
import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SaldoDevedorTransferObject</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft Internet Service</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PropostaPagamentoDividaTO extends CustomTransferObject {

    public PropostaPagamentoDividaTO() {
        super();
    }

    public PropostaPagamentoDividaTO(TransferObject propostaPagamentoDivida) {
        super();
        setAtributos(propostaPagamentoDivida.getAtributos());
    }

    // Getters

    public String getPpdCodigo() {
        return (String) getAttribute(Columns.PPD_CODIGO);
    }

    public String getAdeCodigo() {
        return (String) getAttribute(Columns.PPD_ADE_CODIGO);
    }

    public String getCsaCodigo() {
        return (String) getAttribute(Columns.PPD_CSA_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.PPD_USU_CODIGO);
    }

    public String getStpCodigo() {
        return (String) getAttribute(Columns.PPD_STP_CODIGO);
    }

    public Integer getPpdNumero() {
        return (Integer) getAttribute(Columns.PPD_NUMERO);
    }

    public BigDecimal getPpdValorDivida() {
        return (BigDecimal) getAttribute(Columns.PPD_VALOR_DIVIDA);
    }

    public BigDecimal getPpdValorParcela() {
        return (BigDecimal) getAttribute(Columns.PPD_VALOR_PARCELA);
    }

    public Integer getPpdPrazo() {
        return (Integer) getAttribute(Columns.PPD_PRAZO);
    }

    public BigDecimal getPpdTaxaJuros() {
        return (BigDecimal) getAttribute(Columns.PPD_TAXA_JUROS);
    }

    public Date getPpdDataCadastro() {
        return (Date) getAttribute(Columns.PPD_DATA_CADASTRO);
    }

    public Date getPpdDataValidade() {
        return (Date) getAttribute(Columns.PPD_DATA_VALIDADE);
    }


    // Setters

    public void setPpdCodigo(String ppdCodigo) {
        setAttribute(Columns.PPD_CODIGO, ppdCodigo);
    }

    public void setAdeCodigo(String adeCodigo) {
        setAttribute(Columns.PPD_ADE_CODIGO, adeCodigo);
    }

    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.PPD_CSA_CODIGO, csaCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.PPD_USU_CODIGO, usuCodigo);
    }

    public void setStpCodigo(String stpCodigo) {
        setAttribute(Columns.PPD_STP_CODIGO, stpCodigo);
    }

    public void setPpdNumero(Integer ppdNumero) {
        setAttribute(Columns.PPD_NUMERO, ppdNumero);
    }

    public void setPpdValorDivida(BigDecimal ppdValorDivida) {
        setAttribute(Columns.PPD_VALOR_DIVIDA, ppdValorDivida);
    }

    public void setPpdValorParcela(BigDecimal ppdValorParcela) {
        setAttribute(Columns.PPD_VALOR_PARCELA, ppdValorParcela);
    }

    public void setPpdPrazo(Integer ppdPrazo) {
        setAttribute(Columns.PPD_PRAZO, ppdPrazo);
    }

    public void setPpdTaxaJuros(BigDecimal ppdTaxaJuros) {
        setAttribute(Columns.PPD_TAXA_JUROS, ppdTaxaJuros);
    }

    public void setPpdDataCadastro(Date ppdDataCadastro) {
        setAttribute(Columns.PPD_DATA_CADASTRO, ppdDataCadastro);
    }

    public void setPpdDataValidade(Date ppdDataValidade) {
        setAttribute(Columns.PPD_DATA_VALIDADE, ppdDataValidade);
    }
}
