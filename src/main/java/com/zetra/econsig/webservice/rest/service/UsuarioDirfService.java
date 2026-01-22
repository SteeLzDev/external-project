package com.zetra.econsig.webservice.rest.service;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.exception.ArquivoDirfControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.dirf.ArquivoDirfController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import org.apache.http.util.TextUtils;

/**
 * <p>Title: UsuarioDirfService</p>
 * <p>Description: Serviço REST para dirf do usuário.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/usuario")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class UsuarioDirfService extends RestService {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UsuarioDirfService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/consultarDirf")
    public Response consultarDirf() {

        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        String rseCodigo = responsavel.getRseCodigo();
        String matricula = responsavel.getRseMatricula();

        if (TextHelper.isNull(rseCodigo) || TextHelper.isNull(matricula)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_CONS_DIRF_SERVIDOR)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {

            ArquivoDirfController controller = ApplicationContextProvider.getApplicationContext().getBean(ArquivoDirfController.class);

            List<Short> anoCalendarioDirf = controller.listarAnoCalendarioDirf(responsavel.getSerCodigo(), responsavel);

            if (anoCalendarioDirf != null && !anoCalendarioDirf.isEmpty()) {
                return Response.status(Response.Status.OK).entity(anoCalendarioDirf).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            } else {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.consultar.dirf.nao.encontrada", null);
                return Response.status(Response.Status.NOT_FOUND).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

        } catch (ArquivoDirfControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/consultarDirf/{ano}")
    public Response consultarDirfPorAno(@PathParam("ano") Short ano) {

        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        String rseCodigo = responsavel.getRseCodigo();
        String matricula = responsavel.getRseMatricula();

        if (TextHelper.isNull(rseCodigo) || TextHelper.isNull(matricula)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_CONS_DIRF_SERVIDOR)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            ArquivoDirfController controller = ApplicationContextProvider.getApplicationContext().getBean(ArquivoDirfController.class);

            String conteudoDirfBase64 = controller.obterConteudoArquivoDirf(responsavel.getSerCodigo(), ano, responsavel);

            if (!TextUtils.isEmpty(conteudoDirfBase64)) {
                return Response.status(Response.Status.OK).entity(conteudoDirfBase64).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8").build();
            } else {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.consultar.dirf.nao.encontrada", null);
                return Response.status(Response.Status.NOT_FOUND).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

        } catch (ArquivoDirfControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.consultar.dirf.nao.encontrada", null);
            return Response.status(Response.Status.NOT_FOUND).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }
}
