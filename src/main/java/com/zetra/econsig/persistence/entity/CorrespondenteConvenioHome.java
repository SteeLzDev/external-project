package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: CorrespondenteConvenioHome</p>
 * <p>Description: Classe Home para a entidade CorrespondenteConvenio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CorrespondenteConvenioHome extends AbstractEntityHome {

    public static List<CorrespondenteConvenio> findByCnvCodigo(String cnvCodigo) throws FindException {
        String query = "FROM CorrespondenteConvenio crc WHERE crc.convenio.cnvCodigo = :cnvCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cnvCodigo", cnvCodigo);

        return findByQuery(query, parameters);
    }

    public static List<CorrespondenteConvenio> findByCorCodigo(String corCodigo) throws FindException {
        String query = "FROM CorrespondenteConvenio crc WHERE crc.correspondente.corCodigo = :corCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("corCodigo", corCodigo);

        return findByQuery(query, parameters);
    }

    public static List<CorrespondenteConvenio> findByCorSvcCodigo(String corCodigo, String svcCodigo) throws FindException {
        StringBuilder query = new StringBuilder("SELECT crc FROM CorrespondenteConvenio crc");
        query.append(" INNER JOIN crc.convenio cnv");
        query.append(" WHERE crc.correspondente.corCodigo = :corCodigo");
        query.append(" AND cnv.servico.svcCodigo = :svcCodigo");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("corCodigo", corCodigo);
        parameters.put("svcCodigo", svcCodigo);

        return findByQuery(query.toString(), parameters);
    }

    public static CorrespondenteConvenio findByPrimaryKey(CorrespondenteConvenioId pk) throws FindException {
        CorrespondenteConvenio correspondenteConvenio = new CorrespondenteConvenio();
        correspondenteConvenio.setId(pk);
        return find(correspondenteConvenio, pk);
    }

    public static CorrespondenteConvenio create(String corCodigo, String cnvCodigo, String scvCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        CorrespondenteConvenio bean = new CorrespondenteConvenio();

        try {
            CorrespondenteConvenioId id = new CorrespondenteConvenioId(corCodigo, cnvCodigo);
            bean.setId(id);
            bean.setStatusConvenio((StatusConvenio) session.getReference(StatusConvenio.class, scvCodigo));
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
