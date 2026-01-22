package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

public class TipoBeneficiarioHome extends AbstractEntityHome {

    public static TipoBeneficiario findByPrimaryKey(String tibCodigo) throws FindException {
        TipoBeneficiario tipoBeneficiario = new TipoBeneficiario();
        tipoBeneficiario.setTibCodigo(tibCodigo);

        return find(tipoBeneficiario, tibCodigo);
    }
}
