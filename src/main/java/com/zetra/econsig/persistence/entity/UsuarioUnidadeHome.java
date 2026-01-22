package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: UsuarioUnidadeHome</p>
 * <p>Description: Classe Home para a entidade UsuarioUnidade</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioUnidadeHome extends AbstractEntityHome {

    public static UsuarioUnidade findByPrimaryKey(String usuCodigo) throws FindException {
        UsuarioUnidade usuarioUnidade = new UsuarioUnidade();
        usuarioUnidade.setUsuCodigo(usuCodigo);
        return find(usuarioUnidade, usuCodigo);
    }

    public static List<UsuarioUnidade> listUniCodigosByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioUnidade usuUni WHERE usuUni.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static UsuarioUnidade create(String usuCodigo, String uniCodigo) throws CreateException {
        UsuarioUnidade bean = new UsuarioUnidade();
        UsuarioUnidadeId id = new UsuarioUnidadeId();
        id.setUsuCodigo(usuCodigo);
        id.setUniCodigo(uniCodigo);
        bean.setId(id);
        create(bean);

        return bean;
    }

    public static void deleteByUsucodigo(String usuCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "DELETE FROM UsuarioUnidade usuUni WHERE usuUni.usuCodigo = :usuCodigo ";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
