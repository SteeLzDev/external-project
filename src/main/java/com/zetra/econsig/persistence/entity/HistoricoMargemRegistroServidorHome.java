package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoMargemRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade HistoricoMargemRse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoMargemRegistroServidorHome extends AbstractEntityHome {

    public static HistoricoMargemRse findByPrimaryKey(Integer hmrCodigo) throws FindException {
        HistoricoMargemRse historicoMargem = new HistoricoMargemRse();
        historicoMargem.setHmrCodigo(hmrCodigo);
        return find(historicoMargem, hmrCodigo);
    }

    public static HistoricoMargemRse create(String rseCodigo, Short marCodigo, String ocaCodigo, String hmrOperacao,
            BigDecimal hmrMargemAntes, BigDecimal hmrMargemDepois) throws CreateException {

        Session session = SessionUtil.getSession();
        HistoricoMargemRse bean = new HistoricoMargemRse();

        try {
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setMargem(session.getReference(Margem.class, marCodigo));
            bean.setHmrOperacao(hmrOperacao);
            bean.setHmrData(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setHmrMargemAntes(hmrMargemAntes);
            bean.setHmrMargemDepois(hmrMargemDepois);
            if (ocaCodigo != null) {
                bean.setOcorrenciaAutorizacao(session.getReference(OcorrenciaAutorizacao.class, ocaCodigo));
            }
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM HistoricoMargemRse hmr WHERE hmr.registroServidor.rseCodigo = :rseCodigo ");

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
