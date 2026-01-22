package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AcessoUsuarioHome</p>
 * <p>Description: Home da classe AcessoUsuario.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class AcessoUsuarioHome extends AbstractEntityHome {
    public static AcessoUsuario findById(String acrCodigo, String usuCodigo) throws FindException {
        AcessoUsuarioId id = new AcessoUsuarioId(acrCodigo, usuCodigo);

        AcessoUsuario acessoUsuario = new AcessoUsuario();
        acessoUsuario.setId(id);

        return find(acessoUsuario, id);
    }

    private static AcessoUsuario create(String acrCodigo, String usuCodigo, int acuNumeroAcesso, Session session) throws CreateException {
        AcessoUsuario acessoUsuario = new AcessoUsuario();
        acessoUsuario.setId(new AcessoUsuarioId(acrCodigo, usuCodigo));
        acessoUsuario.setAcuNroAcesso(acuNumeroAcesso);

        return create(acessoUsuario, session);
    }

    public static void saveOrUpdate(String acrCodigo, String usuCodigo) throws CreateException, UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "UPDATE AcessoUsuario acu SET acu.acuNroAcesso = acu.acuNroAcesso + 1 WHERE acu.id.usuCodigo = :usuCodigo AND acu.id.acrCodigo = :acrCodigo";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("usuCodigo", usuCodigo);
            query.setParameter("acrCodigo", acrCodigo);

            int rows = query.executeUpdate();
            if (rows <= 0) {
                // Se nenhuma linha foi afetada, chama a criação
                create(acrCodigo, usuCodigo, 1, session);
            }
            session.flush();
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removeByAcessoRescurso(String acrCodigo) {
        Session session = SessionUtil.getSession();
        try {
            String hql = "DELETE FROM AcessoUsuario acu WHERE acu.acessoRecurso.acrCodigo = :acrCodigo";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("acrCodigo", acrCodigo);

            query.executeUpdate();
            session.flush();
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
