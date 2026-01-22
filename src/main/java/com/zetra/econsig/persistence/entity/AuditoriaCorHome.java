package com.zetra.econsig.persistence.entity;

import java.sql.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AuditoriaCorHome</p>
 * <p>Description: Classe Home para a entidade AuditoriaCor</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AuditoriaCorHome extends AbstractEntityHome {

    public static AuditoriaCor findByPrimaryKey(Integer acoCodigo) throws FindException {
        AuditoriaCor auditoria = new AuditoriaCor();
        auditoria.setAcoCodigo(acoCodigo);
        return find(auditoria, acoCodigo);
    }

    public static AuditoriaCor create(String corCodigo, String tloCodigo, String usuCodigo, String funCodigo, String tenCodigo,
                                      String acoAuditado, Date acoData, String acoIp, String acoObs, String usuCodigoAuditor, Date acoDataAuditoria) throws CreateException {

        Session session = SessionUtil.getSession();
        AuditoriaCor bean = new AuditoriaCor();

        try {
            bean.setCorrespondente((Correspondente) session.getReference(Correspondente.class, corCodigo));
            bean.setTipoLog((TipoLog) session.getReference(TipoLog.class, tloCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setUsuarioAuditor((Usuario) session.getReference(Usuario.class, usuCodigoAuditor));
            bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            bean.setTipoEntidade((TipoEntidade) session.getReference(TipoEntidade.class, tenCodigo));
            bean.setAcoAuditado(acoAuditado);
            bean.setAcoData(acoData);
            bean.setAcoDataAuditoria(acoDataAuditoria);
            bean.setAcoIp(acoIp);
            bean.setAcoObs(acoObs);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
