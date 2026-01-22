package com.zetra.econsig.values;

import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: OperacaoCompraEnum</p>
 * <p>Description: Enumeração de operações de compra, utilizado pelo método
 * que gerencia o status de uma compra.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum OperacaoCompraEnum {

    INFORMAR_SALDO_DEVEDOR("1"),
    APROVAR_SALDO_DEVEDOR("2"),
    REJEITAR_SALDO_DEVEDOR("3"),
    SOLICITAR_RECALCULO_SALDO("4"),
    PAGAMENTO_SALDO_DEVEDOR("5"),
    REJEITAR_PAGAMENTO_SALDO("6"),
    LIQUIDACAO_CONTRATO("7"),
    CONCLUSAO_CONTRATO("8");

    private String codigo;

    private OperacaoCompraEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera uma operação de compra de acordo com o código passado.
     * @param codigo : Código da operação de compra que deve ser recuperado.
     * @return Retorna uma operação de compra
     * @throws IllegalArgumentException Caso o código da operação de compra informádo seja inválido
     */
    public static OperacaoCompraEnum recuperaStatusCompra(String codigo) {
        OperacaoCompraEnum statusCompra = null;

        for (OperacaoCompraEnum status : OperacaoCompraEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusCompra = status;
                break;
            }
        }

        if (statusCompra == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.informado.operacao.compra.invalido", null) );
        }

        return statusCompra;
    }

    public final boolean equals(OperacaoCompraEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
