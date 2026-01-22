package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PalavraChaveHome</p>
 * <p>Description: Classe Home para a entidade RegraConvenio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraConvenioHome extends AbstractEntityHome {

    public static RegraConvenio findByPrimaryKey(String rcCodigo) throws FindException {
    	RegraConvenio regraConvenio = new RegraConvenio();
    	regraConvenio.setRcoCodigo(rcCodigo);
        return find(regraConvenio, rcCodigo);
    }
}
