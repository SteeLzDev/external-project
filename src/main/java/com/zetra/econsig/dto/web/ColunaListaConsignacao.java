package com.zetra.econsig.dto.web;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ColunaListaConsignacao</p>
 * <p>Description: POJO contendo configuração sobre as colunas da listagem de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ColunaListaConsignacao {

    public enum TipoValor {
        NUMERICO, MONETARIO, TEXTO, DATA
    }

    private String chaveCampo;
    private String titulo;
    private TipoValor tipoValor;
    private final boolean desabilitarAcao;

    public ColunaListaConsignacao(String chaveCampo, String titulo) {
        this(chaveCampo, titulo, TipoValor.TEXTO, false);
    }

    public ColunaListaConsignacao(String chaveCampo, String titulo, TipoValor tipoValor) {
        this(chaveCampo, titulo, tipoValor, false);
    }

    public ColunaListaConsignacao(String chaveCampo, String titulo, TipoValor tipoValor, boolean desabilitarAcao) {
        super();
        this.chaveCampo = chaveCampo;
        this.titulo = titulo;
        this.tipoValor = tipoValor;
        this.desabilitarAcao = desabilitarAcao;
    }

    public String getChaveCampo() {
        return chaveCampo;
    }

    public void setChaveCampo(String chaveCampo) {
        this.chaveCampo = chaveCampo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(TipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }


    public boolean isMonetario() {
        return tipoValor != null && tipoValor.equals(TipoValor.MONETARIO);
    }

    public boolean isNumerico() {
        return tipoValor != null && tipoValor.equals(TipoValor.NUMERICO);
    }

    public boolean isData() {
        return tipoValor != null && tipoValor.equals(TipoValor.DATA);
    }

    public boolean isAcaoDesabilitada(TransferObject ade) {
        return desabilitarAcao && ade != null && !TextHelper.isNull(ade.getAttribute(chaveCampo));
    }
}
