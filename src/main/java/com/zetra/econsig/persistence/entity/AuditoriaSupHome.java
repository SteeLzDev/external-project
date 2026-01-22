package com.zetra.econsig.persistence.entity;

import java.sql.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AuditoriaSupHome</p>
 * <p>Description: Classe Home para a entidade AuditoriaSup</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AuditoriaSupHome extends AbstractEntityHome {

    public static AuditoriaSup findByPrimaryKey(Integer asuCodigo) throws FindException {
        AuditoriaSup auditoria = new AuditoriaSup();
        auditoria.setAsuCodigo(asuCodigo);
        return find(auditoria, asuCodigo);
    }

    public static AuditoriaSup create(String cseCodigo, String tloCodigo, String usuCodigo, String funCodigo, String tenCodigo,
                                      String asuAuditado, Date asuData, String asuIp, String asuObs, String usuCodigoAuditor, Date asuDataAuditoria) throws CreateException {

        Session session = SessionUtil.getSession();
        AuditoriaSup bean = new AuditoriaSup();

        try {
            bean.setConsignante((Consignante) session.getReference(Consignante.class, cseCodigo));
            bean.setTipoLog((TipoLog) session.getReference(TipoLog.class, tloCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setUsuarioAuditor((Usuario) session.getReference(Usuario.class, usuCodigoAuditor));
            bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            bean.setTipoEntidade((TipoEntidade) session.getReference(TipoEntidade.class, tenCodigo));
            bean.setAsuAuditado(asuAuditado);
            bean.setAsuData(asuData);
            bean.setAsuDataAuditoria(asuDataAuditoria);
            bean.setAsuIp(asuIp);
            bean.setAsuObs(asuObs);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
