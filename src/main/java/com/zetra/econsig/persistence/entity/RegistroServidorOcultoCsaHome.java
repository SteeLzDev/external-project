package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RegistroServidorOcultoCsaHome</p>
 * <p>Description: Classe Home para a entidade RegistroServidorOcultoCsaHome</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegistroServidorOcultoCsaHome extends AbstractEntityHome {

    public static RegistroServidorOcultoCsa create(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws CreateException {
        Session session = SessionUtil.getSession();
        RegistroServidorOcultoCsaId id = new RegistroServidorOcultoCsaId(rseCodigo, csaCodigo);
        RegistroServidorOcultoCsa bean = null;

        try {
            bean = findByPrimaryKey(id);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));

            RelacionamentoRegistroServidorHome.update(bean);

        } catch (UpdateException | FindException ex) {
            bean = new RegistroServidorOcultoCsa();
            bean.setId(id);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            create(bean, session);

        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static RegistroServidorOcultoCsa findByPrimaryKey(RegistroServidorOcultoCsaId pk) throws FindException {
        RegistroServidorOcultoCsa bean = new RegistroServidorOcultoCsa();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM RegistroServidorOcultoCsa roc WHERE roc.registroServidor.rseCodigo = :rseCodigo");

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
