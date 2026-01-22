package com.zetra.econsig.webservice.rest.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.values.CodedValues;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.ShowRestRequest;
import com.zetra.econsig.webservice.rest.request.SolicitacaoLeilaoRestRequest;

/**
 * <p>Title: SolicitacaoLeilaoService</p>
 * <p>Description: Serviço REST para solicitações de leilão.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/leilao")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SolicitacaoLeilaoService extends RestService {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoLeilaoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/listaPls")
    public Response listaPls(SolicitacaoLeilaoRestRequest request) {
        if (request == null) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_ACOMPANHAR_LEILAO_VIA_SIMULACAO), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        String adeCodigo = request.adeCodigo;

        if (TextHelper.isNull(adeCodigo)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.informe.codigo.solicitacao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        try {
            LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);

            List<TransferObject> lstPls = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(adeCodigo, null, null, false, responsavel);

            if (lstPls != null && !lstPls.isEmpty()) {
                List<String> filter = Arrays.asList("pls_numero", "pls_taxa_juros", "pls_prazo", "stp_codigo", "stp_descricao", "pls_data_cadastro", "pls_data_validade", "pls_valor_parcela");
                return Response.status(Response.Status.OK).entity(transformTOs(lstPls, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            } else {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.nenhuma.proposta.leilao.encontrada", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } catch (LeilaoSolicitacaoControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/acompanhar")
    public Response acompanharLeilao(ShowRestRequest dados) {

        if (dados == null) {
            dados = new ShowRestRequest();
        }
        if (dados.size == null) {
            dados.size = 20;
        }
        if (dados.offset == null) {
            dados.offset = 0;
        }

        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_ACOMPANHAR_LEILAO_VIA_SIMULACAO), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        String rseCodigo = responsavel.getRseCodigo();
        String matricula = responsavel.getRseMatricula();

        if (TextHelper.isNull(rseCodigo) || TextHelper.isNull(matricula)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        List<TransferObject> lstLeilaoSolicitacaoResult = null;
        try {
            TransferObject criteriosPesquisa = new CustomTransferObject();
            if(dados.id != null) {
                criteriosPesquisa.setAttribute("ADE_CODIGO", dados.id);
            }
            criteriosPesquisa.setAttribute("filtro", "4");
            criteriosPesquisa.setAttribute("ORDENACAO", "[ORD14;ASC],[ORD01;ASC]");
            LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
            lstLeilaoSolicitacaoResult = leilaoSolicitacaoController.acompanharLeilaoSolicitacao(criteriosPesquisa, dados.offset, dados.size, responsavel);
        } catch (LeilaoSolicitacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
        return Response.status(Response.Status.OK).entity(transformTOs(lstLeilaoSolicitacaoResult, null)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/obtemMelhorTaxa")
    public Response acompanharLeilao(SolicitacaoLeilaoRestRequest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_ACOMPANHAR_LEILAO_VIA_SIMULACAO), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        String adeCodigo = dados.adeCodigo;

        if (TextHelper.isNull(adeCodigo)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.informe.codigo.solicitacao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        try {
            LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
            BigDecimal melhorTaxa = leilaoSolicitacaoController.obterMelhorTaxaLeilao(adeCodigo, responsavel);
            Map<String, Object> taxaMap = new HashMap<>();
            taxaMap.put("PLS_TAXA_JUROS", melhorTaxa);

            return Response.status(Response.Status.OK).entity(taxaMap).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (LeilaoSolicitacaoControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.codigo.solicitacao.nao.econtrada", null);
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

    }
}
