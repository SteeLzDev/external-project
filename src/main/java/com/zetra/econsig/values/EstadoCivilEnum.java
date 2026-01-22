package com.zetra.econsig.values;

/**
 * <p>Title: EstadoCivilEnum </p>
 * <p>Description: Enumeração de Estado Civil.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum EstadoCivilEnum {

    CASADO("C"),
    DESQUITADO("D"),
    DIVORCIADO("I"),
    SEPARADO_JUDICIALMENTE("J"),
    MARITAL("M"),
    CONCUBINATO("N"),
    OUTROS("O"),
    SOLTEIRO("S"),
    VIUVO("V");

    private String codigo;

    private EstadoCivilEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean equals(String codigo) {
        return this.codigo.equals(codigo);
    }
}
