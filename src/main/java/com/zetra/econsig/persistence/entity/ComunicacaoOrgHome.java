package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;

/**
 * <p>Title: ComunicacaoOrgHome</p>
 * <p>Description: Home do bean class ComunicacaoOrg.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoOrgHome extends AbstractEntityHome {

    public static ComunicacaoOrg create(String cmnCodigo, String orgCodigo, String cmoDestinatario) throws CreateException {
        ComunicacaoOrgId id = new ComunicacaoOrgId(cmnCodigo, orgCodigo);

        ComunicacaoOrg bean = new ComunicacaoOrg();
        bean.setId(id);
        bean.setCmoDestinatario(cmoDestinatario);

        create(bean);
        return bean;
    }
}
