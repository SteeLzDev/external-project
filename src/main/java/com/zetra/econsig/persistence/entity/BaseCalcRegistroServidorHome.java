package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
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
 * <p>Title: BaseCalcRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade BaseCalcRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2002-20014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BaseCalcRegistroServidorHome extends AbstractEntityHome {

    public static BaseCalcRegistroServidor findByPrimaryKey(BaseCalcRegistroServidorId pk) throws FindException {
        BaseCalcRegistroServidor bean = new BaseCalcRegistroServidor();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static List<BaseCalcRegistroServidor> findByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM BaseCalcRegistroServidor bcs WHERE bcs.registroServidor.rseCodigo = :rseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        return findByQuery(query, parameters);
    }

    public static BigDecimal getBcsValor(String rseCodigo, String tbcCodigo) {
        BaseCalcRegistroServidor bean = null;

        try {
            bean = findByPrimaryKey(new BaseCalcRegistroServidorId(tbcCodigo, rseCodigo));
        } catch (FindException ex) {
        }

        return (bean != null ? bean.getBcsValor() : null);
    }

    public static BaseCalcRegistroServidor create(String tbcCodigo, String rseCodigo, BigDecimal bcsValor) throws CreateException {
        BaseCalcRegistroServidor bean = new BaseCalcRegistroServidor();

        BaseCalcRegistroServidorId id = new BaseCalcRegistroServidorId();
        id.setTbcCodigo(tbcCodigo);
        id.setRseCodigo(rseCodigo);
        bean.setId(id);
        bean.setBcsValor(bcsValor);

        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BaseCalcRegistroServidor bcs WHERE bcs.registroServidor.rseCodigo = :rseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}
