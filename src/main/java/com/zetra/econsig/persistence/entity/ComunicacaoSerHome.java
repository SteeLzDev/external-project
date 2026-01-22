package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;

/**
 * <p>Title: ComunicacaoSerHome</p>
 * <p>Description: Home do bean class ComunicacaoSer.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoSerHome extends AbstractEntityHome {

    public static ComunicacaoSer create(String cmnCodigo, String serCodigo, String rseCodigo, String cmsDestinatario) throws CreateException {
        ComunicacaoSerId id = new ComunicacaoSerId(cmnCodigo, serCodigo, rseCodigo);

        ComunicacaoSer bean = new ComunicacaoSer();
        bean.setId(id);
        bean.setCmsDestinatario(cmsDestinatario);

        create(bean);
        return bean;
    }
}
