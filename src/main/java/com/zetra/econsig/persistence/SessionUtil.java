package com.zetra.econsig.persistence;

import com.zetra.econsig.web.ApplicationContextProvider;

import org.hibernate.Session;

/**
 * <p> Title: SessionUtil</p>
 * <p> Description: Gestor de sessões do Hibernate.</p>
 * <p> Copyright: Copyright (c) 2002-2019</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SessionUtil {
	public static Session getSession() {
		HibernateSessionFactory factory = ApplicationContextProvider.getApplicationContext().getBean(HibernateSessionFactory.class);
		return factory.getSession();
	}

	/**
	 * Fecha a session informada.
	 * @param session
	 */
	public static void closeSession(Session session) {
		// Não faz nada
	}

	/**
	 * Cuidado, ao chamar isso qualquer objeto entity que não foi salvo será perdido.
	 * @param session
	 */
	public static void clearSession(Session session) {
	    if (session != null) {
	        session.clear();
	    }
	}

	/**
	 * Realizar flush em momentos pontuias.
	 * @param session
	 */
	public static void flushSession(Session session) {
	    if (session != null) {
            session.flush();
        }
	}

	public static boolean isManaged(Session session, Object entity) {
        try {
            return session.contains(entity);
        } catch (IllegalArgumentException ex) {
            return false;
        }
	}

	public static void evictFromSession(Session session, Object entity) {
	    if (entity != null) {
	        if (entity instanceof Object[]) {
	            Object[] entities = (Object[]) entity;
	            for (Object entity2 : entities) {
	                evictFromSession(session, entity2);
	            }
	        } else if (isManaged(session, entity)) {
	            session.evict(entity);
	        }
	    }
	}
}
