package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: StatusDespesaComumHome</p>
 * <p>Description: Classe Home para a entidade StatusDespesaComum</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusDespesaComumHome extends AbstractEntityHome {

    public static StatusDespesaComum findByPrimaryKey(String sdcCodigo) throws FindException {
        StatusDespesaComum statusDespesaComum = new StatusDespesaComum();
        statusDespesaComum.setSdcCodigo(sdcCodigo);
        return find(statusDespesaComum, sdcCodigo);
    }
}
