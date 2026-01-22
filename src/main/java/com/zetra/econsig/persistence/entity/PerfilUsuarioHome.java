package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: PerfilUsuarioHome</p>
 * <p>Description: Classe Home para a entidade PerfilUsuario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilUsuarioHome extends AbstractEntityHome {

    public static List<PerfilUsuario> findByPerfil(String perCodigo) throws FindException {
        String query = "FROM PerfilUsuario AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }

    public static PerfilUsuario findByPrimaryKey(String usuCodigo) throws FindException {
        PerfilUsuario perfilUsu = new PerfilUsuario();
        perfilUsu.setUsuCodigo(usuCodigo);
        return find(perfilUsu, usuCodigo);
    }

    public static PerfilUsuario create(String usuCodigo, String perCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        PerfilUsuario bean = new PerfilUsuario();

        try {
            bean.setUsuCodigo(usuCodigo);
            bean.setPerfil(session.getReference(Perfil.class, perCodigo));
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static List<PerfilUsuario> findByPerCodigo(String perCodigo) throws FindException {
        String query = "FROM PerfilUsuario AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }
}
