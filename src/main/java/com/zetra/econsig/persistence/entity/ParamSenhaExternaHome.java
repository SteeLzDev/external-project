package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ParamSenhaExternaHome</p>
 * <p>Description: Classe Home para a entidade ParamSenhaExterna</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSenhaExternaHome extends AbstractEntityHome {

    public static ParamSenhaExterna findByPrimaryKey(String psxChave) throws FindException {
        ParamSenhaExterna paramSenhaExterna = new ParamSenhaExterna();
        paramSenhaExterna.setPsxChave(psxChave);
        return find(paramSenhaExterna, psxChave);
    }

    public static ParamSenhaExterna create(String psxChave, String psxValor) throws CreateException {
        ParamSenhaExterna paramSenhaExterna = new ParamSenhaExterna();
        paramSenhaExterna.setPsxChave(psxChave);
        paramSenhaExterna.setPsxValor(psxValor);
        create(paramSenhaExterna);
        return paramSenhaExterna;
    }
}
