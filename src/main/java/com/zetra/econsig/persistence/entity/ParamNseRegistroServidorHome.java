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
 * <p>Title: ParamNseRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade ParamNseRegistroSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamNseRegistroServidorHome extends AbstractEntityHome {

    public static ParamNseRegistroSer findByPrimaryKey(ParamNseRegistroSerId pk) throws FindException {
        ParamNseRegistroSer paramNseRse = new ParamNseRegistroSer();
        paramNseRse.setId(pk);
        return find(paramNseRse, pk);
    }

    public static ParamNseRegistroSer create(String tpsCodigo, String rseCodigo, String nseCodigo, String pnrVlr, String pnrObs, String pnrAlteradoPeloServidor) throws CreateException {
        ParamNseRegistroSer bean = new ParamNseRegistroSer();
        ParamNseRegistroSerId id = new ParamNseRegistroSerId();
        id.setRseCodigo(rseCodigo);
        id.setNseCodigo(nseCodigo);
        id.setTpsCodigo(tpsCodigo);
        bean.setId(id);
        bean.setPnrVlr(pnrVlr);
        bean.setPnrObs(pnrObs);
        bean.setPnrAlteradoPeloServidor(pnrAlteradoPeloServidor);
        bean.setPnrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ParamNseRegistroSer pnr WHERE pnr.registroServidor.rseCodigo = :rseCodigo ");

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
