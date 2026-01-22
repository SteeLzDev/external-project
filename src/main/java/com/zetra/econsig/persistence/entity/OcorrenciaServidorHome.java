package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaServidorHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaServidorHome extends AbstractEntityHome {

    public static OcorrenciaServidor findByPrimaryKey(String ocsCodigo) throws FindException {
        OcorrenciaServidor ocorrenciaServidor = new OcorrenciaServidor();
        ocorrenciaServidor.setOcsCodigo(ocsCodigo);
        return find(ocorrenciaServidor, ocsCodigo);
    }

    public static OcorrenciaServidor create(String serCodigo, String tocCodigo, String usuCodigo, String ocsObs, String ocsIpAcesso, String tmoCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaServidor bean = new OcorrenciaServidor();
        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOcsCodigo(objectId);
            bean.setServidor(session.getReference(Servidor.class, serCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            if (!TextHelper.isNull(tmoCodigo)) {
                bean.setTipoMotivoOperacao(session.getReference(TipoMotivoOperacao.class, tmoCodigo));
            }
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOcsData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOcsObs(ocsObs);
            bean.setOcsIpAcesso(ocsIpAcesso);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM OcorrenciaServidor ocs WHERE ocs.servidor.serCodigo = :serCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("serCodigo", serCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
