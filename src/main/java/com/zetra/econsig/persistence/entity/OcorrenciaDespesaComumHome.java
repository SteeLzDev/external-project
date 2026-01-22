package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaDespesaComumHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaDespesaComum</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaDespesaComumHome extends AbstractEntityHome {

    public static OcorrenciaDespesaComum findByPrimaryKey(String odcCodigo) throws FindException {
        OcorrenciaDespesaComum bean = new OcorrenciaDespesaComum();
        bean.setOdcCodigo(odcCodigo);
        return find(bean, odcCodigo);
    }

    public static Collection<OcorrenciaDespesaComum> findByPrmTocCodigo(String decCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaDespesaComum odc WHERE odc.despesaComum.decCodigo = :decCodigo AND odc.tipoOcorrencia.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("decCodigo", decCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<OcorrenciaDespesaComum> findByPrmTocCodigo(String decCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaDespesaComum odc WHERE odc.despesaComum.decCodigo = :decCodigo AND odc.tipoOcorrencia.tocCodigo IN (:tocCodigos)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("decCodigo", decCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaDespesaComum create(String decCodigo, String tocCodigo, String usuCodigo, String odcIpAcesso, String odcObs) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaDespesaComum bean = new OcorrenciaDespesaComum();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOdcCodigo(objectId);
            bean.setDespesaComum((DespesaComum) session.getReference(DespesaComum.class, decCodigo));
            bean.setTipoOcorrencia((TipoOcorrencia) session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setOdcData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOdcIpAcesso(odcIpAcesso);
            bean.setOdcObs(odcObs);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
