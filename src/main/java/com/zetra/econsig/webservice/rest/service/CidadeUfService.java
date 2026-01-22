package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.CidadeRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: CidadeUfService</p>
 * <p>Description: Serviço REST para recuperação de cidades</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/cidade")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class CidadeUfService extends RestService {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CidadeUfService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/lstCidades")
    public Response lstCidades(CidadeRequest req) {

        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);

        try {
            List<TransferObject> lstCidades = sistemaController.lstCidadeUf(req.ufCod, req.termo, responsavel);
            List<TransferObject> lstRetorno = new ArrayList<>();
            for (TransferObject toCid : lstCidades) {
                toCid.setAttribute("label_nome", toCid.getAttribute(Columns.CID_NOME)+"/"+toCid.getAttribute(Columns.CID_UF_CODIGO));
                lstRetorno.add(toCid);
            }

            List<String> filter = Arrays.asList("cid_codigo", "cid_nome","uf_cod", "cid_ddd", "label_nome");
            return Response.status(Response.Status.OK).entity(transformTOs(lstRetorno, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE.withCharset("utf-8")).build();

        } catch (ConsignanteControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

    }

}
