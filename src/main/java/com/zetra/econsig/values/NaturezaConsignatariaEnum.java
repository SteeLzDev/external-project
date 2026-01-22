package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: NaturezaConsignatariaEnum</p>
 * <p>Description: Enumeração de natureza de consignatária.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum NaturezaConsignatariaEnum {

    INSTITUICAO_FINANCEIRA("1"),
    ASSOCIACAO_SERVIDORES("2"),
    PREFEITURA_AERONAUTICA("3"),
    INSTITUICAO_FINANCEIRA_PUBLICA("4"),
    INSTITUICAO_FINANCEIRA_MISTA("5"),
    COOPERATIVA("6"),
    OPERACAO_PLANO_SAUDE("7"),
    SINDICATO("8"),
    SEGURADORA("9"),
    OPERADORA_BENEFICIOS("10"),
    ORGAO_PUBLICO("11"),
    OUTROS("99");

    /**
     * Recupera a natureza do consignatária de acordo com o código passado.
     * @param codigo Código da natureza de consignatária que deve ser recuperado.
     * @return Retorna uma natureza da consignatária
     * @throws IllegalArgumentException Caso o código da natureza de consignatária seja inválido
     */
    public static NaturezaConsignatariaEnum recuperaNaturezaConsignataria(String codigo) {
        NaturezaConsignatariaEnum natureza = null;

        for (NaturezaConsignatariaEnum bean : NaturezaConsignatariaEnum.values()) {
            if (bean.getCodigo().equals(codigo)) {
                natureza = bean;
                break;
            }
        }

        if (natureza == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.natureza.consignataria.invalido", (AcessoSistema) null));
        }

        return natureza;
    }

    private String codigo;

    private NaturezaConsignatariaEnum(String codigo) {
        this.codigo = codigo;
    }

    public final boolean equals(NaturezaConsignatariaEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }

    public String getCodigo() {
        return codigo;
    }
}
