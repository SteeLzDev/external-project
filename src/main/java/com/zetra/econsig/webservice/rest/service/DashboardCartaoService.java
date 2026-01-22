package com.zetra.econsig.webservice.rest.service;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.DashboadCartaoRequest;
import com.zetra.econsig.webservice.rest.request.DashboardCartaoDadosProcessamentoResponse;
import com.zetra.econsig.webservice.rest.request.DashboardCartaoResponse;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/dashboardCartao")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DashboardCartaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DashboardCartaoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/consulta")
    public Response consulta(DashboadCartaoRequest request) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
            final HistoricoArquivoController historicoArquivoController = ApplicationContextProvider.getApplicationContext().getBean(HistoricoArquivoController.class);

            final Date datePeridoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
            final int dataProxDiaCorte = PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel);

            Date filterPeriodo = null;
            if (!TextHelper.isNull(request.getPeriodo())) {
                filterPeriodo = DateHelper.parse(request.getPeriodo() + "-01", "yyyy-MM-dd");
            } else {
                filterPeriodo = datePeridoAtual;
            }

            final List<TransferObject> historicosArquivos = historicoArquivoController.lstHistoricoArquivosDashboard(responsavel.getCsaCodigo(), filterPeriodo, responsavel);
            final ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            final DashboardCartaoResponse dashboardCartaoResponse = new DashboardCartaoResponse();
            dashboardCartaoResponse.setCseNome(cse.getCseNome());
            dashboardCartaoResponse.setPeriodoAtual(datePeridoAtual != null ? datePeridoAtual.toString() : null);
            dashboardCartaoResponse.setDiaDeCorte(!TextHelper.isNull(dataProxDiaCorte) ? dataProxDiaCorte : 0);
            if (!TextHelper.isNull(historicosArquivos)) {
                for (final TransferObject hist : historicosArquivos) {
                    final DashboardCartaoDadosProcessamentoResponse dashboardCartaoDadosProcessamentoResponse = new DashboardCartaoDadosProcessamentoResponse();
                    dashboardCartaoDadosProcessamentoResponse.setHarDataProc(hist.getAttribute(Columns.HAR_DATA_PROC) != null ? hist.getAttribute(Columns.HAR_DATA_PROC).toString() : null);
                    dashboardCartaoDadosProcessamentoResponse.setHarQtdLinhas(hist.getAttribute(Columns.HAR_QTD_LINHAS) != null ? hist.getAttribute(Columns.HAR_QTD_LINHAS).toString() : null);
                    dashboardCartaoDadosProcessamentoResponse.setHarResultadoProc(hist.getAttribute(Columns.HAR_RESULTADO_PROC) != null ? hist.getAttribute(Columns.HAR_RESULTADO_PROC).toString() : null);
                    dashboardCartaoDadosProcessamentoResponse.setHarNomeArquivo(hist.getAttribute(Columns.HAR_NOME_ARQUIVO) != null ? hist.getAttribute(Columns.HAR_NOME_ARQUIVO).toString() : null);
                    dashboardCartaoResponse.dadosProcessamento.add(dashboardCartaoDadosProcessamentoResponse);
                }
            }

            return Response.status(Response.Status.OK).entity(dashboardCartaoResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema",  (AcessoSistema) securityContext.getUserPrincipal());
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }
}
