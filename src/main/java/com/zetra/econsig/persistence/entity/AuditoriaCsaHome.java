package com.zetra.econsig.persistence.entity;

import java.sql.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AuditoriaCsaHome</p>
 * <p>Description: Classe Home para a entidade AuditoriaCsa</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AuditoriaCsaHome extends AbstractEntityHome {

    public static AuditoriaCsa findByPrimaryKey(Integer acsCodigo) throws FindException {
        AuditoriaCsa auditoria = new AuditoriaCsa();
        auditoria.setAcsCodigo(acsCodigo);
        return find(auditoria, acsCodigo);
    }

    public static AuditoriaCsa create(String csaCodigo, String tloCodigo, String usuCodigo, String funCodigo, String tenCodigo,
                                      String acsAuditado, Date acsData, String acsIp, String acsObs, String usuCodigoAuditor, Date acsDataAuditoria) throws CreateException {

        Session session = SessionUtil.getSession();
        AuditoriaCsa bean = new AuditoriaCsa();

        try {
            bean.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
            bean.setTipoLog((TipoLog) session.getReference(TipoLog.class, tloCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setUsuarioAuditor((Usuario) session.getReference(Usuario.class, usuCodigoAuditor));
            bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            bean.setTipoEntidade((TipoEntidade) session.getReference(TipoEntidade.class, tenCodigo));
            bean.setAcsAuditado(acsAuditado);
            bean.setAcsData(acsData);
            bean.setAcsDataAuditoria(acsDataAuditoria);
            bean.setAcsIp(acsIp);
            bean.setAcsObs(acsObs);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
