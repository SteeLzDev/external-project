package com.zetra.econsig.dto.entidade;

import java.io.File;
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
public class SaldoDevedorTransferObject extends CustomTransferObject {

    private String obs;
    private File anexoDsd;
    private File anexoBoleto;

    public SaldoDevedorTransferObject() {
        super();
    }

    public SaldoDevedorTransferObject(TransferObject saldoDevedor) {
        super();
        setAtributos(saldoDevedor.getAtributos());
    }

    // Get:

    public String getAdeCodigo() {
        return (String) getAttribute(Columns.SDV_ADE_CODIGO);
    }

    public Short getBcoCodigo() {
        return (Short) getAttribute(Columns.SDV_BCO_CODIGO);
    }

    public String getSdvAgencia() {
        return (String) getAttribute(Columns.SDV_AGENCIA);
    }

    public String getSdvConta() {
        return (String) getAttribute(Columns.SDV_CONTA);
    }

    public Date getSdvDataMod() {
        return (Date) getAttribute(Columns.SDV_DATA_MOD);
    }

    public BigDecimal getSdvValor() {
        return getAttribute(Columns.SDV_VALOR) != null ? new BigDecimal(getAttribute(Columns.SDV_VALOR).toString()) : null;
    }

    public BigDecimal getSdvValorComDesconto() {
        return getAttribute(Columns.SDV_VALOR_COM_DESCONTO) != null ? new BigDecimal(getAttribute(Columns.SDV_VALOR_COM_DESCONTO).toString()) : null;
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.SDV_USU_CODIGO);
    }

    public String getSdvNomeFavorecido() {
        return (String) getAttribute(Columns.SDV_NOME_FAVORECIDO);
    }

    public String getSdvCnpj() {
        return (String) getAttribute(Columns.SDV_CNPJ);
    }

    public String getSdvLinkBoletoQuitacao() {
        return (String) getAttribute(Columns.SDV_LINK_BOLETO_QUITACAO);
    }

    public String getSdvNumeroContrato() {
        return (String) getAttribute(Columns.SDV_NUMERO_CONTRATO);
    }

    public Date getSdvDataValidade() {
        return (Date) getAttribute(Columns.SDV_DATA_VALIDADE);
    }

    public String getObs() {
        return obs;
    }

    public File getAnexoDsd() {
        return anexoDsd;
    }

    public File getAnexoBoleto() {
        return anexoBoleto;
    }

    // Set:

    public void setAdeCodigo(String adeCodigo) {
        setAttribute(Columns.SDV_ADE_CODIGO, adeCodigo);
    }

    public void setBcoCodigo(Short bcoCodigo) {
        setAttribute(Columns.SDV_BCO_CODIGO, bcoCodigo);
    }

    public void setSdvAgencia(String sdvAgencia) {
        setAttribute(Columns.SDV_AGENCIA, sdvAgencia);
    }

    public void setSdvConta(String sdvConta) {
        setAttribute(Columns.SDV_CONTA, sdvConta);
    }

    public void setSdvDataMod(Date sdvDataMod) {
        setAttribute(Columns.SDV_DATA_MOD, sdvDataMod);
    }

    public void setSdvValor(BigDecimal sdvValor) {
        setAttribute(Columns.SDV_VALOR, sdvValor);
    }

    public void setSdvValorComDesconto(BigDecimal sdvValorComDesconto) {
        setAttribute(Columns.SDV_VALOR_COM_DESCONTO, sdvValorComDesconto);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.SDV_USU_CODIGO, usuCodigo);
    }

    public void setSdvNomeFavorecido(String sdvNomeFavorecido) {
        setAttribute(Columns.SDV_NOME_FAVORECIDO, sdvNomeFavorecido);
    }

    public void setSdvCnpj(String sdvCnpj) {
        setAttribute(Columns.SDV_CNPJ, sdvCnpj);
    }

    public void setSdvLinkBoletoQuitacao(String sdvLinkBoletoQuitacao) {
        setAttribute(Columns.SDV_LINK_BOLETO_QUITACAO, sdvLinkBoletoQuitacao);
    }

    public void setSdvNumeroContrato(String sdvNumeroContrato) {
        setAttribute(Columns.SDV_NUMERO_CONTRATO, sdvNumeroContrato);
    }

    public void setSdvDataValidade(Date sdvDataValidade) {
        setAttribute(Columns.SDV_DATA_VALIDADE, sdvDataValidade);
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public void setAnexoDsd(File anexoDsd) {
        this.anexoDsd = anexoDsd;
    }

    public void setAnexoBoleto(File anexoBoleto) {
        this.anexoBoleto = anexoBoleto;
    }
}
