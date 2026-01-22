package com.zetra.econsig.webservice.rest.service;

import java.io.IOException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.json.simple.parser.ParseException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SolicitacaoSuporteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.solicitacaosuporte.SolicitacaoSuporteConfig;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.solicitacaosuporte.SolicitacaoSuporteController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.SolicitacaoSuporteRestRequest;

/**
 * <p>Title: SolicitacaoSuporteService</p>
 * <p>Description: Serviço REST para solicitação suporte.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/solicitacaosuporte")
@Produces({ MediaType.APPLICATION_JSON })
public class SolicitacaoSuporteService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoSuporteService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/inserir")
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response inserirSolicitacao(SolicitacaoSuporteRestRequest dados) throws ZetraException, IOException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
        } catch (final Exception ex) {
            LOG.error("ERRO JiraException NO JIRA: " + ex.getMessage());
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.jira.erro.interno", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        final String sosServico = ssc.getMobileSosServico();

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        //inicialmente a conferência não faz nada porque o código está fixo em 27625 pelo
        if(dados.sosServico == null || dados.sosServico.isEmpty()) {
            return genericError(new ZetraException("mensagem.informe.sos.servico", responsavel));
        }

        if(dados.sosDescricao == null || dados.sosDescricao.isEmpty()) {
            return genericError(new ZetraException("mensagem.informe.sos.sumario", responsavel));
        }

        if(dados.sosSumario == null || dados.sosSumario.isEmpty()) {
            return genericError(new ZetraException("mensagem.informe.sos.descricao", responsavel));
        }

        final SolicitacaoSuporteController solicitacaoSuporteController = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteController.class);
        final TransferObject sosTO = new CustomTransferObject();
        //DESENV-17590 -  tipo de serviço JEMH, para este campo o Jira utiliza o custom field de número 27625
        sosTO.setAttribute(Columns.SOS_SERVICO_TRANSIENTE, sosServico);
        sosTO.setAttribute(Columns.SOS_SUMARIO, dados.sosSumario);
        sosTO.setAttribute(Columns.SOS_DESCRICAO_TRANSIENTE, dados.sosDescricao);

        String sosChave;
        try{
            sosChave = solicitacaoSuporteController.criarSolicitacaoSuporte(sosTO, responsavel);
        } catch ( final SolicitacaoSuporteControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).entity(sosChave).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/totem")
    public Response inserirSolicitacaoTotem(SolicitacaoSuporteRestRequest dados) throws ZetraException, IOException, ParseException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if(dados.sosDescricao == null || dados.sosDescricao.isEmpty()) {
            return genericError(new ZetraException("mensagem.informe.sos.sumario", responsavel));
        }

        if(dados.sosSumario == null || dados.sosSumario.isEmpty()) {
            return genericError(new ZetraException("mensagem.informe.sos.descricao", responsavel));
        }

        if(dados.matricula == null || dados.matricula.isEmpty()) {
            return genericError(new ZetraException("mensagem.informe.sos.matricula", responsavel));
        }

        final SolicitacaoSuporteController solicitacaoSuporteController = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteController.class);
        final TransferObject sosTO = new CustomTransferObject();
        sosTO.setAttribute(Columns.SOS_SUMARIO, dados.sosSumario);
        sosTO.setAttribute(Columns.SOS_DESCRICAO_TRANSIENTE, dados.sosDescricao);
        sosTO.setAttribute(Columns.SOS_MATRICULA, dados.matricula);
        sosTO.setAttribute(Columns.SOS_USUARIO_SUPORTE, dados.usuarioSuporte);
        sosTO.setAttribute(Columns.SOS_ARQUIVO, dados.arquivo);
        sosTO.setAttribute(Columns.SOS_TOTEM, Boolean.TRUE);

        String sosChave;
        try{
            sosChave = solicitacaoSuporteController.criarSolicitacaoSuporte(sosTO, responsavel);
        } catch ( final SolicitacaoSuporteControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).entity(sosChave).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }
}
