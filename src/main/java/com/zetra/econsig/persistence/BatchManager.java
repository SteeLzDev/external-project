package com.zetra.econsig.persistence;

import org.hibernate.Session;

/**
 * <p>Title: BatchManager</p>
 * <p>Description: Classe auxiliar para fazer o gerenciamento da quantidade de iterações a serem executadas antes de limpar a
 * a sessão do Hibernate, por motivos de performace.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BatchManager {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BatchManager.class);

    private static int DEFAULT_THRESHOLD = 25;

    private int threshold = DEFAULT_THRESHOLD;

    private int current = 0;

    private Long total = 0L;

    private boolean debug = true;

    private Session session;

    public BatchManager(Session session) {
        this.session = session;
    }

    public BatchManager setThresold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    public BatchManager disableLogDebug() {
        this.debug = false;
        return this;
    }

    public Long getTotal() {
        return total;
    }


    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
        current = 0;
        total = 0L;
    }

    public void iterate() {
        if (session == null) {
            LOG.debug("Session is not defined. ");
            return;
        }
        current++;
        total++;
        if (current >= threshold) {
            if (debug) {
                final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
                String callerClassName = null;
                for (int i = 1; i < stElements.length; i++) {
                    final StackTraceElement ste = stElements[i];
                    if (!ste.getClassName().equals(BatchManager.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                        callerClassName = ste.getClassName();
                        break;
                    }
                }
                LOG.debug("Cleaning hibernate session - context: " + callerClassName + " (" + current + " / " + total + ")");
            }
            if (session.isJoinedToTransaction()) {
                session.flush();
            }
            session.clear();
            current = 0;
        }
    }

    public void finish() {
        if (session == null) {
            LOG.debug("Session is not defined. ");
            return;
        }
        if (session.isJoinedToTransaction()) {
            session.flush();
        }
        session.clear();
    }
}
