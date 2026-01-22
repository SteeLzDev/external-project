package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: DirfServidorHome</p>
 * <p>Description: Classe Home para a entidade DirfServidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DirfServidorHome extends AbstractEntityHome {

    public static DirfServidor findByPrimaryKey(DirfServidorId pk) throws FindException {
        DirfServidor dirfServidor = new DirfServidor();
        dirfServidor.setId(pk);
        return find(dirfServidor, pk);
    }

    public static List<DirfServidor> listAnoCalendarioBySerCodigo(String serCodigo) throws FindException {
        String query = "FROM DirfServidor dis WHERE dis.servidor.serCodigo = :serCodigo ORDER BY dis.id.disAnoCalendario";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);

        return findByQuery(query, parameters);
    }

    public static DirfServidor create(Short disAnoCalendario, String serCodigo, String arqCodigo) throws CreateException {
        DirfServidor bean = new DirfServidor();
        Session session = SessionUtil.getSession();

        try {
            DirfServidorId id = new DirfServidorId();
            id.setSerCodigo(serCodigo);
            id.setDisAnoCalendario(disAnoCalendario);

            bean.setId(id);
            bean.setDisDataCarga(DateHelper.getSystemDatetime());
            bean.setArquivo(session.getReference(Arquivo.class, arqCodigo));

            create(bean);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DirfServidor dis WHERE dis.servidor.serCodigo = :serCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("serCodigo", serCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
