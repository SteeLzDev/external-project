package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EstatisticoProcessamentoBean implements Serializable {

    private String tarCodigo;
    private String tarDescricao;
    private Integer harMaxQntLinhas0;
    private Integer harMaxQntLinhas1;
    private Integer harMaxQntLinhas2;
    private Integer harMaxQntLinhas3;
    private Integer harMaxQntLinhas4;
    private Integer harMaxQntLinhas5;
    private Integer harMaxQntLinhas6;
    private Integer harMaxQntLinhas7;
    private Integer harMaxQntLinhas8;
    private Integer harMaxQntLinhas9;
    private Integer harMaxQntLinhas10;
    private Integer harMaxQntLinhas11;

    public EstatisticoProcessamentoBean() {
    }

    public EstatisticoProcessamentoBean(String tarCodigo, String tarDescricao, List<Integer> harMaxQntLinhas) {
        this.tarCodigo = tarCodigo;
        this.tarDescricao = tarDescricao;
        if (harMaxQntLinhas.size() == 12) {
            harMaxQntLinhas0 = harMaxQntLinhas.get(0);
            harMaxQntLinhas1 = harMaxQntLinhas.get(1);
            harMaxQntLinhas2 = harMaxQntLinhas.get(2);
            harMaxQntLinhas3 = harMaxQntLinhas.get(3);
            harMaxQntLinhas4 = harMaxQntLinhas.get(4);
            harMaxQntLinhas5 = harMaxQntLinhas.get(5);
            harMaxQntLinhas6 = harMaxQntLinhas.get(6);
            harMaxQntLinhas7 = harMaxQntLinhas.get(7);
            harMaxQntLinhas8 = harMaxQntLinhas.get(8);
            harMaxQntLinhas9 = harMaxQntLinhas.get(9);
            harMaxQntLinhas10 = harMaxQntLinhas.get(10);
            harMaxQntLinhas11 = harMaxQntLinhas.get(11);
        } else if (harMaxQntLinhas.isEmpty()) {
            harMaxQntLinhas0 = null;
            harMaxQntLinhas1 = null;
            harMaxQntLinhas2 = null;
            harMaxQntLinhas3 = null;
            harMaxQntLinhas4 = null;
            harMaxQntLinhas5 = null;
            harMaxQntLinhas6 = null;
            harMaxQntLinhas7 = null;
            harMaxQntLinhas8 = null;
            harMaxQntLinhas9 = null;
            harMaxQntLinhas10 = null;
            harMaxQntLinhas11 = null;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getTarCodigo() {
        return tarCodigo;
    }
    public void setTarCodigo(String tarCodigo) {
        this.tarCodigo = tarCodigo;
    }
    public String getTarDescricao() {
        return tarDescricao;
    }
    public void setTarDescricao(String tarDescricao) {
        this.tarDescricao = tarDescricao;
    }
    public Integer getHarMaxQntLinhas0() {
        return harMaxQntLinhas0;
    }
    public void setHarMaxQntLinhas0(Integer harMaxQntLinhas0) {
        this.harMaxQntLinhas0 = harMaxQntLinhas0;
    }
    public Integer getHarMaxQntLinhas1() {
        return harMaxQntLinhas1;
    }
    public void setHarMaxQntLinhas1(Integer harMaxQntLinhas1) {
        this.harMaxQntLinhas1 = harMaxQntLinhas1;
    }
    public Integer getHarMaxQntLinhas2() {
        return harMaxQntLinhas2;
    }
    public void setHarMaxQntLinhas2(Integer harMaxQntLinhas2) {
        this.harMaxQntLinhas2 = harMaxQntLinhas2;
    }
    public Integer getHarMaxQntLinhas3() {
        return harMaxQntLinhas3;
    }
    public void setHarMaxQntLinhas3(Integer harMaxQntLinhas3) {
        this.harMaxQntLinhas3 = harMaxQntLinhas3;
    }
    public Integer getHarMaxQntLinhas4() {
        return harMaxQntLinhas4;
    }
    public void setHarMaxQntLinhas4(Integer harMaxQntLinhas4) {
        this.harMaxQntLinhas4 = harMaxQntLinhas4;
    }
    public Integer getHarMaxQntLinhas5() {
        return harMaxQntLinhas5;
    }
    public void setHarMaxQntLinhas5(Integer harMaxQntLinhas5) {
        this.harMaxQntLinhas5 = harMaxQntLinhas5;
    }
    public Integer getHarMaxQntLinhas6() {
        return harMaxQntLinhas6;
    }
    public void setHarMaxQntLinhas6(Integer harMaxQntLinhas6) {
        this.harMaxQntLinhas6 = harMaxQntLinhas6;
    }
    public Integer getHarMaxQntLinhas7() {
        return harMaxQntLinhas7;
    }
    public void setHarMaxQntLinhas7(Integer harMaxQntLinhas7) {
        this.harMaxQntLinhas7 = harMaxQntLinhas7;
    }
    public Integer getHarMaxQntLinhas8() {
        return harMaxQntLinhas8;
    }
    public void setHarMaxQntLinhas8(Integer harMaxQntLinhas8) {
        this.harMaxQntLinhas8 = harMaxQntLinhas8;
    }
    public Integer getHarMaxQntLinhas9() {
        return harMaxQntLinhas9;
    }
    public void setHarMaxQntLinhas9(Integer harMaxQntLinhas9) {
        this.harMaxQntLinhas9 = harMaxQntLinhas9;
    }
    public Integer getHarMaxQntLinhas10() {
        return harMaxQntLinhas10;
    }
    public void setHarMaxQntLinhas10(Integer harMaxQntLinhas10) {
        this.harMaxQntLinhas10 = harMaxQntLinhas10;
    }
    public Integer getHarMaxQntLinhas11() {
        return harMaxQntLinhas11;
    }
    public void setHarMaxQntLinhas11(Integer harMaxQntLinhas11) {
        this.harMaxQntLinhas11 = harMaxQntLinhas11;
    }

    public void setHarMaxQntLinhas(List<Integer> harMaxQntLinhas) {
        if (harMaxQntLinhas.size() == 12) {
            harMaxQntLinhas0 = harMaxQntLinhas.get(0);
            harMaxQntLinhas1 = harMaxQntLinhas.get(1);
            harMaxQntLinhas2 = harMaxQntLinhas.get(2);
            harMaxQntLinhas3 = harMaxQntLinhas.get(3);
            harMaxQntLinhas4 = harMaxQntLinhas.get(4);
            harMaxQntLinhas5 = harMaxQntLinhas.get(5);
            harMaxQntLinhas6 = harMaxQntLinhas.get(6);
            harMaxQntLinhas7 = harMaxQntLinhas.get(7);
            harMaxQntLinhas8 = harMaxQntLinhas.get(8);
            harMaxQntLinhas9 = harMaxQntLinhas.get(9);
            harMaxQntLinhas10 = harMaxQntLinhas.get(10);
            harMaxQntLinhas11 = harMaxQntLinhas.get(11);
        } else if (harMaxQntLinhas.isEmpty()) {
            harMaxQntLinhas0 = null;
            harMaxQntLinhas1 = null;
            harMaxQntLinhas2 = null;
            harMaxQntLinhas3 = null;
            harMaxQntLinhas4 = null;
            harMaxQntLinhas5 = null;
            harMaxQntLinhas6 = null;
            harMaxQntLinhas7 = null;
            harMaxQntLinhas8 = null;
            harMaxQntLinhas9 = null;
            harMaxQntLinhas10 = null;
            harMaxQntLinhas11 = null;
        } else {
            throw new IllegalArgumentException();
        }
    }
    public List<Integer> getHarMaxQntLinhas() {
        List<Integer> harMaxQntLinhas = new ArrayList<>(12);

        harMaxQntLinhas.add(0, harMaxQntLinhas0);
        harMaxQntLinhas.add(1, harMaxQntLinhas1);
        harMaxQntLinhas.add(2, harMaxQntLinhas2);
        harMaxQntLinhas.add(3, harMaxQntLinhas3);
        harMaxQntLinhas.add(4, harMaxQntLinhas4);
        harMaxQntLinhas.add(5, harMaxQntLinhas5);
        harMaxQntLinhas.add(6, harMaxQntLinhas6);
        harMaxQntLinhas.add(7, harMaxQntLinhas7);
        harMaxQntLinhas.add(8, harMaxQntLinhas8);
        harMaxQntLinhas.add(9, harMaxQntLinhas9);
        harMaxQntLinhas.add(10, harMaxQntLinhas10);
        harMaxQntLinhas.add(11, harMaxQntLinhas11);

        return harMaxQntLinhas;
    }
}