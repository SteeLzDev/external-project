package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: DestinatarioEmailCsaHome</p>
 * <p>Description: Classe Home para a entidade DestinatarioEmailCsa</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DestinatarioEmailCsaHome extends AbstractEntityHome {

    public static DestinatarioEmailCsa findByPrimaryKey(String funCodigo, String papCodigo, String csaCodigo) throws FindException {
        return findByPrimaryKey(new DestinatarioEmailCsaId(funCodigo, papCodigo, csaCodigo));
    }

    public static DestinatarioEmailCsa findByPrimaryKey(DestinatarioEmailCsaId pk) throws FindException {
        DestinatarioEmailCsa bean = new DestinatarioEmailCsa();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static DestinatarioEmailCsa create(String funCodigo, String papCodigo, String csaCodigo, String demReceber, String demEmail) throws CreateException {
        DestinatarioEmailCsa bean = new DestinatarioEmailCsa();

        DestinatarioEmailCsaId id = new DestinatarioEmailCsaId();
        id.setFunCodigo(funCodigo);
        id.setPapCodigo(papCodigo);
        id.setCsaCodigo(csaCodigo);
        bean.setId(id);
        bean.setDemReceber(demReceber);
        bean.setDemEmail(demEmail);

        create(bean);
        return bean;
    }

}
