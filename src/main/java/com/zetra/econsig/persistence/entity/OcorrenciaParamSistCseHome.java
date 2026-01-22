package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;


/**
 * <p>Title: OcorrenciaParamSistCseHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaParamSistCse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public class OcorrenciaParamSistCseHome extends AbstractEntityHome {

    public static OcorrenciaParamSistCse create(String tocCodigo, String usuCodigo, String tpcCodigo, String cseCodigo, Date opsData, String opsObs, String opsIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaParamSistCse bean = new OcorrenciaParamSistCse();

        try {
            String objectId = DBHelper.getNextId();
            bean.setOpsCodigo(objectId);
            bean.setTipoOcorrencia((TipoOcorrencia) session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setTipoParamSistConsignante((TipoParamSistConsignante) session.getReference(TipoParamSistConsignante.class, tpcCodigo));
            bean.setConsignante((Consignante) session.getReference(Consignante.class, cseCodigo));

            if (opsData == null) {
                bean.setOpsData(Calendar.getInstance().getTime());
            } else {
                bean.setOpsData(opsData);
            }

            bean.setOpsObs(opsObs);
            bean.setOpsIpAcesso(opsIpAcesso);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

}
