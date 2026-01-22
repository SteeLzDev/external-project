package com.zetra.econsig.values;

/**
 * <p>Title: TipoRegistroServidorEnum </p>
 * <p>Description: Enumeração do Tipo de Registro Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum TipoRegistroServidorEnum {

    PADRAO("0"),
    EXTERIOR("1"),
    TEMPORARIO("2"),
    PENSIONISTA("3");

    private final String codigo;

    private TipoRegistroServidorEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean equals(String codigo) {
        return this.codigo.equals(codigo);
    }
}
