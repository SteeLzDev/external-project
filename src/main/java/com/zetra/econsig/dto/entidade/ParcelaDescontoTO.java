package com.zetra.econsig.dto.entidade;

import java.math.BigDecimal;
import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParcelaDescontoTO</p>
 * <p>Description: Transfer Object de Parcela Desconto</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParcelaDescontoTO extends CustomTransferObject {

    private boolean parcelaDoPeriodo;

    public ParcelaDescontoTO() {
        super();
    }

    public ParcelaDescontoTO(boolean parcelaDoPeriodo) {
        this();
        this.parcelaDoPeriodo = parcelaDoPeriodo;
    }

    public ParcelaDescontoTO(String adeCodigo, Short prdNumero) {
        this();
        setAttribute(Columns.PRD_ADE_CODIGO, adeCodigo);
        setAttribute(Columns.PRD_NUMERO, prdNumero);
    }

    public ParcelaDescontoTO(ParcelaDescontoTO parcelaDesconto) {
        this();
        setAtributos(parcelaDesconto.getAtributos());
    }

    // Getter
    public Integer getPrdCodigo() {
        return (Integer) getAttribute(Columns.PRD_CODIGO);
    }

    public String getAdeCodigo() {
        return (String) getAttribute(Columns.PRD_ADE_CODIGO);
    }

    public Short getPrdNumero() {
        return (Short) getAttribute(Columns.PRD_NUMERO);
    }

    public String getSpdCodigo() {
        return (String) getAttribute(Columns.PRD_SPD_CODIGO);
    }

    public String getSpdDescricao() {
        return (String) getAttribute(Columns.SPD_DESCRICAO);
    }

    public Date getPrdDataDesconto() {
        return (Date) getAttribute(Columns.PRD_DATA_DESCONTO);
    }

    public Date getPrdDataRealizado() {
        return (Date) getAttribute(Columns.PRD_DATA_REALIZADO);
    }

    public BigDecimal getPrdVlrPrevisto() {
        return (BigDecimal) getAttribute(Columns.PRD_VLR_PREVISTO);
    }

    public BigDecimal getPrdVlrRealizado() {
        return (BigDecimal) getAttribute(Columns.PRD_VLR_REALIZADO);
    }

    public String getTdeCodigo() {
        return (String) getAttribute(Columns.PRD_TDE_CODIGO);
    }

    public String getMneCodigo() {
        return (String) getAttribute(Columns.PRD_MNE_CODIGO);
    }

    public boolean isParcelaDoPeriodo() {
        return parcelaDoPeriodo;
    }

    // Setter
    public void setPrdCodigo(Integer prdCodigo) {
        setAttribute(Columns.PRD_CODIGO, prdCodigo);
    }

    public void setAdeCodigo(String adeCodigo) {
        setAttribute(Columns.PRD_ADE_CODIGO, adeCodigo);
    }

    public void setPrdNumero(Short prdNumero) {
        setAttribute(Columns.PRD_NUMERO, prdNumero);
    }

    public void setSpdCodigo(String spdCodigo) {
        setAttribute(Columns.PRD_SPD_CODIGO, spdCodigo);
    }

    public void setSpdDescricao(String spdDescricao) {
        setAttribute(Columns.SPD_DESCRICAO, spdDescricao);
    }

    public void setPrdDataDesconto(Date prdDataDesconto) {
        setAttribute(Columns.PRD_DATA_DESCONTO, prdDataDesconto);
    }

    public void setPrdDataRealizado(Date prdDataRealizado) {
        setAttribute(Columns.PRD_DATA_REALIZADO, prdDataRealizado);
    }

    public void setPrdVlrPrevisto(BigDecimal prdVlrPrevisto) {
        setAttribute(Columns.PRD_VLR_PREVISTO, prdVlrPrevisto);
    }

    public void setPrdVlrRealizado(BigDecimal prdVlrRealizado) {
        setAttribute(Columns.PRD_VLR_REALIZADO, prdVlrRealizado);
    }

    public void setTdeCodigo(String tdeCodigo) {
        setAttribute(Columns.PRD_TDE_CODIGO, tdeCodigo);
    }

    public void setMneCodigo(String mneCodigo) {
        setAttribute(Columns.PRD_MNE_CODIGO, mneCodigo);
    }
}
