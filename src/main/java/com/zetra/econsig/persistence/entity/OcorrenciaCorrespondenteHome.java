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
 * <p>Title: OcorrenciaCorrespondenteHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaCorrespondente</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaCorrespondenteHome extends AbstractEntityHome {

    public static OcorrenciaCorrespondente findByPrimaryKey(String ocrCodigo) throws FindException {
        OcorrenciaCorrespondente ocorrenciaCorrespondente = new OcorrenciaCorrespondente();
        ocorrenciaCorrespondente.setOcrCodigo(ocrCodigo);
        return find(ocorrenciaCorrespondente, ocrCodigo);
    }

    public static List<OcorrenciaCorrespondente> findByCorTocCodigo(String corCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaCorrespondente ocr WHERE ocr.corCodigo = :corCodigo AND ocr.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("corCodigo", corCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaCorrespondente create(String corCodigo, String usuCodigo, String tocCodigo, String ocrObs, String tmoCodigo, String ipAcesso) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaCorrespondente bean = new OcorrenciaCorrespondente();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOcrCodigo(objectId);
            bean.setCorrespondente(session.getReference(Correspondente.class, corCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setOcrObs(ocrObs);
            bean.setOcrData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOcrIpAcesso(ipAcesso);
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
