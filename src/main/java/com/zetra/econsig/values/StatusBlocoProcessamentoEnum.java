package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusBlocoProcessamentoEnum</p>
 * <p>Description: Enumeração de status de blocos de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusBlocoProcessamentoEnum {

    PREPARANDO("1"),
    AGUARD_PROCESSAMENTO("2"),
    EM_PROCESSAMENTO("3"),
    PROCESSADO_COM_SUCESSO("4"),
    PROCESSADO_COM_ERRO("5"),
    CANCELADO("6");

    private String codigo;

    private StatusBlocoProcessamentoEnum(String codigo) {
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
     * Recupera um status de bloco de processamento de acordo com o código passado.
     * @param codigo Código do status de bloco de processamento que deve ser recuperado.
     * @return Retorna um status de bloco de processamento
     * @throws IllegalArgumentException Caso o código do status de bloco de processamento informádo seja inválido
     */
    public static StatusBlocoProcessamentoEnum recuperaStatusBlocoProcessamento(String codigo) {
        StatusBlocoProcessamentoEnum statusBlocoProcessamento = null;

        for (StatusBlocoProcessamentoEnum status : StatusBlocoProcessamentoEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusBlocoProcessamento = status;
                break;
            }
        }

        if (statusBlocoProcessamento == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.bloco.processamento.invalido", (AcessoSistema) null));
        }

        return statusBlocoProcessamento;
    }

    public final boolean equals(StatusBlocoProcessamentoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }

    public final boolean equals(String other) {
        return getCodigo().equals(other);
    }
}
