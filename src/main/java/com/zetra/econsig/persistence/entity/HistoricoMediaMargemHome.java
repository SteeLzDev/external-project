package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoMediaMargemHome</p>
 * <p>Description: Classe Home para a entidade HistoricoMediaMargem</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoMediaMargemHome extends AbstractEntityHome {

    public static HistoricoMediaMargem findByPrimaryKey(HistoricoMediaMargemId id) throws FindException {
        HistoricoMediaMargem bean = new HistoricoMediaMargem();
        bean.setId(id);
        return find(bean, id);
    }

    public static List<HistoricoMediaMargem> findByHpmCodigo(Integer hpmCodigo) throws FindException {
        String query = "FROM HistoricoMediaMargem hmm WHERE hmm.id.hpmCodigo = :hpmCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("hpmCodigo", hpmCodigo);

        List<HistoricoMediaMargem> result = findByQuery(query, parameters);
        if (result == null || result.isEmpty()) {
            throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
        }

        return result;
    }

    public static HistoricoMediaMargem create(Long hpmCodigo, Short marCodigo, BigDecimal hmmMediaMargemAntes, BigDecimal hmmMediaMargemDepois) throws CreateException {
        HistoricoMediaMargem bean = new HistoricoMediaMargem();

        HistoricoMediaMargemId id = new HistoricoMediaMargemId();
        id.setMarCodigo(marCodigo);
        id.setHpmCodigo(hpmCodigo);
        bean.setId(id);
        bean.setHmmMediaMargemAntes(hmmMediaMargemAntes);
        bean.setHmmMediaMargemDepois(hmmMediaMargemDepois);

        create(bean);
        return bean;
    }

    public static void removerHistorico(Long hpmCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM HistoricoMediaMargem hmm ");
            hql.append("WHERE hmm.historicoProcMargem.hpmCodigo = :hpmCodigo ");

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
