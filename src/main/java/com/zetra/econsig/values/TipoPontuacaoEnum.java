package com.zetra.econsig.values;

/**
 * <p>Title: TipoPontuacaoEnum</p>
 * <p>Description: Enumeração dos tipos de pontuação.</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoPontuacaoEnum {

    PONTUACAO_INICIAL("1"),
    QTDE_CONTRATOS_SUSPENSOS("2"),
    QTDE_CONTRATOS_CONCLUIDOS("3"),
    PORCENTAGEM_MARGEM_UTILIZADA("4"),
    PORCENTAGEM_INADIMPLENCIA("5"),
    PONTOS_PERDIDOS_POR_LEILAO_NAO_CONCRETIZADO("6"),
    FAIXA_ETARIA_EM_ANOS("7"),
    FAIXA_TEMPO_SERVICO_EM_MESES("8"),
    FAIXA_SALARIAL("9"),
    FAIXA_VALOR_MARGEM("10"), /* NÃO IMPLEMENTADO */
    QTDE_CONTRATOS_LIQUIDADOS("11"),
    QTDE_CONTRATOS_LIQUIDADOS_GERAL("12"),
    QTDE_CONTRATOS_SUSPENSOS_GERAL("13"),
    QTDE_CONTRATOS_CONCLUIDOS_GERAL("14"),
    QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL("15"),
    QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL_GERAL("16"),
    PORCENTAGEM_INADIMPLENCIA_GERAL("17"),
    PORCENTAGEM_TURNOVER_CSE("18"), /* NÃO IMPLEMENTADO */
    QTDE_COLABORADORES_CSE("19"),
    MEDIA_SALARIAL_CSE("20"),
    MEDIA_MARGEM_LIVRE_CSE("21"), /* NÃO IMPLEMENTADO */
    PORCENTAGEM_DESCONTOS_CSA("22"); /* NÃO IMPLEMENTADO */

    private final String codigo;

    private TipoPontuacaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static TipoPontuacaoEnum recuperaTipo(String codigo) {
        TipoPontuacaoEnum acao = null;

        for (TipoPontuacaoEnum aca : TipoPontuacaoEnum.values()) {
            if (aca.getCodigo().equals(codigo)) {
                acao = aca;
                break;
            }
        }

        if (acao == null) {
            throw new IllegalArgumentException("Tipo pontuação inválido");
        }

        return acao;
    }

    public final boolean equals(TipoPontuacaoEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
