package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusDespesaComumEnum</p>
 * <p>Description: Enumeração de status de despesa comum.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusDespesaComumEnum {

    ATIVO("1"),
    CANCELADO("2"),
    CONCLUIDO("3");

    private String codigo;

    private StatusDespesaComumEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um status de despesa comum de acordo com o código passado.
     * @param codigo Código do status de despesa comum que deve ser recuperado.
     * @return Retorna um status de despesa comum
     * @throws IllegalArgumentException Caso o código do status de despesa comum informádo seja inválido
     */
    public static StatusDespesaComumEnum recuperaStatusDespesaComum(String codigo) {
        StatusDespesaComumEnum statusDespesaComum = null;

        for (StatusDespesaComumEnum status : StatusDespesaComumEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusDespesaComum = status;
                break;
            }
        }

        if (statusDespesaComum == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.despesa.comum.invalido", (AcessoSistema) null));
        }

        return statusDespesaComum;
    }

    public final boolean equals(StatusDespesaComumEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
