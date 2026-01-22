package com.zetra.econsig.values;

/**
 * <p>Title: TipoBeneficiarioEnum </p>
 * <p>Description: Classe Enum que contem os tipos de beneficiario </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum TipoBeneficiarioEnum {
    TITULAR("1"),
    DEPENDENTE("2"),
    AGREGADO("3");

    public String tibCodigo;

    private TipoBeneficiarioEnum(String tibCodigo) {
        this.tibCodigo = tibCodigo;
    }

    public boolean equals(String tibCodigo) {
        return this.tibCodigo.equals(tibCodigo);
    }
}
