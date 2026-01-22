package com.zetra.econsig.webservice.rest.service;

import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.TermoAdesaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.sistema.TermoAdesaoController;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.TermoAdesaoAceiteRestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/termosAdesao")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class TermosDeAdesaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TermosDeAdesaoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/aceitarTermoAdesao")
    public Response aceitarTermoAdesao(TermoAdesaoAceiteRestRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if ((request.tadCodigo == null) || request.tadCodigo.isBlank()) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.termo.codigo.nao.encontrado", null);
            return Response.status(Response.Status.NOT_FOUND).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (request.aceite == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.termo.aceite.nao.encontrado", null);
            return Response.status(Response.Status.NOT_FOUND).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if ((request.deviceToken == null) || request.deviceToken.isBlank()) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.device.token.nao.encontrado", null);
            return Response.status(Response.Status.NOT_FOUND).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (!responsavel.isSer()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                final TermoAdesaoController termoAdesaoController = ApplicationContextProvider.getApplicationContext().getBean(TermoAdesaoController.class);

                termoAdesaoController.createLeituraTermoAdesaoUsuario(request.tadCodigo, request.aceite, request.deviceToken, responsavel);
            } catch (final TermoAdesaoControllerException e) {
                throw new RuntimeException(e);
            }
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Secured
    @Path("/buscarTermoAdesao")
    public Response consultarTermoAdesao() {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        List<TransferObject> termoAdesaoTO;

        if (!responsavel.isSer()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            try {
                final TermoAdesaoController termoAdesaoController = ApplicationContextProvider.getApplicationContext().getBean(TermoAdesaoController.class);
                termoAdesaoTO = termoAdesaoController.findTermoAdesaoAceite(responsavel);
            } catch (final TermoAdesaoControllerException e) {
                throw new RuntimeException(e);
            }
        }

        final List<String> filter = Arrays.asList("tad_html", "tad_permite_recusar", "ltu_termo_aceito", "tad_permite_ler_depois", "tad_codigo","ltu_data", "tad_data", "tad_texto", "tad_titulo", "tad_sequencia","fun_codigo", "aceiteValido");
        return Response.status(Response.Status.OK).entity(transformTOs(termoAdesaoTO, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }
}
