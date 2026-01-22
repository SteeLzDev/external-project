package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;

/**
 * <p>Title: ComunicacaoCseHome</p>
 * <p>Description: Home do bean class ComunicacaoCse.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoCseHome extends AbstractEntityHome {

    public static ComunicacaoCse create(String cmnCodigo, String cseCodigo, String cmeDestinatario) throws CreateException {
        ComunicacaoCseId id = new ComunicacaoCseId(cmnCodigo, cseCodigo);

        ComunicacaoCse bean = new ComunicacaoCse();
        bean.setId(id);
        bean.setCmeDestinatario(cmeDestinatario);

        create(bean);
        return bean;
    }
}
