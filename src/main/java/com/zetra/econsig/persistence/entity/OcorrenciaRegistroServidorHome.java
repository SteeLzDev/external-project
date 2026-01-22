package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
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
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaRegistroSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaRegistroServidorHome extends AbstractEntityHome {

    public static OcorrenciaRegistroSer findByPrimaryKey(String orsCodigo) throws FindException {
        OcorrenciaRegistroSer ocorrenciaRegistroSer = new OcorrenciaRegistroSer();
        ocorrenciaRegistroSer.setOrsCodigo(orsCodigo);
        return find(ocorrenciaRegistroSer, orsCodigo);
    }

    public static List<OcorrenciaRegistroSer> findByRseTocCodigo(String rseCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaRegistroSer ors WHERE ors.registroServidor.rseCodigo = :rseCodigo AND ors.tipoOcorrencia.tocCodigo = :tocCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("tocCodigo", tocCodigo);
        return findByQuery(query, parameters);
    }

    public static OcorrenciaRegistroSer findLastByRseTocCodigos(String rseCodigo, List<String> tocCodigo) throws FindException {
        String query = "FROM OcorrenciaRegistroSer ors WHERE ors.registroServidor.rseCodigo = :rseCodigo AND ors.tipoOcorrencia.tocCodigo IN (:tocCodigo) ORDER BY ors.orsData DESC";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("tocCodigo", tocCodigo);

        List<OcorrenciaRegistroSer> result = findByQuery(query, parameters, 1, 0);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }
    
    public static OcorrenciaRegistroSer create(String rseCodigo, String tocCodigo, String usuCodigo, String orsObs, String ipAcesso, String tmoCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaRegistroSer bean = new OcorrenciaRegistroSer();
        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOrsCodigo(objectId);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOrsData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOrsObs(orsObs);
            bean.setOrsIpAcesso(ipAcesso);
            if(!TextHelper.isNull(tmoCodigo)) {
                bean.setTipoMotivoOperacao(session.getReference(TipoMotivoOperacao.class, tmoCodigo));
            }
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

            hql.append("DELETE FROM OcorrenciaRegistroSer ors WHERE ors.registroServidor.rseCodigo = :rseCodigo ");

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
