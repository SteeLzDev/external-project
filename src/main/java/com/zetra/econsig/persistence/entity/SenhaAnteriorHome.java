package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: SenhaAnteriorHome</p>
 * <p>Description: Classe Home para a entidade SenhaAnterior</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SenhaAnteriorHome extends AbstractEntityHome {

    public static SenhaAnterior findByPrimaryKey(SenhaAnteriorId pk) throws FindException {
        SenhaAnterior SenhaAnterior = new SenhaAnterior();
        SenhaAnterior.setId(pk);
        return find(SenhaAnterior, pk);
    }

    public static SenhaAnterior create(String usuCodigo, String seaSenha, Date seaData) throws CreateException {

        Session session = SessionUtil.getSession();
        SenhaAnterior bean = new SenhaAnterior();
        try {
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setSeaData(seaData);
            SenhaAnteriorId id = new SenhaAnteriorId();
            id.setUsuCodigo(usuCodigo);
            id.setSeaSenha(seaSenha);
            bean.setId(id);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
