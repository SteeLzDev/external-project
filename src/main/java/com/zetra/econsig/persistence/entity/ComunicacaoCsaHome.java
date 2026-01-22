package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;

/**
 * <p>Title: ComunicacaoCsaHome</p>
 * <p>Description: Home do bean class ComunicacaoCsa.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoCsaHome extends AbstractEntityHome {

    public static ComunicacaoCsa create(String cmnCodigo, String csaCodigo, String cmcDestinatario) throws CreateException {
        ComunicacaoCsaId id = new ComunicacaoCsaId(cmnCodigo, csaCodigo);

        ComunicacaoCsa bean = new ComunicacaoCsa();
        bean.setId(id);
        bean.setCmcDestinatario(cmcDestinatario);

        create(bean);
        return bean;
    }
}
