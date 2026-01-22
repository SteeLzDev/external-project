package com.zetra.econsig.persistence.entity;

import java.sql.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AuditoriaOrgHome</p>
 * <p>Description: Classe Home para a entidade AuditoriaOrg</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AuditoriaOrgHome extends AbstractEntityHome {

    public static AuditoriaOrg findByPrimaryKey(Integer aorCodigo) throws FindException {
        AuditoriaOrg auditoria = new AuditoriaOrg();
        auditoria.setAorCodigo(aorCodigo);
        return find(auditoria, aorCodigo);
    }

    public static AuditoriaOrg create(String orgCodigo, String tloCodigo, String usuCodigo, String funCodigo, String tenCodigo,
                                      String aorAuditado, Date aorData, String aorIp, String aorObs, String usuCodigoAuditor, Date aorDataAuditoria) throws CreateException {

        Session session = SessionUtil.getSession();
        AuditoriaOrg bean = new AuditoriaOrg();

        try {
            bean.setOrgao((Orgao) session.getReference(Orgao.class, orgCodigo));
            bean.setTipoLog((TipoLog) session.getReference(TipoLog.class, tloCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setUsuarioAuditor((Usuario) session.getReference(Usuario.class, usuCodigoAuditor));
            bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            bean.setTipoEntidade((TipoEntidade) session.getReference(TipoEntidade.class, tenCodigo));
            bean.setAorAuditado(aorAuditado);
            bean.setAorData(aorData);
            bean.setAorDataAuditoria(aorDataAuditoria);
            bean.setAorIp(aorIp);
            bean.setAorObs(aorObs);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
