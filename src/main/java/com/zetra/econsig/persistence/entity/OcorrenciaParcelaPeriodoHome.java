package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaParcelaPeriodoHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaParcelaPeriodo</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaParcelaPeriodoHome extends AbstractEntityHome {

    public static OcorrenciaParcelaPeriodo findByPrimaryKey(String ocpCodigo) throws FindException {
        OcorrenciaParcelaPeriodo ocorrenciaParcelaPeriodo = new OcorrenciaParcelaPeriodo();
        ocorrenciaParcelaPeriodo.setOcpCodigo(ocpCodigo);
        return find(ocorrenciaParcelaPeriodo, ocpCodigo);
    }

    public static List<OcorrenciaParcelaPeriodo> findByPrdCodigo(Integer prdCodigo) throws FindException {
        String query = "FROM OcorrenciaParcelaPeriodo opp WHERE opp.parcelaDescontoPeriodo.prdCodigo = :prdCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prdCodigo", prdCodigo);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaParcelaPeriodo create(Integer prdCodigo, String tocCodigo, String ocpObs, String usuCodigo) throws CreateException {
        return create(prdCodigo, tocCodigo, ocpObs, usuCodigo, DateHelper.getSystemDatetime());
    }

    public static OcorrenciaParcelaPeriodo create(Integer prdCodigo, String tocCodigo, String ocpObs, String usuCodigo, Date ocpData) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaParcelaPeriodo bean = new OcorrenciaParcelaPeriodo();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOcpCodigo(objectId);
            bean.setParcelaDescontoPeriodo(session.getReference(ParcelaDescontoPeriodo.class, prdCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setOcpData(ocpData);
            bean.setOcpObs(ocpObs);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
