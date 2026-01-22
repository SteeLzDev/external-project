package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaCredenciamentoCsaHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaCredenciamentoCsa</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaCredenciamentoCsaHome extends AbstractEntityHome {

    public static OcorrenciaCredenciamentoCsa findByPrimaryKey(String ocdCodigo) throws FindException {
        OcorrenciaCredenciamentoCsa ocorrenciaCorrespondenteCsa = new OcorrenciaCredenciamentoCsa();
        ocorrenciaCorrespondenteCsa.setOcdCodigo(ocdCodigo);
        return find(ocorrenciaCorrespondenteCsa, ocdCodigo);
    }

    public static List<OcorrenciaCredenciamentoCsa> findByCreCodigo(String creCodigo) throws FindException {
        String query = "FROM OcorrenciaCredenciamentoCsa ocd "
        		+ "JOIN FETCH ocd.tipoOcorrencia "
        		+ "JOIN FETCH ocd.usuario "
        		+ "WHERE ocd.creCodigo = :creCodigo "
        		+ "ORDER BY ocd.ocdData DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("creCodigo", creCodigo);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaCredenciamentoCsa create(String creCodigo, String usuCodigo, String tocCodigo, String ocdObs, String tmoCodigo, String ipAcesso) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaCredenciamentoCsa bean = new OcorrenciaCredenciamentoCsa();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOcdCodigo(objectId);
            bean.setCredenciamentoCsa(session.getReference(CredenciamentoCsa.class, creCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            if (!TextHelper.isNull(tmoCodigo)) {
            	bean.setTipoMotivoOperacao(session.getReference(TipoMotivoOperacao.class, tmoCodigo));
            }
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOcdData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOcdObs(ocdObs);
            bean.setOcdIpAcesso(ipAcesso);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}