package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: TpsExigeConfirmacaoRenegociacaoValoresEnum</p>
 * <p>Description: Enumeração de tipos de valores de parâmetro de serviço que exige confirmação de renegociação.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24630 $
 * $Date: 2018-06-11 11:17:47 -0300 (seg, 11 jun 2018) $
 */
public enum TpsExigeConfirmacaoRenegociacaoValoresEnum {

    TODAS("T"),
    SOMENTE_PARA_MAIOR("A"),
    SOMENTE_PARA_MENOR("D"),
    NENHUMA("N");

    private String codigo;

    private TpsExigeConfirmacaoRenegociacaoValoresEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static TpsExigeConfirmacaoRenegociacaoValoresEnum recuperaTpsExigeConfirmacaoRenegociacao(String codigo) {
        if (TextHelper.isNull(codigo)) {
            return TODAS;
        }

        TpsExigeConfirmacaoRenegociacaoValoresEnum tpsEnum = null;

        for (TpsExigeConfirmacaoRenegociacaoValoresEnum valor : TpsExigeConfirmacaoRenegociacaoValoresEnum.values()) {
            if (valor.getCodigo().equals(codigo)) {
                tpsEnum = valor;
                break;
            }
        }

        if (tpsEnum == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.valor.incorreto", (AcessoSistema) null));
        }

        return tpsEnum;
    }

    public final boolean equals(TpsExigeConfirmacaoRenegociacaoValoresEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }
}
