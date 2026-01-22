package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RelacionamentoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade RelacionamentoRegistroServidorHome</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelacionamentoRegistroServidorHome extends AbstractEntityHome {

    public static RelacionamentoRegistroSer createRelRegistroServidor(String rseCodigoOrigem, String rseCodigoDestino,
            String tntCodigo, String usuCodigo, Date rreData, AcessoSistema responsavel) throws CreateException {

        Session session = SessionUtil.getSession();
        RelacionamentoRegistroSerId id = new RelacionamentoRegistroSerId(rseCodigoOrigem, rseCodigoDestino, tntCodigo);
        RelacionamentoRegistroSer bean = null;

        try {
            bean = findByPrimaryKey(id);
            bean.setRreData(rreData);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));

            RelacionamentoRegistroServidorHome.update(bean);

        } catch (UpdateException | FindException ex) {
            bean = new RelacionamentoRegistroSer(id);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setRreData(rreData);
            create(bean, session);

        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static RelacionamentoRegistroSer findByPrimaryKey(RelacionamentoRegistroSerId pk) throws FindException {
        RelacionamentoRegistroSer bean = new RelacionamentoRegistroSer();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM RelacionamentoRegistroSer rre WHERE rre.registroServidorOrigem.rseCodigo = :rseCodigo or rre.registroServidorDestino.rseCodigo = :rseCodigo ");

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
