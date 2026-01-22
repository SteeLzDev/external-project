package com.zetra.econsig.dto.web;

public class RankServicoDTO {

    String csaNomeLst;

    String strCftVlr;

    String cetAnual;

    String vlrParcela;

    boolean temCET;

    boolean simulacaoPorTaxaJuros;

    boolean simulacaoMetodoMexicano;

    String cat;

    String iva;

    String tac;

    String iof;

    public RankServicoDTO() {
    }

    public RankServicoDTO(String csaNomeLst, String strCftVlr, String cetAnual, String vlrParcela, boolean temCET, boolean simulacaoPorTaxaJuros, boolean simulacaoMetodoMexicano, String cat, String iva, String tac, String iof) {
        this.csaNomeLst = csaNomeLst;
        this.strCftVlr = strCftVlr;
        this.cetAnual = cetAnual;
        this.vlrParcela = vlrParcela;
        this.temCET = temCET;
        this.simulacaoPorTaxaJuros = simulacaoPorTaxaJuros;
        this.simulacaoMetodoMexicano = simulacaoMetodoMexicano;
        this.cat = cat;
        this.iva = iva;
        this.tac = tac;
        this.iof = iof;
    }

    public String getCsaNomeLst() {
        return csaNomeLst;
    }

    public void setCsaNomeLst(String csaNomeLst) {
        this.csaNomeLst = csaNomeLst;
    }

    public String getStrCftVlr() {
        return strCftVlr;
    }

    public void setStrCftVlr(String strCftVlr) {
        this.strCftVlr = strCftVlr;
    }

    public String getCetAnual() {
        return cetAnual;
    }

    public void setCetAnual(String cetAnual) {
        this.cetAnual = cetAnual;
    }

    public String getVlrParcela() {
        return vlrParcela;
    }

    public void setVlrParcela(String vlrParcela) {
        this.vlrParcela = vlrParcela;
    }

    public boolean isTemCET() {
        return temCET;
    }

    public void setTemCET(boolean temCET) {
        this.temCET = temCET;
    }

    public boolean isSimulacaoPorTaxaJuros() {
        return simulacaoPorTaxaJuros;
    }

    public void setSimulacaoPorTaxaJuros(boolean simulacaoPorTaxaJuros) {
        this.simulacaoPorTaxaJuros = simulacaoPorTaxaJuros;
    }

    public boolean isSimulacaoMetodoMexicano() {
        return simulacaoMetodoMexicano;
    }

    public void setSimulacaoMetodoMexicano(boolean simulacaoMetodoMexicano) {
        this.simulacaoMetodoMexicano = simulacaoMetodoMexicano;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getIof() {
        return iof;
    }

    public void setIof(String iof) {
        this.iof = iof;
    }

}
