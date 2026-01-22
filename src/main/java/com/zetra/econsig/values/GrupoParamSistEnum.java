package com.zetra.econsig.values;

/**
 * <p>Title: ItemMenuEnum</p>
 * <p>Description: Enumeração para itens do menu.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum GrupoParamSistEnum {

    SEGURANCA(1, "Segurança"),
    INTEGRACAO_FOLHA(2, "Integração Folha"),
    REGRAS_DE_NEGOCIO(3, "Regras de Negócio"),
    BENEFICIOS(4, "Benefícios");

    private int codigo;
    private String descricao;

    private GrupoParamSistEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
