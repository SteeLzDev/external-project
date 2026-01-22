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
 * <p>Title: DadosOrgaoHome</p>
 * <p>Description: Classe Home para a entidade DadosOrgao</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosOrgaoHome extends AbstractEntityHome {

    public static DadosOrgao findByPrimaryKey(DadosOrgaoId pk) throws FindException {
        DadosOrgao dadosOrgao = new DadosOrgao();
        dadosOrgao.setId(pk);
        return find(dadosOrgao, pk);
    }

    public static List<DadosOrgao> findByOrgCodigo(String orgCodigo) throws FindException {
        String query = "FROM DadosOrgao dao WHERE dao.orgao.orgCodigo = :orgCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCodigo", orgCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosOrgao create(String orgCodigo, String tdaCodigo, String daoValor) throws CreateException {
        DadosOrgao bean = new DadosOrgao();

        DadosOrgaoId id = new DadosOrgaoId();
        id.setOrgCodigo(orgCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDaoValor(daoValor);

        create(bean);
        return bean;
    }

    public static void removeByOrg(String orgCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DadosOrgao dao WHERE dao.orgao.orgCodigo = :orgCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("orgCodigo", orgCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
