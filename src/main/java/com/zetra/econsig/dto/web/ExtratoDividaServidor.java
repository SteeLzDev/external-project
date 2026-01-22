package com.zetra.econsig.dto.web;

/**
 * <p>Title: ExtratoDividaServidor</p>
 * <p>Description: POJO auxiliar de model da view de extrato de dívida de servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExtratoDividaServidor {

    private final String consignataria;
    private final String servico;
    private final String adeNumero;
    private final String adeData;
    private final String adeVlr;
    private final String adePrazo;
    private final String adePrdPagas;
    private final String sadDescricao;

    public ExtratoDividaServidor(String consignataria, String servico, String adeNumero, String adeData, String adeVlr, String adePrazo, String adePrdPagas, String sadDescricao) {
        super();
        this.consignataria = consignataria;
        this.servico = servico;
        this.adeNumero = adeNumero;
        this.adeData = adeData;
        this.adeVlr = adeVlr;
        this.adePrazo = adePrazo;
        this.adePrdPagas = adePrdPagas;
        this.sadDescricao = sadDescricao;
    }

    public String getConsignataria() {
        return consignataria;
    }

    public String getServico() {
        return servico;
    }

    public String getAdeNumero() {
        return adeNumero;
    }

    public String getAdeData() {
        return adeData;
    }

    public String getAdeVlr() {
        return adeVlr;
    }

    public String getAdePrazo() {
        return adePrazo;
    }

    public String getAdePrdPagas() {
        return adePrdPagas;
    }

    public String getSadDescricao() {
        return sadDescricao;
    }

}
