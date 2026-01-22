package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: DestinatarioEmailCseHome</p>
 * <p>Description: Home da entidade DestinatarioEmailCse
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */
public class DestinatarioEmailCseHome extends AbstractEntityHome {

    public static DestinatarioEmailCse findByPrimaryKey(String funCodigo, String papCodigo, String cseCodigo) throws FindException {
        return findByPrimaryKey(new DestinatarioEmailCseId(funCodigo, papCodigo, cseCodigo));
    }

    public static DestinatarioEmailCse findByPrimaryKey(DestinatarioEmailCseId pk) throws FindException {
        final DestinatarioEmailCse bean = new DestinatarioEmailCse();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static DestinatarioEmailCse create(String funCodigo, String papCodigo, String cseCodigo, String deeReceber, String deeEmail) throws CreateException {
        final DestinatarioEmailCse bean = new DestinatarioEmailCse();

        final DestinatarioEmailCseId id = new DestinatarioEmailCseId();
        id.setFunCodigo(funCodigo);
        id.setPapCodigo(papCodigo);
        id.setCseCodigo(cseCodigo);
        bean.setId(id);
        bean.setDeeReceber(deeReceber);
        bean.setDeeEmail(deeEmail);

        create(bean);
        return bean;
    }

}
