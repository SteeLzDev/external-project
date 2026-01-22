package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Date;
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

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.boleto.BoletoServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.BoletoServidorRestResponse;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: BoletoServidorService</p>
 * <p>Description: Serviço REST para consulta de boletos do servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/boleto")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class BoletoServidorService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BoletoServidorService.class);

    @Context
    SecurityContext securityContext;

    private Response validarOperacao(AcessoSistema responsavel) {
        if (!responsavel.isSer() || TextHelper.isNull(responsavel.getRseCodigo())) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_CONSULTAR_BOLETO)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        return null;
    }

    @POST
    @Secured
    @Path("/listar")
    public Response listar() {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        try {
            BoletoServidorController boletoServidorController = ApplicationContextProvider.getApplicationContext().getBean(BoletoServidorController.class);
            List<TransferObject> lstBoletoServidor = boletoServidorController.listarBoletoServidor(null, -1, -1, responsavel);
            List<BoletoServidorRestResponse> boletos = new ArrayList<>();

            if (lstBoletoServidor != null && !lstBoletoServidor.isEmpty()) {
                for (TransferObject boletoServidor : lstBoletoServidor) {
                    BoletoServidorRestResponse boleto = new BoletoServidorRestResponse();
                    boleto.codigo = boletoServidor.getAttribute(Columns.BOS_CODIGO).toString();
                    boleto.consignataria = boletoServidor.getAttribute(Columns.CSA_NOME).toString();
                    boleto.dataUpload = DateHelper.toISOString((Date) boletoServidor.getAttribute(Columns.BOS_DATA_UPLOAD));
                    boleto.dataDownload = (boletoServidor.getAttribute(Columns.BOS_DATA_DOWNLOAD) != null ? DateHelper.toISOString((Date) boletoServidor.getAttribute(Columns.BOS_DATA_DOWNLOAD)) : "");
                    boletos.add(boleto);
                }
            }

            return Response.status(Response.Status.OK).entity(boletos).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (BoletoServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }
    }

    @POST
    @Secured
    @Path("/download/{id}")
    public Response download(@PathParam("id") String codigo) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        try {
            // Pesquisa o boleto pelo código
            BoletoServidorController boletoServidorController = ApplicationContextProvider.getApplicationContext().getBean(BoletoServidorController.class);
            TransferObject boletoServidor = boletoServidorController.findBoletoServidor(codigo, responsavel);
            String arqConteudo = boletoServidor.getAttribute(Columns.ARQ_CONTEUDO).toString();

            // Gera log de download de arquivo
            LogDelegate log = new LogDelegate(responsavel, Log.BOLETO_SERVIDOR, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.setBoletoServidor(codigo);
            log.write();

            // Registra o download do boleto
            boletoServidorController.atualizaDataDownloadBoleto(codigo, responsavel);

            // Gera o resultado para o cliente
            BoletoServidorRestResponse boleto = new BoletoServidorRestResponse();
            boleto.conteudo = arqConteudo;

            return Response.status(Response.Status.OK).entity(boleto).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (LogControllerException | BoletoServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }
    }
}
