package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParamSvcCorrespondenteHome</p>
 * <p>Description: Classe Home para a entidade ParamSvcCorrespondente</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSvcCorrespondenteHome extends AbstractEntityHome {

    public static ParamSvcCorrespondente findByPrimaryKey(String psoCodigo) throws FindException {
        ParamSvcCorrespondente paramSvcCorrespondente = new ParamSvcCorrespondente();
        paramSvcCorrespondente.setPsoCodigo(psoCodigo);
        return find(paramSvcCorrespondente, psoCodigo);
    }

    public static ParamSvcCorrespondente create(String tpsCodigo, String corCodigo, String svcCodigo, Date psoDataIniVig, Date psoDataFimVig, Short psoAtivo,
            String psoVlr, String psoVlrRef) throws CreateException {

        Session session = SessionUtil.getSession();
        ParamSvcCorrespondente bean = new ParamSvcCorrespondente();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPsoCodigo(objectId);
            bean.setTipoParamSvc(session.getReference(TipoParamSvc.class, tpsCodigo));
            bean.setCorrespondente(session.getReference(Correspondente.class, corCodigo));
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setPsoDataIniVig(psoDataIniVig);
            bean.setPsoDataFimVig(psoDataFimVig);
            bean.setPsoAtivo(psoAtivo);
            bean.setPsoVlr(psoVlr);
            bean.setPsoVlrRef(psoVlrRef);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
