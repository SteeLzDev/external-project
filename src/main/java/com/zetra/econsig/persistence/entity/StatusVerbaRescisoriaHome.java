package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: StatusVerbaRescisoria</p>
 * <p>Description: Classe Home para a entidade StatusVerbaRescisoria</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusVerbaRescisoriaHome extends AbstractEntityHome {

    public static StatusVerbaRescisoria findByPrimaryKey(String svrCodigo) throws FindException {
        StatusVerbaRescisoria statusVerbaRescisoria = new StatusVerbaRescisoria();
        statusVerbaRescisoria.setSvrCodigo(svrCodigo);
        return find(statusVerbaRescisoria, svrCodigo);
    }
}
