package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusSolicitacaoEnum</p>
 * <p>Description: Enumeração de status de solicitação.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusSolicitacaoEnum {

    PENDENTE("1"),
    CANCELADA("2"),
    FINALIZADA("3"),
    EXPIRADA("4"),
    PENDENTE_VALIDACAO_DOCUMENTOS("5"),
    PENDENTE_ASSINATURA_DOCUMENTACAO("6"),
    PENDENTE_INFORMACAO_DOCUMENTACAO("7"),
    DOCUMENTACAO_ENVIADA_PARA_ASSINATURA("8"),
    DOCUMENTACAO_ASSINADA_DIGITALMENTE("9"),
    VALIDACAO_DOCUMENTO_APROVADA("10"),
    VALIDACAO_DOCUMENTO_REPROVADA("11"),
    AGUARDANDO_DOCUMENTO("12"),
    AGUARDANDO_ANALISE_SUP("13");

    private String codigo;

    private StatusSolicitacaoEnum(String codigo) {
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
     * Recupera um status de solicitação de acordo com o código passado.
     * @param codigo Código do status de solicitação que deve ser recuperado.
     * @return Retorna um status de solicitação
     * @throws IllegalArgumentException Caso o código do status de solicitação informádo seja inválido
     */
    public static StatusSolicitacaoEnum recuperaStatusSolicitacao(String codigo) {
        StatusSolicitacaoEnum statusSolicitacao = null;

        for (StatusSolicitacaoEnum status : StatusSolicitacaoEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusSolicitacao = status;
                break;
            }
        }

        if (statusSolicitacao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.solicitacao.invalido", (AcessoSistema) null));
        }

        return statusSolicitacao;
    }

    public final boolean equals(StatusSolicitacaoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }
}
