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
 * <p>Title: HistoricoMargemPeriodoHome</p>
 * <p>Description: Classe Home para a entidade HistoricoMargemPeriodo</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoMargemPeriodoHome extends AbstractEntityHome {

    public static HistoricoMargemPeriodo findByPrimaryKey(Integer hmpCodigo) throws FindException {
        HistoricoMargemPeriodo historicoMargem = new HistoricoMargemPeriodo();
        historicoMargem.setHmpCodigo(hmpCodigo);
        return find(historicoMargem, hmpCodigo);
    }

    public static HistoricoMargemPeriodo create(String rseCodigo, Short marCodigo, String hmpOperacao, BigDecimal hmpMargemAntes, BigDecimal hmpMargemDepois) throws CreateException {
        Session session = SessionUtil.getSession();
        HistoricoMargemPeriodo bean = new HistoricoMargemPeriodo();

        try {
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setMargem(session.getReference(Margem.class, marCodigo));
            bean.setHmpOperacao(hmpOperacao);
            bean.setHmpData(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setHmpMargemAntes(hmpMargemAntes);
            bean.setHmpMargemDepois(hmpMargemDepois);

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

            hql.append("DELETE FROM HistoricoMargemPeriodo hmp WHERE hmp.registroServidor.rseCodigo = :rseCodigo ");

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
