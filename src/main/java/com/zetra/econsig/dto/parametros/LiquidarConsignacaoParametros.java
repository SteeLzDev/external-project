package com.zetra.econsig.dto.parametros;

import java.io.File;
import java.util.Collection;
import java.util.Date;

/**
 * <p>Title: LiquidarConsiganacaoParametros</p>
 * <p>Description: Parâmetros necessários na liquidação de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LiquidarConsignacaoParametros extends Parametros {
    // Atributos para dados da decisão judicial
    private String tjuCodigo;
    private String cidCodigo;
    private String djuNumProcesso;
    private Date djuData;
    private String djuTexto;

    private String[] visibilidadeAnexos;
    private Collection<File> anexos;

    private boolean liquidarPortabilidadeCartao = false;
    private boolean renegociacao = false;
    private boolean podeConfirmarRenegociacao = false;
    private boolean verificaBloqueioOperacao = true;
    private boolean verificaReservaCartaoCredito = true;
    private boolean apenasValidacao = false;
    private java.util.Date ocaPeriodo;

    public String getTjuCodigo() {
        return tjuCodigo;
    }

    public void setTjuCodigo(String tjuCodigo) {
        this.tjuCodigo = tjuCodigo;
    }

    public String getCidCodigo() {
        return cidCodigo;
    }

    public void setCidCodigo(String cidCodigo) {
        this.cidCodigo = cidCodigo;
    }

    public String getDjuNumProcesso() {
        return djuNumProcesso;
    }

    public void setDjuNumProcesso(String djuNumProcesso) {
        this.djuNumProcesso = djuNumProcesso;
    }

    public Date getDjuData() {
        return djuData;
    }

    public void setDjuData(Date djuData) {
        this.djuData = djuData;
    }

    public String getDjuTexto() {
        return djuTexto;
    }

    public void setDjuTexto(String djuTexto) {
        this.djuTexto = djuTexto;
    }

    public Collection<File> getAnexos() {
        return anexos;
    }

    public void setAnexos(Collection<File> anexos) {
        this.anexos = anexos;
    }

    public String[] getVisibilidadeAnexos() {
        return visibilidadeAnexos;
    }

    public void setVisibilidadeAnexos(String[] visibilidadeAnexos) {
        this.visibilidadeAnexos = visibilidadeAnexos;
    }

    public boolean isRenegociacao() {
        return renegociacao;
    }

    public void setRenegociacao(boolean renegociacao) {
        this.renegociacao = renegociacao;
    }

    public boolean isPodeConfirmarRenegociacao() {
        return podeConfirmarRenegociacao;
    }

    public void setPodeConfirmarRenegociacao(boolean podeConfirmarRenegociacao) {
        this.podeConfirmarRenegociacao = podeConfirmarRenegociacao;
    }

    public boolean isVerificaBloqueioOperacao() {
        return verificaBloqueioOperacao;
    }

    public void setVerificaBloqueioOperacao(boolean verificaBloqueioOperacao) {
        this.verificaBloqueioOperacao = verificaBloqueioOperacao;
    }

    public boolean isVerificaReservaCartaoCredito() {
        return verificaReservaCartaoCredito;
    }

    public void setVerificaReservaCartaoCredito(boolean verificaReservaCartaoCredito) {
        this.verificaReservaCartaoCredito = verificaReservaCartaoCredito;
    }

    public boolean isApenasValidacao() {
        return apenasValidacao;
    }

    public void setApenasValidacao(boolean apenasValidacao) {
        this.apenasValidacao = apenasValidacao;
    }

    public java.util.Date getOcaPeriodo() {
        return ocaPeriodo;
    }

    public void setOcaPeriodo(java.util.Date ocaPeriodo) {
        this.ocaPeriodo = ocaPeriodo;
    }

    public boolean isLiquidarPortabilidadeCartao() {
        return liquidarPortabilidadeCartao;
    }

    public void setLiquidarPortabilidadeCartao(boolean liquidarPortabilidadeCartao) {
        this.liquidarPortabilidadeCartao = liquidarPortabilidadeCartao;
    }
}
