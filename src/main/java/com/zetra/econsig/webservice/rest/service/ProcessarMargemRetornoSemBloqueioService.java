package com.zetra.econsig.webservice.rest.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.folha.BlocoProcessamentoController;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.request.ProcessamentoMargemRetornoRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: ProcessarMargemRetornoSemBloqueioService</p>
 * <p>Description: Retornar processamento de margem e retorno sem bloqueio.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
@Path("/processarMargemRetorno")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ProcessarMargemRetornoSemBloqueioService extends RestService {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarMargemRetornoSemBloqueioService.class);

    @POST
    @Path("/calcular")
    public Response calcularPeriodo(@Context HttpServletRequest request, ProcessamentoMargemRetornoRestRequest dados) {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, responsavel);
        if (TextHelper.isNull(ipsAcessoLiberado)) {
            ipsAcessoLiberado = "127.0.0.1";
        }

        if (!JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
            return Response.status(Response.Status.FORBIDDEN).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        // 1.1.1) Quando informado código do estabelecimento, deve retornar o progresso do processamento de margem e retorno sem bloqueio do Estabelecimento que possua o código informado.
        // 1.1.2) Quando informado o código do órgão, deve retornar o progresso do processamento de margem e retorno sem bloqueio do Órgão que possuam o código informado.
        // 1.1.3) Quando não informado nenhum código, deve retornar o progresso do processamento de margem e retorno sem bloqueio da CSE.
        try {
            BigDecimal percentual = new BigDecimal(0.00);
            int totalBlocosRetorno = 0;
            int totalBlocosMargem = 0;
            int totalBlocosComSucessoRetorno = 0;
            int totalBlocosComErroRetorno = 0;
            int totalBlocosComSucessoMargem = 0;
            int totalBlocosComErroMargem = 0;
            double percentualBlocosProcessados = 0.00;
            String bprPeriodo = "";

            String tipoEntidade = !TextHelper.isNull(dados.estCodigo) ? AcessoSistema.ENTIDADE_EST : (!TextHelper.isNull(dados.orgCodigo) ? AcessoSistema.ENTIDADE_ORG : null);
            String codigoEntidade = !TextHelper.isNull(dados.estCodigo) ? dados.estCodigo : (!TextHelper.isNull(dados.orgCodigo) ? dados.orgCodigo : null);

            BlocoProcessamentoController blocoProcessamentoController = ApplicationContextProvider.getApplicationContext().getBean(BlocoProcessamentoController.class);
            ProcessarFolhaController processarFolhaController = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);

            List<TransferObject> blocosProcessamento = blocoProcessamentoController.listarBlocosProcessamentoDashboard(null, null, null, tipoEntidade, codigoEntidade, responsavel);

            if (blocosProcessamento == null || blocosProcessamento.isEmpty()) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.bloco.processamento.encontrado", responsavel);

                return Response.status(Response.Status.NOT_FOUND).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").entity(responseError).build();
            }

            for (TransferObject blocos : blocosProcessamento) {
                if (TextHelper.isNull(bprPeriodo)) {
                    bprPeriodo = blocos.getAttribute(Columns.BPR_PERIODO).toString();
                } else if (!bprPeriodo.equals(blocos.getAttribute(Columns.BPR_PERIODO).toString())) {
                    ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.multiplos.processamento.execucao", responsavel);

                    return Response.status(Response.Status.CONFLICT).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").entity(responseError).build();
                }

                if (!blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.CANCELADO.getCodigo())) {
                    if (blocos.getAttribute(Columns.TBP_CODIGO).equals(TipoBlocoProcessamentoEnum.RETORNO.getCodigo())) {
                        // soma total blocos de retorno
                        totalBlocosRetorno += Integer.parseInt(blocos.getAttribute("TOTAL").toString());
                        if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo())) {
                            // total de blocos de retorno processados com sucesso
                            totalBlocosComSucessoRetorno += Integer.parseInt(blocos.getAttribute("TOTAL").toString());
                        }
                        if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo())) {
                            // total de blocos de retorno processados com erro
                            totalBlocosComErroRetorno += Integer.parseInt(blocos.getAttribute("TOTAL").toString());
                        }
                    } else if (blocos.getAttribute(Columns.TBP_CODIGO).equals(TipoBlocoProcessamentoEnum.MARGEM.getCodigo())) {
                        // soma total blocos de margem
                        totalBlocosMargem += Integer.parseInt(blocos.getAttribute("TOTAL").toString());
                        if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo())) {
                            // total de blocos de margem processados com sucesso
                            totalBlocosComSucessoMargem += Integer.parseInt(blocos.getAttribute("TOTAL").toString());
                        }
                        if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo())) {
                            // total de blocos de margem processados com erro
                            totalBlocosComErroMargem += Integer.parseInt(blocos.getAttribute("TOTAL").toString());
                        }
                    }
                }
            }

            // total de blocos de processamento
            int totalBlocos = totalBlocosRetorno + totalBlocosMargem;
            // total de blocos processados
            int totalBlocosProcessados =  totalBlocosComSucessoMargem + totalBlocosComErroMargem + totalBlocosComSucessoRetorno + totalBlocosComErroRetorno;

            // recupera o início do processamento dos blocos
            List<String> sbpCodigos = Arrays.asList(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo(), StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo());
            Date dataInicioProcessamento = blocoProcessamentoController.obterInicioProcessamento(sbpCodigos, responsavel);

            Date dataFimProcessamento = null;
            List<String> orgCodigos = !TextHelper.isNull(dados.orgCodigo) ? Arrays.asList(dados.orgCodigo) : null;
            List<String> estCodigos = !TextHelper.isNull(dados.estCodigo) ? Arrays.asList(dados.estCodigo) : null;
            List<TransferObject> lstHistProcessamento = processarFolhaController.listarHistoricoProcessamento(DateHelper.parse(bprPeriodo, "yyyy-MM-dd"), orgCodigos, estCodigos, responsavel);
            for (TransferObject histProcessamento : lstHistProcessamento) {
                dataFimProcessamento = !TextHelper.isNull(histProcessamento.getAttribute(Columns.HPR_DATA_FIM)) ? DateHelper.parse(histProcessamento.getAttribute(Columns.HPR_DATA_FIM).toString(), "yyyy-MM-dd HH:mm:ss") : null;
            }

            // calcular percentual de blocos processados de margem e retorno
            if (totalBlocos > 0 && totalBlocosProcessados > 0) {
                percentual = calcularPercentualBlocos(totalBlocosProcessados, totalBlocos);
                percentualBlocosProcessados = percentual.doubleValue();
            }

            // 1.2) O webservice deve retornar os seguintes parâmetros sempre que houverem valor a ser retornado para eles:
            // período (obter valor da tb_historico_processamento.HPR_PERIODO)
            // porcentagem da folha que foi processada (obter valor conforme consulta usada no dashboard)
            // quantidade de blocos finalizados com sucesso até o momento (obter valor conforme consulta usada no dashboard)
            // quantidade de blocos com erro até o momento (obter valor conforme consulta usada no dashboard)
            // data/hora início do processo de preparação (obter valor da tb_historico_processamento.HPR_DATA_INI)
            // data/hora fim do processo de conclusão (obter varlo da tb_historico_processamento.HPR_DATA_FIM)
            TransferObject retorno = new CustomTransferObject();
            retorno.setAttribute("periodo", DateHelper.format(dataInicioProcessamento, LocaleHelper.getDatePattern()));
            retorno.setAttribute("percentualBlocosProcessados", percentualBlocosProcessados);
            retorno.setAttribute("totalBlocosComSucessoMargem", totalBlocosComSucessoMargem);
            retorno.setAttribute("totalBlocosComErroMargem", totalBlocosComErroMargem);
            retorno.setAttribute("totalBlocosComSucessoRetorno", totalBlocosComSucessoRetorno);
            retorno.setAttribute("totalBlocosComErroRetorno", totalBlocosComErroRetorno);
            retorno.setAttribute("dataInicioProcessamento", DateHelper.format(dataInicioProcessamento, LocaleHelper.getDateTimePattern()));
            retorno.setAttribute("dataFimProcessamento", !TextHelper.isNull(dataFimProcessamento) ? DateHelper.format(dataFimProcessamento, LocaleHelper.getDateTimePattern()) : null);

            return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").entity(retorno.getAtributos()).build();

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").entity(responseError).build();
        }
    }

    private BigDecimal calcularPercentualBlocos(int qtdeParcial, int qtdeTotal) {
        BigDecimal variacaoMargem = new BigDecimal(0.00);
        if (qtdeParcial > 0 && qtdeTotal > 0 ) {
            variacaoMargem = new BigDecimal(qtdeParcial).multiply(new BigDecimal(100)).divide(new BigDecimal(qtdeTotal),4,java.math.RoundingMode.HALF_UP).setScale(2,java.math.RoundingMode.HALF_UP);
        }
        return variacaoMargem.abs();
    }
}

