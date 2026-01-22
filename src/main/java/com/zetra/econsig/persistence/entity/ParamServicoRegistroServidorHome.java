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
 * <p>Title: ParamServicoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade ParamServicoRegistroSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamServicoRegistroServidorHome extends AbstractEntityHome {

    public static ParamServicoRegistroSer findByPrimaryKey(ParamServicoRegistroSerId pk) throws FindException {
        ParamServicoRegistroSer paramSvcRse = new ParamServicoRegistroSer();
        paramSvcRse.setId(pk);
        return find(paramSvcRse, pk);
    }

    public static ParamServicoRegistroSer create(String tpsCodigo, String rseCodigo, String svcCodigo, String psrVlr, String pcrObs, String psrAlteradoPeloServidor) throws CreateException {
        ParamServicoRegistroSer bean = new ParamServicoRegistroSer();
        ParamServicoRegistroSerId id = new ParamServicoRegistroSerId();
        id.setRseCodigo(rseCodigo);
        id.setSvcCodigo(svcCodigo);
        id.setTpsCodigo(tpsCodigo);
        bean.setId(id);
        bean.setPsrVlr(psrVlr);
        bean.setPsrObs(pcrObs);
        bean.setPsrAlteradoPeloServidor(psrAlteradoPeloServidor);
        bean.setPsrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ParamServicoRegistroSer psr WHERE psr.registroServidor.rseCodigo = :rseCodigo ");

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
