package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ReclamacaoMotivoHome</p>
 * <p>Description: Classe Home para a entidade ReclamacaoMotivo</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReclamacaoMotivoHome extends AbstractEntityHome {

    public static ReclamacaoMotivo findByPrimaryKey(ReclamacaoMotivoId pk) throws FindException {
        ReclamacaoMotivo reclamacaoMotivo = new ReclamacaoMotivo();
        reclamacaoMotivo.setId(pk);
        return find(reclamacaoMotivo, pk);
    }

    public static ReclamacaoMotivo create(String tmrCodigo, String rrsCodigo) throws CreateException {
        ReclamacaoMotivo bean = new ReclamacaoMotivo();

        ReclamacaoMotivoId id = new ReclamacaoMotivoId();
        id.setTmrCodigo(tmrCodigo);
        id.setRrsCodigo(rrsCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ReclamacaoMotivo rmt WHERE rmt.id.rrsCodigo in ( SELECT rrs.rrsCodigo FROM ReclamacaoRegistroSer rrs WHERE rrs.registroServidor.rseCodigo = :rseCodigo) ");

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
