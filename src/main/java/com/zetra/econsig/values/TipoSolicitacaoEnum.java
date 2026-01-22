package com.zetra.econsig.values;

import java.util.Arrays;
import java.util.List;

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
public enum TipoSolicitacaoEnum {

    SOLICITACAO_SALDO_DEVEDOR("1"),
    SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO("2"),
    SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA("3"),
    SOLICITACAO_LIQUIDACAO_CONTRATO("4"),
    SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO("5"),
    SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO("6"),
    SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO("7"),
    SOLICITACAO_DEPENDE_AUTORIZACAO("8");

    private String codigo;

    private TipoSolicitacaoEnum(String codigo) {
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
     * Recupera um tipo de solicitação de acordo com o código passado.
     * @param codigo Código do tipo de solicitação que deve ser recuperado.
     * @return Retorna um tipo de solicitação
     * @throws IllegalArgumentException Caso o código do tipo de solicitação informádo seja inválido
     */
    public static TipoSolicitacaoEnum recuperaTipoSolicitacao(String codigo) {
        TipoSolicitacaoEnum tipoSolicitacao = null;

        for (TipoSolicitacaoEnum tipo : TipoSolicitacaoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoSolicitacao = tipo;
                break;
            }
        }

        if (tipoSolicitacao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.solicitacao.invalido", (AcessoSistema) null));
        }

        return tipoSolicitacao;
    }

    public final boolean equals(TipoSolicitacaoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }

    public static final List<TipoSolicitacaoEnum> getSolicitacoesSaldoDevedor() {
        return Arrays.asList(SOLICITACAO_SALDO_DEVEDOR, SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO, SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO);
    }
}
