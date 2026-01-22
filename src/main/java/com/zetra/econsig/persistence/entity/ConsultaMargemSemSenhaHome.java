package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

public class ConsultaMargemSemSenhaHome extends AbstractEntityHome {

    public static ConsultaMargemSemSenha findByPrimaryKey(String cssCodigo) throws FindException {
        final ConsultaMargemSemSenha consultaMargemSemSenha = new ConsultaMargemSemSenha();
        consultaMargemSemSenha.setCssCodigo(cssCodigo);
        return find(consultaMargemSemSenha, cssCodigo);
    }

    public static ConsultaMargemSemSenha create(String rseCodigo, String csaCodigo, Date cssDataIni, Date cssDataFim) throws CreateException {

        final Session session = SessionUtil.getSession();

        final ConsultaMargemSemSenha bean = new ConsultaMargemSemSenha();
        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCssCodigo(objectId);
            bean.setRseCcodigo(rseCodigo);
            bean.setCsaCodigo(csaCodigo);
            bean.setCssDataIni(cssDataIni);
            bean.setCssDataFim(cssDataFim);

            create(bean, session);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static List<ConsultaMargemSemSenha> findByRseCodigoCsaCodigo(String rseCodigo, String csaCodigo) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append(" FROM ConsultaMargemSemSenha cmss WHERE cmss.registroServidor.rseCodigo = :rseCodigo ");
        query.append(" AND cmss.cssDataFim >= current_timestamp() AND cmss.cssDataRevogacaoSer is NULL AND cmss.cssDataRevogacaoSup is NULL ");

        if (!TextHelper.isNull(csaCodigo)) {
            query.append(" AND cmss.csaCodigo = :csaCodigo ");
        }

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        if (!TextHelper.isNull(csaCodigo)) {
            parameters.put("csaCodigo", csaCodigo);
        }

        return findByQuery(query.toString(), parameters);
    }

    public static List<ConsultaMargemSemSenha> findByCsaCodigo(String csaCodigo) throws FindException {
        final String query = "FROM ConsultaMargemSemSenha cmss WHERE cmss.consignataria.csaCodigo = :csaCodigo "
                             + "AND cmss.cssDataFim >= current_timestamp() AND cmss.cssDataRevogacaoSer is NULL AND cmss.cssDataRevogacaoSup is NULL";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static List<ConsultaMargemSemSenha> findAlertaCsaByRseCodigoCsaCodigo(String rseCodigo, String csaCodigo) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append(" FROM ConsultaMargemSemSenha cmss WHERE cmss.registroServidor.rseCodigo = :rseCodigo ");
        query.append(" AND cmss.cssDataFim >= current_timestamp() AND (cmss.cssDataRevogacaoSer is NOT NULL OR cmss.cssDataRevogacaoSup is NOT NULL) AND cmss.cssDataAlerta IS NULL ");
        query.append(" AND cmss.csaCodigo = :csaCodigo ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query.toString(), parameters);
    }

    public static List<ConsultaMargemSemSenha> findAutorizacaoPrestesVencer(Integer qtdeDiasVencimentoAutorizacao) throws FindException {
        qtdeDiasVencimentoAutorizacao *= -1;
        final StringBuilder query = new StringBuilder();
        query.append(" FROM ConsultaMargemSemSenha cmss WHERE ");
        query.append(" cmss.cssDataFim >= current_timestamp() AND (cmss.cssDataRevogacaoSer is NULL AND cmss.cssDataRevogacaoSup is NULL) ");
        query.append(" AND data_corrente() = to_date(add_day(cmss.cssDataFim, :qtdeDiasVencimentoAutorizacao)) ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("qtdeDiasVencimentoAutorizacao", qtdeDiasVencimentoAutorizacao);

        return findByQuery(query.toString(), parameters);
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ConsultaMargemSemSenha psr WHERE psr.registroServidor.rseCodigo = :rseCodigo ");

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
}
