package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;

public class ServidorPorCrsBean implements Serializable {

    private String descricao;
    private Long quantidade;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
