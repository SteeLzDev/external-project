package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaParcelaHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaParcela</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaParcelaHome extends AbstractEntityHome {

    public static OcorrenciaParcela findByPrimaryKey(String ocpCodigo) throws FindException {
        OcorrenciaParcela ocorrenciaParcela = new OcorrenciaParcela();
        ocorrenciaParcela.setOcpCodigo(ocpCodigo);
        return find(ocorrenciaParcela, ocpCodigo);
    }

    public static Collection<OcorrenciaParcela> findByPrdCodigo(Integer prdCodigo) throws FindException {
        String query = "FROM OcorrenciaParcela ocp WHERE ocp.parcelaDesconto.prdCodigo = :prdCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prdCodigo", prdCodigo);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaParcela create(Integer prdCodigo, String tocCodigo, String ocpObs, String usuCodigo) throws CreateException {
        return create(prdCodigo, tocCodigo, ocpObs, usuCodigo, DateHelper.getSystemDatetime());
    }

    public static OcorrenciaParcela create(Integer prdCodigo, String tocCodigo, String ocpObs, String usuCodigo, Date ocpData) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaParcela bean = new OcorrenciaParcela();

        try {
            String objectId = DBHelper.getNextId();
            bean.setOcpCodigo(objectId);
            bean.setParcelaDesconto(session.getReference(ParcelaDesconto.class, prdCodigo));
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

    public static List<OcorrenciaParcela> findMaxPrdNumeroTocCodigo(String adeCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaParcela ocp INNER JOIN ocp.parcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo AND ocp.tipoOcorrencia.tocCodigo = :tocCodigo ORDER BY prd.prdNumero DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters, 1, 0);
    }

    public static void deleteByPrdCodigoTocCodigo(Integer prdCodigo, List<String> tocCodigos) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "DELETE FROM OcorrenciaParcela ocp WHERE ocp.parcelaDesconto.prdCodigo = :prdCodigo AND ocp.tipoOcorrencia.tocCodigo in (:tocCodigo)";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("prdCodigo", prdCodigo);
            queryUpdate.setParameter("tocCodigo", tocCodigos);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
