package com.zetra.econsig.webservice.rest.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;

/**
 * <p>Title: ErrorHandlerResponseFilter</p>
 * <p>Description: Response Filter para tratamento de erro e evitar retorno com
 * mensagens muito descritivas ao usuário final.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ErrorHandlerResponseFilter implements ContainerResponseFilter {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ErrorHandlerResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (responseContext.getStatusInfo().equals(Response.Status.BAD_REQUEST)) {
            LOG.error(responseContext.getEntity().toString());
            responseContext.setEntity("{\"mensagem\":\"" + responseContext.getStatusInfo().toString() + "\"}");
        }
    }
}
