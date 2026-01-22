package com.zetra.econsig.persistence.entity;

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
 * <p>Title: ParamSvcConsignanteHome</p>
 * <p>Description: Classe Home para a entidade ParamSvcConsignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSvcConsignanteHome extends AbstractEntityHome {

    public static ParamSvcConsignante findByPrimaryKey(String pseCodigo) throws FindException {
        ParamSvcConsignante paramSvcConsignante = new ParamSvcConsignante();
        paramSvcConsignante.setPseCodigo(pseCodigo);
        return find(paramSvcConsignante, pseCodigo);
    }

    public static ParamSvcConsignante findByTipoCseServico(String tpsCodigo, String cseCodigo, String svcCodigo) throws FindException {
        String query = "FROM ParamSvcConsignante pse WHERE pse.tipoParamSvc.tpsCodigo = :tpsCodigo AND pse.consignante.cseCodigo = :cseCodigo AND pse.servico.svcCodigo = :svcCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tpsCodigo", tpsCodigo);
        parameters.put("cseCodigo", cseCodigo);
        parameters.put("svcCodigo", svcCodigo);

        List<ParamSvcConsignante> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParamSvcConsignante create(String svcCodigo, String tpsCodigo, String cseCodigo, String pseVlr, String pseVlrRef) throws CreateException {

        Session session = SessionUtil.getSession();
        ParamSvcConsignante bean = new ParamSvcConsignante();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPseCodigo(objectId);
            bean.setServico((Servico) session.getReference(Servico.class, svcCodigo));
            bean.setTipoParamSvc((TipoParamSvc) session.getReference(TipoParamSvc.class, tpsCodigo));
            bean.setConsignante((Consignante) session.getReference(Consignante.class, cseCodigo));
            bean.setPseVlr(pseVlr);
            bean.setPseVlrRef(pseVlrRef);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
