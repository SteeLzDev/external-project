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
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OcorrenciaConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaConsignatariaHome extends AbstractEntityHome {

    public static OcorrenciaConsignataria findByPrimaryKey(String occCodigo) throws FindException {
        OcorrenciaConsignataria ocorrenciaConsignataria = new OcorrenciaConsignataria();
        ocorrenciaConsignataria.setOccCodigo(occCodigo);
        return find(ocorrenciaConsignataria, occCodigo);
    }

    public static List<OcorrenciaConsignataria> findByCsaTocCodigo(String csaCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaConsignataria occ WHERE occ.consignataria.csaCodigo = :csaCodigo AND occ.tipoOcorrencia.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaConsignataria create(String csaCodigo, String usuCodigo, String tocCodigo, String occObs, String tpeCodigo, String tmoCodigo, String ipAcesso) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaConsignataria bean = new OcorrenciaConsignataria();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOccCodigo(objectId);
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setOccObs(occObs);
            bean.setOccData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOccIpAcesso(ipAcesso);
            if (!TextHelper.isNull(tpeCodigo)) {
                bean.setTipoPenalidade(session.getReference(TipoPenalidade.class, tpeCodigo));
            }
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

    public static OcorrenciaConsignataria findByCsaTocCodigoMaxData(String csaCodigo, String tocCodigo, boolean bloqueioManual) throws FindException {

        StringBuilder query = new StringBuilder();
        query.append("FROM OcorrenciaConsignataria occ WHERE occ.consignataria.csaCodigo = :csaCodigo AND occ.tipoOcorrencia.tocCodigo = :tocCodigo ");

        if(bloqueioManual) {
            query.append(" AND occ.usuario.usuCodigo != :usuCodigo");
        }

        query.append(" ORDER BY occ.occData DESC");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("tocCodigo", tocCodigo);
        if(bloqueioManual) {
            parameters.put("usuCodigo", CodedValues.USU_CODIGO_SISTEMA);
        }

        List<OcorrenciaConsignataria> ocorrenciaConsignataria = findByQuery(query.toString(), parameters, 1, 0);

        return (ocorrenciaConsignataria != null && !ocorrenciaConsignataria.isEmpty()) ? ocorrenciaConsignataria.get(0) : null;
    }
}
