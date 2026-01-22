package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: PerfilHome</p>
 * <p>Description: Classe Home para a entidade Perfil</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilHome extends AbstractEntityHome {

    public static List<Perfil> findByPapel(String papCodigo) throws FindException {
        final String query = "FROM Perfil AS p WHERE p.papel.papCodigo = :papCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("papCodigo", papCodigo);

        return findByQuery(query, parameters);
    }

    public static Perfil findByPrimaryKey(String perCodigo) throws FindException {
        final Perfil perfil = new Perfil();
        perfil.setPerCodigo(perCodigo);
        return find(perfil, perCodigo);
    }

    public static Perfil create(String papCodigo, String perDescricao, String perVisivel, Date perDataExpiracao, String perAutoDesbloqueio, String perEntAltera, String perIpAcesso, String perDdnsAcesso) throws CreateException {

        final Session session = SessionUtil.getSession();
        final Perfil bean = new Perfil();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPerCodigo(objectId);
            bean.setPapel(session.getReference(Papel.class, papCodigo));
            bean.setPerDescricao(perDescricao);
            bean.setPerVisivel(perVisivel);
            bean.setPerDataExpiracao(perDataExpiracao);
            bean.setPerEntAltera(perEntAltera);
            bean.setPerAutoDesbloqueio(perAutoDesbloqueio);
            bean.setPerIpAcesso(perIpAcesso);
            bean.setPerDdnsAcesso(perDdnsAcesso);
            create(bean, session);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static List<Perfil> findPerfilExpirado() throws FindException {
        final String query = "FROM Perfil AS p WHERE p.perDataExpiracao <= :dataExpiracao";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("dataExpiracao", DateHelper.getSystemDate());

        return findByQuery(query, parameters);
    }
}
