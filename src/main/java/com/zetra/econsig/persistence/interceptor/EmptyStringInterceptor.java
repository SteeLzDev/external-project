package com.zetra.econsig.persistence.interceptor;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import com.zetra.econsig.persistence.dao.DAOFactory;

/**
 * <p>Title: EmptyStringInterceptor</p>
 * <p>Description: Interceptor que remove espaços em branco na leitura
 * de campos String e adiciona um espaço vazio na gravação de campos
 * String com conteúdo vazio. Estas modificações são necessárias para
 * o correto funcionamento no banco Oracle.
 * OBS: QUALQUER MÉTODO NOVO NESTE INTERCEPTOR DEVE SER INVOCADO
 * NA CLASSE ZetraHibernateInterceptor QUE É O INTERCEPTOR LIGADO
 * NO HIBERNATE.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EmptyStringInterceptor {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EmptyStringInterceptor.class);

    public EmptyStringInterceptor() {
        LOG.debug("Empty String Hibernate Interceptor Created.");
    }

    /**
     * Callback interceptor executado quando o Hibernate carrega uma nova entidade.
     * A alteração abaixo remove espaços em branco de campos não nulos do tipo
     * String, pois no Oracle não há o conceito de Strings vazias sendo tratadas
     * como NULL, assim os campos que não podem ser NULL mas aceitam Strings
     * vazias terão gravados um espaço em branco.
     * @param entity
     * @param id
     * @param state
     * @param propertyNames
     * @param types
     * @return
     */
    public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        if (DAOFactory.isOracle()) {
            for (int i = 0; i < types.length; i++) {
                if (StandardBasicTypes.STRING.getBindableJavaType().equals(types[i].getReturnedClass()) && (state[i] != null)) {
                    state[i] = state[i].toString().trim();
                }
            }
        }
        return true;
    }

    /**
     * Callback interceptor executado quando o Hibernate grava uma nova entidade no
     * banco de dados. A alteração abaixo evita que Strings vazias sejam
     * gravadas em campos char, varchar no Oracle, pois este não possui o conceito
     * de Strings vazias, sendo tratadas como NULL.
     * @param entity
     * @param id
     * @param state
     * @param propertyNames
     * @param types
     * @return
     */
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        if (DAOFactory.isOracle()) {
            for (int i = 0; i < types.length; i++) {
                if (StandardBasicTypes.STRING.getBindableJavaType().equals(types[i].getReturnedClass()) && (state[i] != null) && (state[i].toString().length() == 0)) {
                    state[i] = " ";
                }
            }
        }
        return true;
    }

    /**
     * Callback interceptor executado quando o Hibernate detecta que um objeto precisa
     * ser atualizado no banco. A alteração abaixo evita que Strings vazias sejam
     * gravadas em campos char, varchar no Oracle, pois este não possui o conceito
     * de Strings vazias, sendo tratadas como NULL.
     * OBS: The interceptor may modify the detected currentState, but it is strongly
     * recommended that the interceptor not modify the previousState. (Hibernate DOC)
     * @param entity
     * @param id
     * @param currentState
     * @param previousState
     * @param propertyNames
     * @param types
     * @return
     */
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (DAOFactory.isOracle()) {
            for (int i = 0; i < types.length; i++) {
                if (StandardBasicTypes.STRING.getBindableJavaType().equals(types[i].getReturnedClass()) && (currentState[i] != null) && (currentState[i].toString().length() == 0)) {
                    currentState[i] = " ";
                }
            }
        }
        return true;
    }
}
