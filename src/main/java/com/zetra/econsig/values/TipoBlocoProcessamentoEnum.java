package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoBlocoProcessamentoEnum</p>
 * <p>Description: Enumeração de tipo de blocos de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoBlocoProcessamentoEnum {

    MARGEM("1"),
    MARGEM_COMPLEMENTAR("2"),
    RETORNO("3"),
    RETORNO_ATRASADO("4"),
    RETORNO_DE_FERIAS("5"),
    CRITICA("6"),
    TRANSFERIDO("7");

    private String codigo;

    private TipoBlocoProcessamentoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }

    /**
     * Recupera um tipo de bloco de processamento de acordo com o código passado.
     * @param codigo Código do tipo de bloco de processamento que deve ser recuperado.
     * @return Retorna um tipo de bloco de processamento
     * @throws IllegalArgumentException Caso o código do tipo de bloco de processamento informádo seja inválido
     */
    public static TipoBlocoProcessamentoEnum recuperaTipoBlocoProcessamento(String codigo) {
        TipoBlocoProcessamentoEnum tipoBlocoProcessamento = null;

        for (TipoBlocoProcessamentoEnum tipo : TipoBlocoProcessamentoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoBlocoProcessamento = tipo;
                break;
            }
        }

        if (tipoBlocoProcessamento == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.bloco.processamento.invalido", (AcessoSistema) null));
        }

        return tipoBlocoProcessamento;
    }

    public final boolean equals(TipoBlocoProcessamentoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }

    public final boolean equals(String other) {
        return getCodigo().equals(other);
    }
}
