package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RelacionamentoServicoHome</p>
 * <p>Description: Classe Home para a entidade RelacionamentoServico</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelacionamentoServicoHome extends AbstractEntityHome {

    public static RelacionamentoServico findByPrimaryKey(String relSvcCodigo) throws FindException {
        RelacionamentoServico relacionamentoServico = new RelacionamentoServico();
        relacionamentoServico.setRelSvcCodigo(relSvcCodigo);
        return find(relacionamentoServico, relSvcCodigo);
    }

    public static RelacionamentoServico create(String svcCodigoOrigem, String svcCodigoDestino, String tntCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        RelacionamentoServico bean = new RelacionamentoServico();

        try {
            String objectId = DBHelper.getNextId();
            bean.setRelSvcCodigo(objectId);
            bean.setServicoBySvcCodigoOrigem(session.getReference(Servico.class, svcCodigoOrigem));
            bean.setServicoBySvcCodigoDestino(session.getReference(Servico.class, svcCodigoDestino));
            bean.setTipoNatureza(session.getReference(TipoNatureza.class, tntCodigo));
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static List<RelacionamentoServico> findBySvcCodigoOrigem(String svcCodigoOrigem, List<String> tntCodigos) throws FindException {
        String query = "SELECT DISTINCT rel from RelacionamentoServico rel where rel.servicoBySvcCodigoOrigem.svcCodigo = :svcCodigoOrigem and rel.tipoNatureza.tntCodigo in (:tntCodigos)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigoOrigem", svcCodigoOrigem);
        parameters.put("tntCodigos", tntCodigos);

        return findByQuery(query, parameters);
    }

    public static List<RelacionamentoServico> findBySvcCodigoDestino(String svcCodigoDestino, List<String> tntCodigos) throws FindException {
        String query = "SELECT DISTINCT rel from RelacionamentoServico rel where rel.servicoBySvcCodigoDestino.svcCodigo = :svcCodigoDestino and rel.tipoNatureza.tntCodigo in (:tntCodigos)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigoDestino", svcCodigoDestino);
        parameters.put("tntCodigos", tntCodigos);

        return findByQuery(query, parameters);
    }

    public static List<RelacionamentoServico> findBySvcCodigoDestinoECodigoOrigem(String svcCodigoDestino, String svcCodigoOrigem) throws FindException {
        String query = "SELECT rel from RelacionamentoServico rel where rel.servicoBySvcCodigoDestino.svcCodigo = :svcCodigoDestino and rel.servicoBySvcCodigoOrigem.svcCodigo = :svcCodigoOrigem and rel.tipoNatureza.tntCodigo in (:tntCodigos)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigoDestino", svcCodigoDestino);
        parameters.put("svcCodigoOrigem", svcCodigoOrigem);
        parameters.put("tntCodigos", CodedValues.TNT_COMPRA);

        return findByQuery(query, parameters);
    }
}
