package com.zetra.econsig.values;

/**
 * <p>Title: TipoEnderecoEnum </p>
 * <p>Description: Classe Enum que contem os tipos de endereços </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum TipoEnderecoEnum {

    OUTRO("1"),
    RESIDENCIAL("2"),
    COMERCIAL("3"),
    FISCAL("4"),
    COBRANCA("5");

    private String codigo;

    private TipoEnderecoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
