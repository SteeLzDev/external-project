package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoSolicitacaoEnum</p>
 * <p>Description: Enumeração de tipo de solicitação.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum OrigemSolicitacaoEnum {

    ORIGEM_INCLUSAO("1"),
    ORIGEM_ALTERACAO_PARA_MAIOR("2"),
    ORIGEM_RENEGOCIACAO("3"),
    ORIGEM_COMPRA("4"),
    ORIGEM_REIMPLANTE("5");

    private String codigo;

    private OrigemSolicitacaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }

    public static OrigemSolicitacaoEnum recuperaOrigemSolicitacao(String codigo) {
        OrigemSolicitacaoEnum origemSolicitacao = null;

        for (OrigemSolicitacaoEnum tipo : OrigemSolicitacaoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                origemSolicitacao = tipo;
                break;
            }
        }

        if (origemSolicitacao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.origem.solicitacao.invalido", (AcessoSistema) null));
        }

        return origemSolicitacao;
    }

    public final boolean equals(OrigemSolicitacaoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }
}
