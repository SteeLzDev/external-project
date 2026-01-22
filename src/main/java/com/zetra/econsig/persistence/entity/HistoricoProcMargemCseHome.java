package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoProcMargemCseHome</p>
 * <p>Description: Classe Home para a entidade HistoricoProcMargemCse</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoProcMargemCseHome extends AbstractEntityHome {

    public static HistoricoProcMargemCse findByPrimaryKey(HistoricoProcMargemCseId id) throws FindException {
        HistoricoProcMargemCse bean = new HistoricoProcMargemCse();
        bean.setId(id);
        return find(bean, id);
    }

    public static HistoricoProcMargemCse create(String cseCodigo, Long hpmCodigo) throws CreateException {
        HistoricoProcMargemCse bean = new HistoricoProcMargemCse();

        HistoricoProcMargemCseId id = new HistoricoProcMargemCseId();
        id.setCseCodigo(cseCodigo);
        id.setHpmCodigo(hpmCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }

    public static void removerHistorico(Long hpmCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM HistoricoProcMargemCse hpm ");
            hql.append("WHERE hpm.historicoProcMargem.hpmCodigo = :hpmCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("hpmCodigo", hpmCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
