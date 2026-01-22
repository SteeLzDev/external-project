package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: NaturezaPlanoEnum</p>
 * <p>Description: Enumeração de natureza de plano.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum NaturezaPlanoEnum {

    TAXA_USO("1"),
    TAXA_CONDOMINIO("2"),
    OUTROS("99");

    private String codigo;

    private NaturezaPlanoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera a natureza do plano de acordo com o código passado.
     * @param codigo Código da natureza do plano que deve ser recuperado.
     * @return Retorna uma natureza da consignatária
     * @throws IllegalArgumentException Caso o código da natureza do plano seja inválido
     */
    public static NaturezaPlanoEnum recuperaNaturezaPlano(String codigo) {
        NaturezaPlanoEnum natureza = null;

        for (NaturezaPlanoEnum bean : NaturezaPlanoEnum.values()) {
            if (bean.getCodigo().equals(codigo)) {
                natureza = bean;
                break;
            }
        }

        if (natureza == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.natureza.plano.invalido", (AcessoSistema) null));
        }

        return natureza;
    }

    public final boolean equals(NaturezaPlanoEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
