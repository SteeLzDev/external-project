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
 * <p>Title: DadosConsignanteHome</p>
 * <p>Description: Classe Home para a entidade DadosConsignante</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosConsignanteHome extends AbstractEntityHome {

    public static DadosConsignante findByPrimaryKey(DadosConsignanteId pk) throws FindException {
        DadosConsignante dadosConsignante = new DadosConsignante();
        dadosConsignante.setId(pk);
        return find(dadosConsignante, pk);
    }

    public static List<DadosConsignante> findByCseCodigo(String cseCodigo) throws FindException {
        String query = "FROM DadosConsignante dac WHERE dac.consignante.cseCodigo = :cseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cseCodigo", cseCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosConsignante create(String cseCodigo, String tdaCodigo, String dacValor) throws CreateException {
        DadosConsignante bean = new DadosConsignante();

        DadosConsignanteId id = new DadosConsignanteId();
        id.setCseCodigo(cseCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDacValor(dacValor);

        create(bean);
        return bean;
    }

    public static void removeByCse(String cseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DadosConsignante dac WHERE dac.consignante.cseCodigo = :cseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("cseCodigo", cseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
