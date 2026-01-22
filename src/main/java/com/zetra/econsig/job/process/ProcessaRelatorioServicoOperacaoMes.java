package com.zetra.econsig.job.process;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.ServicoOperacaoMesBean;
import com.zetra.econsig.report.reports.HeadingsScriptlet;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioServicoOperacaoMes</p>
 * <p>Description: Classe para processamento de relatorio de descontos
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioServicoOperacaoMes extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioServicoOperacaoMes.class);

    public ProcessaRelatorioServicoOperacaoMes(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        StringBuilder subtitulo = new StringBuilder("");
        String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

        try {
            // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_dataHora
            StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.servico.operacao", responsavel), responsavel, parameterMap, null));

            Map<String, String> periodos = getFiltroPeriodo(parameterMap, subtitulo, nomeArquivo, session, responsavel);
            String periodo = periodos.get("PERIODO");
            String dataInicio = null, dataFim = null;

            Calendar cal = Calendar.getInstance();
            cal.setTime(DateHelper.parse(periodo, "yyyy-MM-dd"));

            dataInicio = DateHelper.reformat(periodo, "yyyy-MM-dd", "yyyy-MM-01 00:00:00");
            dataFim = DateHelper.reformat(periodo, "yyyy-MM-dd", "yyyy-MM-" + cal.getActualMaximum(Calendar.DATE) + " 23:59:59");

            Boolean operacaoPorCsa = false;

            if  (parameterMap.containsKey("operacaoPorConsignataria")) {
                operacaoPorCsa = Boolean.valueOf(getParametro("operacaoPorConsignataria", parameterMap));
            }

            // Seta parâmetros
            String titulo = relatorio.getTitulo();
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.REPORT_FILE_NAME, ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.servico.operacao", responsavel));
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ENTIDADE, getCaminhoLogoEntidade(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ECONSIG, getCaminhoLogoEConsig(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
            parameters.put(ReportManager.PARAM_CSE_NOME, getCseNome(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
            parameters.put(ReportManager.REPORT_SCRIPTLET, new HeadingsScriptlet());
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
            parameters.put("OPERACAO_POR_CSA", operacaoPorCsa);

            List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subtitulo, null, session, responsavel);
            List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subtitulo, null, session, responsavel);

            // gera dados do filtro principal
            geraOperacoesMes(periodo, dataInicio, dataFim, svcCodigos, null, false, parameters);
            if (operacaoPorCsa && nseCodigos != null && !nseCodigos.isEmpty()) {
                // gera dados analíticos para as naturezas selecionadas
                geraOperacoesMes(periodo, dataInicio, dataFim, svcCodigos, nseCodigos, true, parameters);
            }

            // Gera relatório
            String reportName = null;
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String strFormato = getStrFormato();
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nomeArquivo.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (ParseException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    private void geraOperacoesMes(String periodo, String dataIni, String dataFim, List<String> svcCodigo, List<String> nseCodigo, Boolean operacaoPorCsa, HashMap<String, Object> parameters) {
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            CustomTransferObject criterioSvcOperacaoMes = new CustomTransferObject();

            criterioSvcOperacaoMes.setAttribute("PERIODO", periodo);
            criterioSvcOperacaoMes.setAttribute("DATA_INI", dataIni);
            criterioSvcOperacaoMes.setAttribute("DATA_FIM", dataFim);
            criterioSvcOperacaoMes.setAttribute("SVC_CODIGO", svcCodigo);
            criterioSvcOperacaoMes.setAttribute("NSE_CODIGO", nseCodigo);
            criterioSvcOperacaoMes.setAttribute("OPERACAO_POR_CSA", operacaoPorCsa);
            criterioSvcOperacaoMes.setAttribute("OPERACAO_POR_CSA_CARTAO", true);
            criterioSvcOperacaoMes.setAttribute("OPERACAO_POR_CSA_EMPRESTIMO", true);

            List<ServicoOperacaoMesBean> listaOperacaoMes = relatorioController.lstOperacaoMes(criterioSvcOperacaoMes, responsavel);

            if (nseCodigo != null && !nseCodigo.isEmpty()) {
                parameters.put("LISTA_SERVICO_OPERACAO_MES_POR_CSA", listaOperacaoMes);
            } else {
                parameters.put("LISTA_SERVICO_OPERACAO_MES", listaOperacaoMes);
            }

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }
}
