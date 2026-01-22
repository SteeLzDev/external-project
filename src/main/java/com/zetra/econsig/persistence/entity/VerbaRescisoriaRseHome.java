package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;

/**
 * <p>Title: VerbaRescisoriaRseHome</p>
 * <p>Description: Classe Home para a entidade VerbaRescisoriaRse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerbaRescisoriaRseHome extends AbstractEntityHome {

    public static VerbaRescisoriaRse findByPrimaryKey(String vrrCodigo) throws FindException {
        VerbaRescisoriaRse verbaRescisoriaRse = new VerbaRescisoriaRse();
        verbaRescisoriaRse.setVrrCodigo(vrrCodigo);
        return find(verbaRescisoriaRse, vrrCodigo);
    }

    public static List<VerbaRescisoriaRse> findByPrimaryRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM VerbaRescisoriaRse vrr WHERE vrr.registroServidor.rseCodigo = :rseCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        return findByQuery(query, parameters);
    }

    public static VerbaRescisoriaRse create(String rseCodigo, String svrCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        VerbaRescisoriaRse bean = new VerbaRescisoriaRse();
        String objectId = null;

        try {
            objectId = DBHelper.getNextId();
            bean.setVrrCodigo(objectId);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setStatusVerbaRescisoria(session.getReference(StatusVerbaRescisoria.class, svrCodigo));
            bean.setVrrDataIni(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setVrrDataUltAtualizacao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setVrrProcessado("N"); // Valor Default
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM VerbaRescisoriaRse vrr WHERE vrr.registroServidor.rseCodigo = :rseCodigo ");

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

    public static void createLote(VerbaRescisoriaRse verbaRescisoria) throws CreateException {
        Session session = SessionUtil.getSession();
        VerbaRescisoriaRse bean = new VerbaRescisoriaRse();
        String objectId = null;

        try {
            objectId = DBHelper.getNextId();
            bean.setVrrCodigo(objectId);
            bean.setRseCodigo(verbaRescisoria.getRseCodigo());
            bean.setSvrCodigo(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());
            bean.setVrrDataIni(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setVrrDataFim(null);
            bean.setVrrDataUltAtualizacao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setVrrValor(verbaRescisoria.getVrrValor());
            bean.setVrrProcessado("N"); // Valor Default
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateVrrLote(VerbaRescisoriaRse verbaRecisoria) throws UpdateException {
        Session session = SessionUtil.getSession();

        try {
            update(verbaRecisoria);
        } catch (UpdateException e) {
            throw new UpdateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}
