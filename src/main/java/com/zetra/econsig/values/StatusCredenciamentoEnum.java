package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusCredenciamentoEnum</p>
 * <p>Description: Enumeração de status de solicitação.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusCredenciamentoEnum {

    AGUARDANDO_ENVIO_DOCUMENTACAO_CSA("1"),
    AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE("2"),
    AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE("3"),
    AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA("4"),
    AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE("5"),
    AGUARDANDO_APROVACAO_TERMO_ADITIVO("6"),
    FINALIZADO("7");

    private String codigo;

    private StatusCredenciamentoEnum(String codigo) {
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
     * Recupera um status de credenciamento de acordo com o código passado.
     * @param codigo Código do status de credenciamento que deve ser recuperado.
     * @return Retorna um status de credenciamento
     * @throws IllegalArgumentException Caso o código do status de credenciamento informádo seja inválido
     */
    public static StatusCredenciamentoEnum recuperaStatusCredenciamento(String codigo) {
        StatusCredenciamentoEnum statusCredenciamento = null;

        for (StatusCredenciamentoEnum status : StatusCredenciamentoEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusCredenciamento = status;
                break;
            }
        }

        if (statusCredenciamento == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.solicitacao.invalido", (AcessoSistema) null));
        }

        return statusCredenciamento;
    }

    public final boolean equals(StatusCredenciamentoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }
}
