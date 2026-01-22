package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CanalEnum;

/**
 * <p>Title: HistoricoConsultaMargemHome</p>
 * <p>Description: Classe Home para a entidade HistoricoConsultaMargemHome</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marlon.silva $
 * $Revision:  $
 * $Date: 2019-06-04 13:00:00 -0300 (Ter, 04 jun 2019) $
 */
public class HistoricoConsultaMargemHome extends AbstractEntityHome {

    public static HistoricoConsultaMargem findByPrimaryKey(String id) throws FindException {
        HistoricoConsultaMargem historicoConsultaMargem = new HistoricoConsultaMargem();
        historicoConsultaMargem.setHcmCodigo(id);

        return find(historicoConsultaMargem, id);
    }

    public static void create(String rseCodigo, String usuCodigo, boolean temMargem, CanalEnum canal) throws CreateException {
        Session session = SessionUtil.getSession();
        try {
            HistoricoConsultaMargem hcm = new HistoricoConsultaMargem();

            hcm.setHcmCodigo(DBHelper.getNextId().toString());
            hcm.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            hcm.setUsuario(session.getReference(Usuario.class, usuCodigo));
            hcm.setHcmData(DateHelper.getSystemDatetime());
            hcm.setHcmTemMargem(temMargem ? "1" : "0");
            hcm.setHcmCanal(canal.getCodigo());

            create(hcm, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM HistoricoConsultaMargem hcm WHERE hcm.registroServidor.rseCodigo = :rseCodigo ");

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
