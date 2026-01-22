package com.zetra.econsig.persistence.entity;

import java.sql.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AuditoriaCseHome</p>
 * <p>Description: Classe Home para a entidade AuditoriaCse</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AuditoriaCseHome extends AbstractEntityHome {

    public static AuditoriaCse findByPrimaryKey(Integer aceCodigo) throws FindException {
        AuditoriaCse auditoria = new AuditoriaCse();
        auditoria.setAceCodigo(aceCodigo);
        return find(auditoria, aceCodigo);
    }

    public static AuditoriaCse create(String cseCodigo, String tloCodigo, String usuCodigo, String funCodigo, String tenCodigo,
                                      String aceAuditado, Date aceData, String aceIp, String aceObs, String usuCodigoAuditor, Date aceDataAuditoria) throws CreateException {

        Session session = SessionUtil.getSession();
        AuditoriaCse bean = new AuditoriaCse();

        try {
            bean.setConsignante((Consignante) session.getReference(Consignante.class, cseCodigo));
            bean.setTipoLog((TipoLog) session.getReference(TipoLog.class, tloCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setUsuarioAuditor((Usuario) session.getReference(Usuario.class, usuCodigoAuditor));
            bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            bean.setTipoEntidade((TipoEntidade) session.getReference(TipoEntidade.class, tenCodigo));
            bean.setAceAuditado(aceAuditado);
            bean.setAceData(aceData);
            bean.setAceDataAuditoria(aceDataAuditoria);
            bean.setAceIp(aceIp);
            bean.setAceObs(aceObs);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
