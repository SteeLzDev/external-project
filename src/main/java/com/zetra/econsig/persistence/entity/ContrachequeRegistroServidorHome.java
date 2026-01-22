package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ContrachequeRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade ContrachequeRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ContrachequeRegistroServidorHome extends AbstractEntityHome {

    public static ContrachequeRegistroSer findByPrimaryKey(ContrachequeRegistroSerId pk) throws FindException {
        ContrachequeRegistroSer contrachequeRegistroServidor = new ContrachequeRegistroSer();
        contrachequeRegistroServidor.setId(pk);
        return find(contrachequeRegistroServidor, pk);
    }

    public static ContrachequeRegistroSer create(String rseCodigo, Date ccqPeriodo, String ccqTexto) throws CreateException {
        ContrachequeRegistroSer bean = new ContrachequeRegistroSer();

        ContrachequeRegistroSerId id = new ContrachequeRegistroSerId();
        id.setRseCodigo(rseCodigo);
        id.setCcqPeriodo(ccqPeriodo);
        bean.setId(id);

        bean.setCcqDataCarga(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        bean.setCcqTexto(ccqTexto);

        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ContrachequeRegistroSer ccq WHERE ccq.registroServidor.rseCodigo = :rseCodigo ");

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
