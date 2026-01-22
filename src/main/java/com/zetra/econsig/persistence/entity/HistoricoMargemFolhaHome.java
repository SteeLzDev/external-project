package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoMargemFolhaHome</p>
 * <p>Description: CRUD para histórico margem folha</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public class HistoricoMargemFolhaHome extends AbstractEntityHome {

    public static List<HistoricoMargemFolha> findByRsePeriodoMarCodigo(String rseCodigo, Short marCodigo, String periodo) throws FindException {
        final String query = "FROM HistoricoMargemFolha AS hma WHERE hma.id.rseCodigo = :rseCodigo AND hma.id.marCodigo = :marCodigo AND hma.id.hmaPeriodo = :periodo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("marCodigo", marCodigo);
        parameters.put("periodo", DateHelper.objectToDate(periodo));

        return findByQuery(query, parameters);
    }

    public static HistoricoMargemFolha create(String rseCodigo, Short marCodigo, Date hmaPeriodo, Date hmaData, BigDecimal hmaMargemFolha) throws CreateException {
        final HistoricoMargemFolha bean = new HistoricoMargemFolha();

        final HistoricoMargemFolhaId id = new HistoricoMargemFolhaId(rseCodigo, marCodigo, hmaPeriodo);
        bean.setId(id);
        bean.setHmaData(hmaData);
        bean.setHmaMargemFolha(hmaMargemFolha);
        create(bean);
        return bean;
    }


    public static void removeByRse(String rseCodigo) throws RemoveException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM HistoricoMargemFolha hma WHERE hma.registroServidor.rseCodigo = :rseCodigo ");

            final MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static List<HistoricoMargemFolha> findByRseFilters(String rseCodigo, Date periodoIni, Date periodoFim, Short marCodigo) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append("FROM HistoricoMargemFolha AS hma WHERE hma.id.rseCodigo = :rseCodigo ");

        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            query.append(" AND hma.hmaPeriodo BETWEEN :periodoIni AND :periodoFim ");
        } else if (!TextHelper.isNull(periodoIni)) {
            query.append(" AND hma.hmaPeriodo = :periodoIni ");
        } else if (!TextHelper.isNull(periodoFim)) {
            query.append(" AND hma.hmaPeriodo = :periodoFim");
        }

        if (!TextHelper.isNull(marCodigo)) {
            query.append(" AND hma.marCodigo = :marCodigo ");
        }

        query.append(" ORDER BY hma.marCodigo, hma.hmaPeriodo ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            parameters.put("periodoIni", periodoIni);
            parameters.put("periodoFim", periodoFim);
        } else if (!TextHelper.isNull(periodoIni)) {
            parameters.put("periodoIni", periodoIni);
        } else if (!TextHelper.isNull(periodoFim)) {
            parameters.put("periodoFim", periodoFim);
        }

        if (!TextHelper.isNull(marCodigo)) {
            parameters.put("marCodigo", marCodigo);
        }

        return findByQuery(query.toString(), parameters);
    }
}
