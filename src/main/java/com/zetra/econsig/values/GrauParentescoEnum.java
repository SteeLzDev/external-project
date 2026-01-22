package com.zetra.econsig.values;

/**
 * <p>Title: GrauParentescoEnum </p>
 * <p>Description: Enumeração de Grau de Parentesco.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum GrauParentescoEnum {

    CONJUGE("1"),
    FILHO("2"),
    COMPANHEIRO("3"),
    PAI("4"),
    MAE("5"),
    TUTELADO("6"),
    MENOR_SOB_GUARDA("15"),
    IRMAO("16"),
    PADRASTO("18"),
    MADRASTA("28"),
    ENTEADO("29"),
    CURATELADO("30");

    private String codigo;

    private GrauParentescoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean equals(String codigo) {
        return this.codigo.equals(codigo);
    }

    public static boolean permiteEdicaoDataCasamento(String codigo) {
        if (CONJUGE.equals(codigo) || COMPANHEIRO.equals(codigo)) {
            return true;
        }

        return false;
    }
}
