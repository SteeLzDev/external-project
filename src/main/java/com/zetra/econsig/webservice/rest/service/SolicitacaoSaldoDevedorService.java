package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import br.com.nostrum.simpletl.util.TextHelper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: DashboardService</p>
 * <p>Description: Service para chamadas REST para consulta de saldo devedor</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/solicitacaoSaldoDevedor")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SolicitacaoSaldoDevedorService extends RestService {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoSaldoDevedorService.class);

    @Context
    SecurityContext securityContext;

    @GET
    @Secured
    @Path("/byRseCodigo/{rseCodigo}")
    public Response listaSolicitacaoSaldoDevedorByRseCodigo(@PathParam("rseCodigo") String rseCodigo) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.isCseSup() || !responsavel.temPermissao(CodedValues.FUN_LISTAR_SOLICITACAO_SALDO_DEVEDOR)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
        
        if (TextHelper.isNull(rseCodigo)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final AutorizacaoDelegate autDelegate = new AutorizacaoDelegate();
        final List<Map<String, Object>> retorno = new ArrayList<>();

        try {
            retorno.addAll(transformTOs(autDelegate.listaSolicitacaoSaldoDevedorPorRegistroServidor(rseCodigo, responsavel), 
                null));
        } catch (AutorizacaoControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

}
