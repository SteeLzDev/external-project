package com.zetra.econsig.values;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: StatusConsignatariaEnum</p>
 * <p>Description: Enumeration do status da  Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
public enum StatusConsignatariaEnum {

    BLOQUEADO("0"),
    ATIVO("1"),
    PENDENTE_DE_APROVACAO("3"),
    BLOQUEADO_POR_SEGURANCA("7");

    private String codigo;

    private StatusConsignatariaEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isBloqueado() {
        return !codigo.equals(ATIVO.codigo);
    }

    /**
     * Recupera um status da consignatária de acordo com o código
     * @param codigo Código do status da consignatária que deve ser recuperado.
     * @return Retorna um status da consignatária
     * @throws IllegalArgumentException Caso o código da consignatária informádo seja inválido
     */
    public static StatusConsignatariaEnum recuperaStatusConsignataria(String codigo) {
        if (TextHelper.isNull(codigo)) {
            // Se é nulo, então está ativo (o campo no banco aceita nulo e default é 1)
            return ATIVO;
        }

        StatusConsignatariaEnum statusConsignataria = null;

        for (StatusConsignatariaEnum status : StatusConsignatariaEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusConsignataria = status;
                break;
            }
        }

        if (statusConsignataria == null) {
            // Se é diferente de nulo e não encontrou, então assume que está bloqueado
            statusConsignataria = BLOQUEADO;
        }

        return statusConsignataria;
    }

    public final boolean equals(StatusConsignatariaEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
