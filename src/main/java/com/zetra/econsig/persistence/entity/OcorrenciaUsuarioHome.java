package com.zetra.econsig.persistence.entity;

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
 * <p>Title: OcorrenciaUsuarioHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaUsuario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaUsuarioHome extends AbstractEntityHome {

    public static OcorrenciaUsuario findByPrimaryKey(String ousCodigo) throws FindException {
        OcorrenciaUsuario ocorrenciaUsuario = new OcorrenciaUsuario();
        ocorrenciaUsuario.setOusCodigo(ousCodigo);
        return find(ocorrenciaUsuario, ousCodigo);
    }

    public static List<OcorrenciaUsuario> findByUsuTocCodigo(String usuCodigo, String[] tocCodigos) throws FindException {
        String query = "FROM OcorrenciaUsuario ous WHERE ous.usuarioByUsuCodigo.usuCodigo = :usuCodigo AND ous.tipoOcorrencia.tocCodigo IN (:tocCodigos)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaUsuario create(String tocCodigo, String usuCodigo, java.util.Date ousData, String ousObs, String ousUsuCodigo, String ousIpAcesso) throws CreateException {
        return create(tocCodigo, usuCodigo, ousData, ousObs, ousUsuCodigo, ousIpAcesso, null);
    }

    public static OcorrenciaUsuario create(String tocCodigo, String usuCodigo, java.util.Date ousData, String ousObs, String ousUsuCodigo, String ousIpAcesso, String tmoCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaUsuario bean = new OcorrenciaUsuario();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();

            bean.setOusCodigo(objectId);
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuarioByUsuCodigo(session.getReference(Usuario.class, usuCodigo));
            bean.setOusData(ousData);
            bean.setOusObs(ousObs);
            bean.setUsuarioByOusUsuCodigo(session.getReference(Usuario.class, ousUsuCodigo));
            bean.setOusIpAcesso(ousIpAcesso);
            if (!TextHelper.isNull(tmoCodigo)) {
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

}
