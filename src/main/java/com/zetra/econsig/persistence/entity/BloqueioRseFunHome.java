package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: BloqueioRseFunHome</p>
 * <p>Description: Classe Home para a entidade BloqueioRseFun</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioRseFunHome extends AbstractEntityHome {

    public static BloqueioRseFun findByPrimaryKey(BloqueioRseFunId pk) throws FindException {
        BloqueioRseFun bloqueioRseFun = new BloqueioRseFun();
        bloqueioRseFun.setId(pk);
        return find(bloqueioRseFun, pk);
    }

    public static BloqueioRseFun create(String rseCodigo, String funCodigo, Date brsDataLimite) throws CreateException {
        BloqueioRseFun bean = new BloqueioRseFun();

        bean.setId(new BloqueioRseFunId(rseCodigo, funCodigo));
        bean.setBrsDataLimite(brsDataLimite);
        create(bean);

        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BloqueioRseFun brs WHERE brs.registroServidor.rseCodigo = :rseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
