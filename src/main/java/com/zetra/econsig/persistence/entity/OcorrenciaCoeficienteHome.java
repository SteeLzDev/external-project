package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaCoeficienteHome</p>
 * <p>Description: Classe para encapsular acesso a entidade OcorrenciaCoeficiente.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaCoeficienteHome extends AbstractEntityHome {

    public static OcorrenciaCoeficiente create(String svcCodigo, String csaCodigo, String usuCodigo, String tocCodigo,
            Date ocfData, Date ocfDataInicioVig, Date ocfDataFimVig, String ocfObs, String ocfIpAcesso) throws CreateException {

        Session session = SessionUtil.getSession();
        String ocfCodigo = null;

        try {
            ocfCodigo = DBHelper.getNextId();
            OcorrenciaCoeficiente bean = new OcorrenciaCoeficiente();
            bean.setOcfCodigo(ocfCodigo);
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            if (!TextHelper.isNull(ocfData)) {
                bean.setOcfData(ocfData);
            } else {
                bean.setOcfData(DateHelper.getSystemDatetime());
            }
            bean.setOcfDataIniVig(ocfDataInicioVig);
            bean.setOcfDataFimVig(ocfDataFimVig);
            bean.setOcfObs(ocfObs);
            bean.setOcfIpAcesso(ocfIpAcesso);
            create(bean, session);

            return bean;
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static OcorrenciaCoeficiente findByPrimaryKey(String ocfCodigo) throws FindException {
        OcorrenciaCoeficiente ocorrencia = new OcorrenciaCoeficiente();
        ocorrencia.setOcfCodigo(ocfCodigo);

        return find(ocorrencia, ocfCodigo);
    }
}
