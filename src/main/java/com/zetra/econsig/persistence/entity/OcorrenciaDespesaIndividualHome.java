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
 * <p>Title: OcorrenciaDespesaIndividualHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaDespIndividual</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaDespesaIndividualHome extends AbstractEntityHome {

    public static OcorrenciaDespIndividual findByPrimaryKey(String odiCodigo) throws FindException {
        OcorrenciaDespIndividual bean = new OcorrenciaDespIndividual();
        bean.setOdiCodigo(odiCodigo);
        return find(bean, odiCodigo);
    }

    public static Collection<OcorrenciaDespIndividual> findByPrmTocCodigo(String adeCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaDespIndividual odi WHERE odi.autorizacaoDesconto.adeCodigo = :adeCodigo AND odi.tipoOcorrencia.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<OcorrenciaDespIndividual> findByPrmTocCodigo(String adeCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaDespIndividual odi WHERE odi.autorizacaoDesconto.adeCodigo = :adeCodigo AND odi.tipoOcorrencia.tocCodigo IN (:tocCodigos)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaDespIndividual create(String adeCodigo, String tocCodigo, String usuCodigo, String odiIpAcesso, String odiObs) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaDespIndividual bean = new OcorrenciaDespIndividual();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOdiCodigo(objectId);
            bean.setDespesaIndividual(session.getReference(DespesaIndividual.class, adeCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOdiData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOdiIpAcesso(odiIpAcesso);
            bean.setOdiObs(odiObs);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
