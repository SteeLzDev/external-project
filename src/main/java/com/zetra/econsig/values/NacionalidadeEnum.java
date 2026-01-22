package com.zetra.econsig.values;

/**
 * <p>Title: NacionalidadeEnum </p>
 * <p>Description: Classe Enum que contem as nacionalidades </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum NacionalidadeEnum {
    BRASILEIRO("1"),
    ESTRANGEIRO("2");

    public String nacCodigo;

    private NacionalidadeEnum(String nacCodigo) {
        this.nacCodigo = nacCodigo;
    }

    public boolean equals(String nacCodigo) {
        return this.nacCodigo.equals(nacCodigo);
    }
}
