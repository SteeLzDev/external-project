package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: DadosServidorHome</p>
 * <p>Description: Classe Home para a entidade DadosServidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosServidorHome extends AbstractEntityHome {

    public static DadosServidor findByPrimaryKey(DadosServidorId pk) throws FindException {
        DadosServidor dadosAutDesconto = new DadosServidor();
        dadosAutDesconto.setId(pk);
        return find(dadosAutDesconto, pk);
    }

    public static List<DadosServidor> findBySerCodigo(String serCodigo) throws FindException {
        String query = "FROM DadosServidor das WHERE das.servidor.serCodigo = :serCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosServidor create(String serCodigo, String tdaCodigo, String dasValor) throws CreateException {
        DadosServidor bean = new DadosServidor();

        DadosServidorId id = new DadosServidorId();
        id.setSerCodigo(serCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDasValor(dasValor);

        create(bean);
        return bean;
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DadosServidor das WHERE das.servidor.serCodigo = :serCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("serCodigo", serCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
