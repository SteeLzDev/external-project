package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

public class PontuacaoRseCsaHome extends AbstractEntityHome {

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM PontuacaoRseCsa psr WHERE psr.registroServidor.rseCodigo = :rseCodigo ");

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
