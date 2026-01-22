package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CampoSistemaHome</p>
 * <p>Description: Classe Home para a entidade CampoSistema</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CampoSistemaHome extends AbstractEntityHome {

    public static CampoSistema findByPrimaryKey(String casChave) throws FindException {
        CampoSistema campoSistema = new CampoSistema();
        campoSistema.setCasChave(casChave);
        return find(campoSistema, casChave);
    }

    public static CampoSistema create(String casChave, String casValor) throws CreateException {
        CampoSistema campoSistema = new CampoSistema();
        campoSistema.setCasChave(casChave);
        campoSistema.setCasValor(casValor);
        create(campoSistema);
        return campoSistema;
    }
}
