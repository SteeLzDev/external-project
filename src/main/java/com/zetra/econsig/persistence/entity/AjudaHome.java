package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AjudaHome</p>
 * <p>Description: Classe Home para a entidade Ajuda</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AjudaHome extends AbstractEntityHome {

    public static Ajuda findByPrimaryKey(String acrCodigo) throws FindException {
        final Ajuda ajuda = new Ajuda();
        ajuda.setAcrCodigo(acrCodigo);
        return find(ajuda, acrCodigo);
    }

    public static Ajuda create(String acrCodigo, String usuCodigo, String ajuTitulo, String ajuTexto, Date ajuDataAlteracao, Short ajuSequencia, Short ajuAtivo) throws CreateException {
        final Session session = SessionUtil.getSession();
        final Ajuda bean = new Ajuda();
        try {
            bean.setAcrCodigo(acrCodigo);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setAjuTitulo(ajuTitulo);
            bean.setAjuTexto(ajuTexto);
            bean.setAjuDataAlteracao(ajuDataAlteracao);
            bean.setAjuSequencia(ajuSequencia);
            bean.setAjuAtivo(ajuAtivo);
            bean.setAjuHtml("N");

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeByAcessoRescurso(String acrCodigo) {
        final Session session = SessionUtil.getSession();
        final String queryDelete = "DELETE FROM Ajuda aju WHERE aju.acrCodigo = :acrCodigo";
        final MutationQuery query = session.createMutationQuery(queryDelete);
        query.setParameter("acrCodigo", acrCodigo);
        query.executeUpdate();
        SessionUtil.closeSession(session);
    }
}
