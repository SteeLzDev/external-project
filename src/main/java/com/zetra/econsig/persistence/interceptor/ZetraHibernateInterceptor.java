package com.zetra.econsig.persistence.interceptor;

import java.io.Serializable;

import org.hibernate.Interceptor;
import org.hibernate.type.Type;

/**
 * <p>Title: ZetraHibernateInterceptor</p>
 * <p>Description: Interceptor geral que utiliza as classes
 * de implementações para realizar os tratamentos necessários.
 * Esta classe é necessária pois o hibernate só aceita um
 * interceptor na construção da sessão.</p>
 * <p>Copyright: Copyright (c) 2013-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ZetraHibernateInterceptor implements Interceptor, Serializable {

    private static final long serialVersionUID = 7L;

    private final EmptyStringInterceptor emptyStringInterceptor;

    public ZetraHibernateInterceptor() {
        emptyStringInterceptor = new EmptyStringInterceptor();
    }

    @Override
    public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        return emptyStringInterceptor.onLoad(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        return emptyStringInterceptor.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return emptyStringInterceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
