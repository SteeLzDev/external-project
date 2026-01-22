package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: BloqueioPostoCsaSvcHome</p>
 * <p>Description: Classe Home para a entidade BloqueioPostoCsaSvc</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioPostoCsaSvcHome extends AbstractEntityHome {

    public static BloqueioPostoCsaSvc findByPrimaryKey(String csaCodigo, String svcCodigo, String posCodigo) throws FindException {
        BloqueioPostoCsaSvcId id = new BloqueioPostoCsaSvcId(csaCodigo, svcCodigo, posCodigo);
        BloqueioPostoCsaSvc bloqueioPostoCsaSvc = new BloqueioPostoCsaSvc();
        return find(bloqueioPostoCsaSvc, id);
    }

    public static BloqueioPostoCsaSvc create(String csaCodigo, String svcCodigo, String posCodigo, String bpcBloqSolicitacao, String bpcBloqReserva) throws CreateException {

        Session session = SessionUtil.getSession();
        BloqueioPostoCsaSvc bean = new BloqueioPostoCsaSvc();

        try {
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setPostoRegistroServidor(session.getReference(PostoRegistroServidor.class, posCodigo));
            bean.setBpcBloqSolicitacao(bpcBloqSolicitacao);
            bean.setBpcBloqReserva(bpcBloqReserva);
            create(bean, session);

        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
