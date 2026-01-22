package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParamSvcConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade ParamSvcConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSvcConsignatariaHome extends AbstractEntityHome {

    public static ParamSvcConsignataria findByPrimaryKey(String pscCodigo) throws FindException {
        ParamSvcConsignataria paramSvcConsignataria = new ParamSvcConsignataria();
        paramSvcConsignataria.setPscCodigo(pscCodigo);
        return find(paramSvcConsignataria, pscCodigo);
    }

    public static ParamSvcConsignataria findAtivoBySvcCsa(String svcCodigo, String csaCodigo) throws FindException {
        String query = "FROM ParamSvcConsignataria psc WHERE psc.servico.svcCodigo = :svcCodigo AND psc.consignataria.csaCodigo = :csaCodigo AND p.pscAtivo = 1";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);

        List<ParamSvcConsignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParamSvcConsignataria findParametroBySvcCsa(String svcCodigo, String csaCodigo, String tpsCodigo) throws FindException {
        String query = "FROM ParamSvcConsignataria psc WHERE psc.servico.svcCodigo = :svcCodigo AND psc.consignataria.csaCodigo = :csaCodigo AND psc.tipoParamSvc.tpsCodigo = :tpsCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("tpsCodigo", tpsCodigo);

        List<ParamSvcConsignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<ParamSvcConsignataria> findAllParametroBySvcCsa(String svcCodigo, String csaCodigo) throws FindException {
        String query = "FROM ParamSvcConsignataria psc WHERE psc.servico.svcCodigo = :svcCodigo AND psc.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static ParamSvcConsignataria create(String tpsCodigo, String csaCodigo, String svcCodigo, Date pscDataIniVig, Date pscDataFimVig, Short pscAtivo,
            String pscVlr, String pscVlrRef) throws CreateException {

        Session session = SessionUtil.getSession();
        ParamSvcConsignataria bean = new ParamSvcConsignataria();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPscCodigo(objectId);
            bean.setTipoParamSvc((TipoParamSvc) session.getReference(TipoParamSvc.class, tpsCodigo));
            bean.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
            bean.setServico((Servico) session.getReference(Servico.class, svcCodigo));
            bean.setPscDataIniVig(pscDataIniVig);
            bean.setPscDataFimVig(pscDataFimVig);
            bean.setPscAtivo(pscAtivo);
            bean.setPscVlr(pscVlr);
            bean.setPscVlrRef(pscVlrRef);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
