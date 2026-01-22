package com.zetra.econsig.webservice.rest.service;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.MensagemRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>Title: MensagemService</p>
 * <p>Description: Serviço REST para mensagens.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/mensagem")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Controller
public class MensagemService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UsuarioService.class);

    @Context
    private SecurityContext securityContext;

    @Autowired
    private MensagemController mensagemController;

    @POST
    @Secured
    @Path("/registrarLeituraMensagem")
    public Response registrarLeituraMensagem(MensagemRestRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.isCseSupSer()) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel);
            return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.LMU_MEN_CODIGO, request.menCodigo);
            criterio.setAttribute(Columns.LMU_USU_CODIGO, responsavel.getUsuCodigo());
            criterio.setAttribute(Columns.LMU_DATA, DateHelper.getSystemDatetime());

            mensagemController.createLeituraMensagemUsuario(criterio, responsavel);
        } catch (final MensagemControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            final ResponseRestRequest responseError = new ResponseRestRequest();

            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }
}
