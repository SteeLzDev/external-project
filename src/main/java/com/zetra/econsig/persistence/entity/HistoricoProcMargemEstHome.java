package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoProcMargemEstHome</p>
 * <p>Description: Classe Home para a entidade HistoricoProcMargemEst</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoProcMargemEstHome extends AbstractEntityHome {

    public static HistoricoProcMargemEst findByPrimaryKey(HistoricoProcMargemEstId id) throws FindException {
        HistoricoProcMargemEst bean = new HistoricoProcMargemEst();
        bean.setId(id);
        return find(bean, id);
    }

    public static HistoricoProcMargemEst create(String estCodigo, Long hpmCodigo) throws CreateException {
        HistoricoProcMargemEst bean = new HistoricoProcMargemEst();

        HistoricoProcMargemEstId id = new HistoricoProcMargemEstId();
        id.setEstCodigo(estCodigo);
        id.setHpmCodigo(hpmCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }

    public static void removerHistorico(Long hpmCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM HistoricoProcMargemEst hpm ");
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
