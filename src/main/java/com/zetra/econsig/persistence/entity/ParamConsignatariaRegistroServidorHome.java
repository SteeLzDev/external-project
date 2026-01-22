package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParamConsignatariaRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade ParamConsignatariaRegistroSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamConsignatariaRegistroServidorHome extends AbstractEntityHome {

    public static ParamConsignatariaRegistroSer findByPrimaryKey(ParamConsignatariaRegistroSerId pk) throws FindException {
        ParamConsignatariaRegistroSer paramCsaRse = new ParamConsignatariaRegistroSer();
        paramCsaRse.setId(pk);
        return find(paramCsaRse, pk);
    }

    public static ParamConsignatariaRegistroSer create(String tpaCodigo, String rseCodigo, String csaCodigo, String prcVlr, String pcrObs) throws CreateException {
        ParamConsignatariaRegistroSer bean = new ParamConsignatariaRegistroSer();
        ParamConsignatariaRegistroSerId id = new ParamConsignatariaRegistroSerId();
        id.setRseCodigo(rseCodigo);
        id.setCsaCodigo(csaCodigo);
        id.setTpaCodigo(tpaCodigo);
        bean.setId(id);
        bean.setPrcVlr(prcVlr);
        bean.setPrcObs(pcrObs);
        bean.setPrcDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ParamConsignatariaRegistroSer psr WHERE psr.registroServidor.rseCodigo = :rseCodigo ");

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
