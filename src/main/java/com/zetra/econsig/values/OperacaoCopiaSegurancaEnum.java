package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: OperacaoCopiaSegurancaEnum</p>
 * <p>Description: Enumeração do operações de cópia de segurança.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum OperacaoCopiaSegurancaEnum {

    BACKUP("1"),
    RESTORE("2");

    private String codigo;

    private OperacaoCopiaSegurancaEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static OperacaoCopiaSegurancaEnum recuperaOperacao(String codigo) {
        OperacaoCopiaSegurancaEnum operacao = null;

        for (OperacaoCopiaSegurancaEnum ocs : OperacaoCopiaSegurancaEnum.values()) {
            if (ocs.getCodigo().equals(codigo)) {
                operacao = ocs;
                break;
            }
        }

        if (operacao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.informado.operacao.copia.seguranca.invalido", (AcessoSistema) null));
        }

        return operacao;
    }

    public final boolean equals(OperacaoCopiaSegurancaEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
