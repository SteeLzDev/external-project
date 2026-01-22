package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: UsuarioChaveDispositivoHome</p>
 * <p>Description: Entidade De mapeamento de token de dispositivo a usuário Home</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioChaveDispositivoHome extends AbstractEntityHome {

    public static UsuarioChaveDispositivo findByPrimaryKey(String usuCodigo) throws FindException {
        final UsuarioChaveDispositivo usuarioChaveDispositivo = new UsuarioChaveDispositivo();
        usuarioChaveDispositivo.setUsuCodigo(usuCodigo);
        return find(usuarioChaveDispositivo, usuCodigo);
    }

    public static UsuarioChaveDispositivo create(String usuCodigo, String tdiCodigo, String deviceToken) throws CreateException {
        final Session session = SessionUtil.getSession();
        final UsuarioChaveDispositivo bean = new UsuarioChaveDispositivo();

        try {
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setUsuCodigo(usuCodigo);
            bean.setTipoDispositivo(session.getReference(TipoDispositivo.class, tdiCodigo));
            bean.setUcdDataCriacao(DateHelper.getSystemDatetime());
            bean.setUcdDataUtilizacao(DateHelper.getSystemDatetime());
            bean.setUcdToken(deviceToken);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void update(String usuCodigo, String tdiCodigo, String deviceToken) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE UsuarioChaveDispositivo set ucdToken = :ucdToken, ucdDataCriacao =:ucdDataCriacao, tipoDispositivo.tdiCodigo =:tipoDispositivoCod WHERE usuCodigo = :usuCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("ucdToken", deviceToken);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.setParameter("tipoDispositivoCod", tdiCodigo);
            queryUpdate.setParameter("ucdDataCriacao", DateHelper.getSystemDatetime());
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateUtilizacao(String usuCodigo) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE UsuarioChaveDispositivo set ucdDataUtilizacao =:ucdDataUtilizacao WHERE usuCodigo = :usuCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.setParameter("ucdDataUtilizacao", DateHelper.getSystemDatetime());
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static List<UsuarioChaveDispositivo> buscarLote(Date ultimaData) throws FindException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "FROM UsuarioChaveDispositivo "
                        + "WHERE ucdDataCriacao > :ultimaData "
                        + "ORDER BY ucdDataCriacao ASC";

            final Query<UsuarioChaveDispositivo> query = session.createQuery(hql, UsuarioChaveDispositivo.class);
            query.setParameter("ultimaData", ultimaData);
            query.setMaxResults(2000);

            return query.list();
        } catch (final Exception ex) {
            throw new FindException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
