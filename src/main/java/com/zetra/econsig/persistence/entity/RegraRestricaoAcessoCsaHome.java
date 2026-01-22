package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;

/**
 * <p>Title: RegraRestricaoAcessoCsaHome</p>
 * <p>Description: CRUD para RegraRestricaoAcesso.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraRestricaoAcessoCsaHome extends AbstractEntityHome {
	
	public static RegraRestricaoAcessoCsa create (String rraCodigo, String csaCodigo) throws CreateException {
		RegraRestricaoAcessoCsa bean = new RegraRestricaoAcessoCsa();

		RegraRestricaoAcessoCsaId id = new RegraRestricaoAcessoCsaId();
        id.setCsaCodigo(csaCodigo);
        id.setRraCodigo(rraCodigo);
        bean.setId(id);
                
        create(bean);
        return bean;
	}
} 
