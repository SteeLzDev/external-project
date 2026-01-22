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
 * <p>Title: DadosCorrespondenteHome</p>
 * <p>Description: Classe Home para a entidade DadosCorrespondente</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosCorrespondenteHome extends AbstractEntityHome {

    public static DadosCorrespondente findByPrimaryKey(DadosCorrespondenteId pk) throws FindException {
        DadosCorrespondente dadosAutDesconto = new DadosCorrespondente();
        dadosAutDesconto.setId(pk);
        return find(dadosAutDesconto, pk);
    }

    public static List<DadosCorrespondente> findByCorCodigo(String corCodigo) throws FindException {
        String query = "FROM DadosCorrespondente dar WHERE dar.correspondente.corCodigo = :corCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("corCodigo", corCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosCorrespondente create(String corCodigo, String tdaCodigo, String darValor) throws CreateException {
        DadosCorrespondente bean = new DadosCorrespondente();

        DadosCorrespondenteId id = new DadosCorrespondenteId();
        id.setCorCodigo(corCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDarValor(darValor);

        create(bean);
        return bean;
    }

    public static void removeByCor(String corCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DadosCorrespondente dar WHERE dar.correspondente.corCodigo = :corCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("corCodigo", corCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
