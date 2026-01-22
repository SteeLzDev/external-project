package com.zetra.econsig.values;

import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusCompraEnum</p>
 * <p>Description: Enumeração de status de compra.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusCompraEnum {

    AGUARDANDO_INF_SALDO("1"),
    AGUARDANDO_PAG_SALDO("2"),
    AGUARDANDO_LIQUIDACAO("3"),
    LIQUIDADO("4"),
    FINALIZADO("5"),
    CANCELADO("6"),
    AGUARDANDO_APR_SALDO("7");

    private String codigo;

    private StatusCompraEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um status de compra de acordo com o código passado.
     * @param codigo Código do status de compra que deve ser recuperado.
     * @return Retorna um status de compra
     * @throws IllegalArgumentException Caso o código do status de compra informádo seja inválido
     */
    public static StatusCompraEnum recuperaStatusCompra(String codigo) {
        StatusCompraEnum statusCompra = null;

        for (StatusCompraEnum status : StatusCompraEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusCompra = status;
                break;
            }
        }

        if (statusCompra == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.informado.status.compra.invalido", null));
        }

        return statusCompra;
    }

    public final boolean equals(StatusCompraEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
