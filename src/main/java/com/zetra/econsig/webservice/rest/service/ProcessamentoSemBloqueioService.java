package com.zetra.econsig.webservice.rest.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.folha.DadosProcessamentoSemBloqueio;
import com.zetra.econsig.helper.folha.RecuperaDadosProcessamentoSemBloqueioViewHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: ProcessamentoSemBloqueioService</p>
 * <p>Description: Serviço de API a dados sobre o processamento sem bloqueio no sistema, se houver.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/processoSemBloqueio")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ProcessamentoSemBloqueioService extends RestService {

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/dadosProcessamento")
    @Consumes("application/json")
    @Produces("application/json")
    public Response dadosProcessamento(@QueryParam("dashboard") String dashboardParam, @QueryParam("orgaoProcessamento") String orgaoIdentificadorProcessamento,@QueryParam("orgaoVariacaoMargem") String orgaoIdentificadorVariacaoMargem) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.isSup()) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", null);
            return Response.status(Response.Status.FORBIDDEN).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        ProcessarFolhaController processarFolhaController = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);

        try {
            List<TransferObject> listHistProc = processarFolhaController.listarHistoricoProcessamento(null, null, null, 0, 1, true, responsavel);

            boolean processaSemBloqueio = false;
            String maiorPeriodo = "";
            String statusProc = "";

            if (listHistProc != null && !listHistProc.isEmpty()) {
                processaSemBloqueio = true;

                TransferObject historicoMaisRecente = listHistProc.get(0);

                Date dataFimProc = (Date) historicoMaisRecente.getAttribute(Columns.HPR_DATA_FIM);
                maiorPeriodo = DateHelper.format((Date) historicoMaisRecente.getAttribute(Columns.HPR_PERIODO), LocaleHelper.getDatePattern());
                statusProc = (dataFimProc == null) ? "Em processamento" : "Processamento concluído";
            }

            DadosProcessamentoSemBloqueio dadosProcessamento = RecuperaDadosProcessamentoSemBloqueioViewHelper.getDadosProcessamentoBloco(orgaoIdentificadorProcessamento, orgaoIdentificadorVariacaoMargem, responsavel);

            Map<String, Object> responseMap = new HashMap<>();

            if (TextHelper.isNull(orgaoIdentificadorProcessamento) && TextHelper.isNull(orgaoIdentificadorVariacaoMargem) && dadosProcessamento.getLstOrgao() != null && !dadosProcessamento.getLstOrgao().isEmpty()) {
                List<String> filter = Arrays.asList("org_codigo", "org_identificador", "org_nome");
                responseMap.put("lstOrgaos",dadosProcessamento.getLstOrgao().stream().map(org -> transformTO(org, filter)).collect(Collectors.toList()));
            }
            responseMap.put("processaSemBloqueio", processaSemBloqueio);
            responseMap.put("statusProc", statusProc);
            responseMap.put("maiorPeriodo", maiorPeriodo);
            responseMap.put("percentualBlocosProcessados", dadosProcessamento.getPercentualBlocosProcessados());
            responseMap.put("temProcessoRodando", dadosProcessamento.isTemProcessoRodando());

            if (!TextHelper.isNull(dashboardParam) && dashboardParam.equals("true")) {
                responseMap.put("percentualBlocosProcessadosComErro", dadosProcessamento.getPercentualBlocosProcessadosComErro());
                responseMap.put("percentualBlocosProcessadosRejeitados", dadosProcessamento.getPercentualBlocosProcessadosRejeitados());

                responseMap.put("dadosMediaMargem", dadosProcessamento.getDadosMediaMargem());
                responseMap.put("estimativaTermino", String.format("%02d", dadosProcessamento.getEstimativaTerminoHoras()) + ":" + String.format("%02d", dadosProcessamento.getEstimativaTerminoMinutos()));

                responseMap.put("percentualBlocosProcessadosMargem", dadosProcessamento.getPercentualBlocosProcessadosMargem());
                responseMap.put("percentualBlocosProcessadosRetorno", dadosProcessamento.getPercentualBlocosProcessadosRetorno());
            }

            return Response.status(Response.Status.OK).entity(responseMap).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (ViewHelperException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.OK).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (ZetraException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    }
}
