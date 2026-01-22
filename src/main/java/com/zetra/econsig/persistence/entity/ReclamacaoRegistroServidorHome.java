package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ReclamacaoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade ReclamacaoRegistroSer</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReclamacaoRegistroServidorHome extends AbstractEntityHome {

    public static ReclamacaoRegistroSer findByPrimaryKey(String rrsCodigo) throws FindException {
        ReclamacaoRegistroSer reclamacao = new ReclamacaoRegistroSer();
        reclamacao.setRrsCodigo(rrsCodigo);
        return find(reclamacao, rrsCodigo);
    }

    public static String create(String rseCodigo, String csaCodigo, Date rrsData, String rrsTexto, String rrsIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        ReclamacaoRegistroSer bean = new ReclamacaoRegistroSer();
        String codigo = null;
        try {
            codigo = DBHelper.getNextId();
            bean.setRrsCodigo(codigo);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setRrsData(rrsData);
            bean.setRrsTexto(rrsTexto);
            bean.setRrsIpAcesso(rrsIpAcesso);

            create(bean, session);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }
        return codigo;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ReclamacaoRegistroSer rrs WHERE rrs.registroServidor.rseCodigo = :rseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}