package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: DestinatarioEmailSerHome</p>
 * <p>Description: Home da entidade DestinatarioEmailSer
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Alexandre Fernandes
 */
public class DestinatarioEmailSerHome extends AbstractEntityHome {

    public static DestinatarioEmailSer findByPrimaryKey(String funCodigo, String papCodigo, String serCodigo) throws FindException {
        return findByPrimaryKey(new DestinatarioEmailSerId(funCodigo, papCodigo, serCodigo));
    }

    public static DestinatarioEmailSer findByPrimaryKey(DestinatarioEmailSerId pk) throws FindException {
        DestinatarioEmailSer bean = new DestinatarioEmailSer();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static DestinatarioEmailSer create(String funCodigo, String papCodigo, String serCodigo, String desReceber) throws CreateException {
        DestinatarioEmailSer bean = new DestinatarioEmailSer();

        DestinatarioEmailSerId id = new DestinatarioEmailSerId();
        id.setFunCodigo(funCodigo);
        id.setPapCodigo(papCodigo);
        id.setSerCodigo(serCodigo);
        bean.setId(id);
        bean.setDesReceber(desReceber);

        create(bean);
        return bean;
    }

}
