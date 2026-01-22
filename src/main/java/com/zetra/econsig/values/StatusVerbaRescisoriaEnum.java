package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusVerbaRescisoriaEnum</p>
 * <p>Description: Enumeração de status da verba rescisória.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusVerbaRescisoriaEnum {
    CANDIDATO("1"),
    AGUARDANDO_VERBA_RESCISORIA("2"),
    CONCLUIDO("3");

    private String codigo;

    private StatusVerbaRescisoriaEnum(String codigo) {
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
     * Recupera um status de verba rescisoria de acordo com o código passado.
     * @param codigo Código do status de verba rescisoria que deve ser recuperado.
     * @return Retorna um status de verba rescisoria
     * @throws IllegalArgumentException Caso o código do status de verba rescisoria informádo seja inválido
     */
    public static StatusVerbaRescisoriaEnum recuperaStatusVerbaRescisoria(String codigo) {
        StatusVerbaRescisoriaEnum statusVerbaRescisoria = null;

        for (StatusVerbaRescisoriaEnum status : StatusVerbaRescisoriaEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusVerbaRescisoria = status;
                break;
            }
        }

        if (statusVerbaRescisoria == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.verba.rescisoria.invalido", (AcessoSistema) null));
        }

        return statusVerbaRescisoria;
    }

    public final boolean equals(StatusVerbaRescisoriaEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }

    public final boolean equals(String other) {
        return getCodigo().equals(other);
    }

}
