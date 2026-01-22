package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AbstractEntityHome</p>
 * <p>Description: Classe pai para encapsular métodos para manipular entidade.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractEntityHome {

    protected static <T> T create(T t) throws CreateException {
        Session session = SessionUtil.getSession();
        try {
            t = create(t, session, true);
        } finally {
            SessionUtil.closeSession(session);
        }

        return t;
    }

    protected static <T> T create(T t, Session session) throws CreateException {
        t = create(t, session, true);
        return t;
    }

    protected static <T> T create(T t, Session session, boolean flush) throws CreateException {
        try {
            session.persist(t);
            if (flush) {
                session.flush();
            }
        } catch (Exception ex) {
            throw new CreateException(ex);
        }

        return t;
    }

    public static void update(Object objeto) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            // Copy the state of the given object onto the persistent object with the same identifier.
            session.merge(objeto);
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void remove(Object objeto) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            // Re-read the state of the given instance from the underlying database.
            Object merged = session.merge(objeto);
            session.remove(merged);
            session.flush();
        } catch (ConstraintViolationException ex) {
            throw new RemoveException("mensagem.erro.excluir.entidade.associada", (AcessoSistema) null);
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    protected static <T> List<T> findByQuery(String query, Map<String, Object> parameters) throws FindException {
        return findByQuery(query, parameters, null, null);
    }

    @SuppressWarnings("unchecked")
    protected static <T> List<T> findByQuery(String query, Map<String, Object> parameters, Integer maxResults, Integer firstResult) throws FindException {

        Session session = SessionUtil.getSession();
        try {
            Query<T> q = session.createQuery(query);

            if (maxResults != null) {
                q.setMaxResults(maxResults);
            }
            if (firstResult != null) {
                q.setFirstResult(firstResult);
            }

            if (parameters != null) {
                for (String name : parameters.keySet()) {
                    Object value = parameters.get(name);
                    if (value instanceof Collection<?>) {
                        q.setParameterList(name, (Collection<?>) value);
                    } else if (value instanceof Object[]) {
                        q.setParameterList(name, (Object[]) value);
                    } else {
                        q.setParameter(name, value);
                    }
                }
            }

            List<T> list = q.list();

            // Removendo a Entity do cache do hibernate.
            list.forEach(entity -> SessionUtil.evictFromSession(session, entity));

            return list;
        } catch (Exception ex) {
            throw new FindException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    protected static <T> T find(T t, Serializable filtro) throws FindException {
        return find(t, filtro, false);
    }

    @SuppressWarnings("unchecked")
    protected static <T> T find(T t, Serializable filtro, boolean lock) throws FindException {
        Session session = SessionUtil.getSession();
        try {
            if (lock) {
                t = (T) session.get(t.getClass(), filtro, LockMode.PESSIMISTIC_WRITE);
            } else {
                t = (T) session.get(t.getClass(), filtro);
            }
            if (t == null) {
                // Esta exceção não pode ser substituída enquanto o sistema de mensagem
                // estiver carregando as mensagens do ApplicationResources.properties.
                throw FindException.byMessage("Entidade não encontrada.");
            }

            // Removendo a Entity do cache do hibernate.
            session.evict(t);

            return t;
        } catch (Exception ex) {
            throw new FindException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
