package com.zetra.econsig.values;

/**
 * <p>Title: IdentificadorAnexosEmLoteEnum</p>
 * <p>Description: Enumeration dos identificadores de contratos em upload de anexos de ade em lote.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum IdentificadorAnexosEmLoteEnum {
    ADE_IDENTIFICADOR(Columns.ADE_IDENTIFICADOR),
    ADE_NUMERO(Columns.ADE_NUMERO);

    private String codigo;

    private IdentificadorAnexosEmLoteEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
