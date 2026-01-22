package com.zetra.econsig.dto.web;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: ParametroServico</p>
 * <p>Description: POJO para parâmetros na edição de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametroServico {

    private String codigo;
    private String descricao;
    private String dominio;
    private String valor;
    private String valorPadrao;
    private int size;
    private int maxSize;
    private String onClick;

    private boolean combo;
    private String campoValor;
    private String campoLabel;
    private String labelNaoSelecionado;
    private List<TransferObject> comboValues;

    private boolean custom;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(String valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getOnClick() {
        return onClick;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isCombo() {
        return combo;
    }

    public void setCombo(boolean combo) {
        this.combo = combo;
    }

    public String getCampoValor() {
        return campoValor;
    }

    public void setCampoValor(String campoValor) {
        this.campoValor = campoValor;
    }

    public String getCampoLabel() {
        return campoLabel;
    }

    public void setCampoLabel(String campoLabel) {
        this.campoLabel = campoLabel;
    }

    public String getLabelNaoSelecionado() {
        return labelNaoSelecionado;
    }

    public void setLabelNaoSelecionado(String labelNaoSelecionado) {
        this.labelNaoSelecionado = labelNaoSelecionado;
    }

    public List<TransferObject> getComboValues() {
        return comboValues;
    }

    public void setComboValues(List<TransferObject> comboValues) {
        this.comboValues = comboValues;
    }
}
