package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaPermissionarioHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaPermissionario</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaPermissionarioHome extends AbstractEntityHome {

    public static OcorrenciaPermissionario findByPrimaryKey(String opeCodigo) throws FindException {
        OcorrenciaPermissionario bean = new OcorrenciaPermissionario();
        bean.setOpeCodigo(opeCodigo);
        return find(bean, opeCodigo);
    }

    public static Collection<OcorrenciaPermissionario> findByPrmTocCodigo(String prmCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaPermissionario ope WHERE ope.permissionario.prmCodigo = :prmCodigo AND ope.tipoOcorrencia.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prmCodigo", prmCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<OcorrenciaPermissionario> findByPrmTocCodigo(String prmCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaPermissionario ope WHERE ope.permissionario.prmCodigo = :prmCodigo AND ope.tipoOcorrencia.tocCodigo IN (:tocCodigos)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prmCodigo", prmCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaPermissionario create(String prmCodigo, String tocCodigo, String usuCodigo, String opeIpAcesso, String opeObs) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaPermissionario bean = new OcorrenciaPermissionario();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOpeCodigo(objectId);
            bean.setPermissionario(session.getReference(Permissionario.class, prmCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOpeData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOpeIpAcesso(opeIpAcesso);
            bean.setOpeObs(opeObs);
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

            hql.append("DELETE FROM OcorrenciaPermissionario ope WHERE ope.permissionario.prmCodigo in (SELECT prm.prmCodigo FROM Permissionario prm WHERE prm.registroServidor.rseCodigo = :rseCodigo) ");

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
