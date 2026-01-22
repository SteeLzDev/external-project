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
 * <p>Title: DadosConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade DadosConsignataria</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosConsignatariaHome extends AbstractEntityHome {

    public static DadosConsignataria findByPrimaryKey(DadosConsignatariaId pk) throws FindException {
        DadosConsignataria dadosConsignataria = new DadosConsignataria();
        dadosConsignataria.setId(pk);
        return find(dadosConsignataria, pk);
    }

    public static List<DadosConsignataria> findByCsaCodigo(String csaCodigo) throws FindException {
        String query = "FROM DadosConsignataria daa WHERE daa.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosConsignataria create(String csaCodigo, String tdaCodigo, String daaValor) throws CreateException {
        DadosConsignataria bean = new DadosConsignataria();

        DadosConsignatariaId id = new DadosConsignatariaId();
        id.setCsaCodigo(csaCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDaaValor(daaValor);

        create(bean);
        return bean;
    }

    public static void removeByCsa(String csaCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DadosConsignataria daa WHERE daa.consignataria.csaCodigo = :csaCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("csaCodigo", csaCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
