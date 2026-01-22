package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ComunicacaoPermitidaHome</p>
 * <p>Description: Home do bean class ComunicacaoPermitida.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoPermitidaHome extends AbstractEntityHome {

    public static ComunicacaoPermitida create(String papCodigoRemetente, String papCodigoDestinatario) throws CreateException {
        ComunicacaoPermitidaId id = new ComunicacaoPermitidaId(papCodigoRemetente, papCodigoDestinatario);

        ComunicacaoPermitida bean = new ComunicacaoPermitida();
        bean.setId(id);

        create(bean);
        return bean;
    }

    public static List<ComunicacaoPermitida> listaComunicacaoPermitida() throws FindException {
        String query = "FROM ComunicacaoPermitida cmn";
        return findByQuery(query, null);
    }
}
