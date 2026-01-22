package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: UsuarioSerHome</p>
 * <p>Description: Classe Home para a entidade UsuarioSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioSerHome extends AbstractEntityHome {

    public static UsuarioSer findByPrimaryKey(UsuarioSerId id) throws FindException {
        UsuarioSer usuarioSer = new UsuarioSer();
        usuarioSer.setId(id);
        return find(usuarioSer, id);
    }

    public static UsuarioSer findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioSer usuSer WHERE usuSer.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<UsuarioSer> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<UsuarioSer> listByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioSer usuSer WHERE usuSer.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }
    public static UsuarioSer create(String serCodigo, String usuCodigo, String stuCodigo) throws CreateException {
        UsuarioSer bean = new UsuarioSer();

        UsuarioSerId id = new UsuarioSerId();
        id.setSerCodigo(serCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }

    public static void removeByUsu(String usuCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM UsuarioSer usuSer WHERE usuSer.usuario.usuCodigo = :usuCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("usuCodigo", usuCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM UsuarioSer usuSer WHERE usuSer.servidor.serCodigo = :serCodigo ");

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
