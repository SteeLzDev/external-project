package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusPropostaEnum</p>
 * <p>Description: Enumeração de status de Proposta.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusPropostaEnum {

    AGUARDANDO_APROVACAO("1"),
    APROVADA("2"),
    FINALIZADA("3"),
    REJEITADA("4"),
    EXPIRADA("5");

    private String codigo;

    private StatusPropostaEnum(String codigo) {
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
     * Recupera um status de proposta de acordo com o código passado.
     * @param codigo Código do status de proposta que deve ser recuperado.
     * @return Retorna um status de proposta
     * @throws IllegalArgumentException Caso o código do status de proposta informádo seja inválido
     */
    public static StatusPropostaEnum recuperaStatusProposta(String codigo) {
        StatusPropostaEnum statusProposta = null;

        for (StatusPropostaEnum status : StatusPropostaEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusProposta = status;
                break;
            }
        }

        if (statusProposta == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.proposta.invalido", (AcessoSistema) null));
        }

        return statusProposta;
    }

    public final boolean equals(StatusPropostaEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }
}
