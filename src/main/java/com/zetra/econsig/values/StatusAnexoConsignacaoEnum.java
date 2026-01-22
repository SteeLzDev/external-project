package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

public enum StatusAnexoConsignacaoEnum {
    INATIVO(Short.valueOf("0")),
    ATIVO(Short.valueOf("1"));

    private Short codigo;

    private StatusAnexoConsignacaoEnum(Short codigo) {
        this.codigo = codigo;
    }

    public Short getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo.toString();
    }

    public static StatusAnexoConsignacaoEnum recuperaStatusProposta(Short codigo) {
        StatusAnexoConsignacaoEnum statusAnexoAdeEnum = null;

        for (StatusAnexoConsignacaoEnum status : StatusAnexoConsignacaoEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusAnexoAdeEnum = status;
                break;
            }
        }

        if (statusAnexoAdeEnum == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.anexo.consignacao", (AcessoSistema) null));
        }

        return statusAnexoAdeEnum;
    }

    public final boolean equals(StatusAnexoConsignacaoEnum other) {
        return (this == other || getCodigo().equals(other.getCodigo()));
    }
}
