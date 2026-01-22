package com.zetra.econsig.persistence.interceptor;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * <p>Title: ZetraStatementInspector</p>
 * <p>Description: Interceptor de Statements geral que utiliza as classes
 * de implementações para realizar os tratamentos necessários.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ZetraStatementInspector implements StatementInspector {

    private static final long serialVersionUID = 1L;

    private final IndexHintInterceptor indexHintInterceptor;

    public ZetraStatementInspector() {
        indexHintInterceptor = new IndexHintInterceptor();
    }

    @Override
    public String inspect(String sql) {
        return indexHintInterceptor.onPrepareStatement(sql);
    }
}
