package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioSinteticoConsultaMargemPorUsuario</p>
 * <p> Description: Classe para processamento de relatorio sintético de consulta de margem por usuário.</p>
 * <p> Copyright: Copyright (c) 2019 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author: marlon.silva $
 * $Revision: $
 * $Date: 2019-06-12 10:38:00 -0300 (qua, 12 jun 2019) $
 */

public class ProcessaRelatorioSinteticoConsultaMargemPorUsuario extends ProcessaRelatorio {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoConsultaMargemPorUsuario.class);

    public ProcessaRelatorioSinteticoConsultaMargemPorUsuario(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.consulta.margem.usuario", responsavel), responsavel, parameterMap, null);
        StringBuilder subtitulo = new StringBuilder();

        String dataInicio = "";
        String dataFim = "";
        String periodoInicio = "";
        String periodoFinal = "";

        // Período
        if (parameterMap.containsKey("periodoIni") && !TextHelper.isNull(getParametro("periodoIni", parameterMap))) {
            dataInicio = reformat(getParametro("periodoIni", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            periodoInicio = reformat(dataInicio, "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDatePattern());
            criterio.setAttribute("DATA_INI", dataInicio);
        }
        if (parameterMap.containsKey("periodoFim") && !TextHelper.isNull(getParametro("periodoFim", parameterMap))) {
            dataFim = reformat(getParametro("periodoFim", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
            periodoFinal = reformat(dataFim, "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDatePattern());
            criterio.setAttribute("DATA_FIM", dataFim);
        }
        if (!TextHelper.isNull(periodoInicio) && !TextHelper.isNull(periodoFinal)) {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, periodoInicio, periodoFinal));
            subtitulo.append(System.getProperty("line.separator"));

        }

        // Consignatária
        if (parameterMap.containsKey("csaCodigo")) {
            String values[] = (parameterMap.get("csaCodigo"));
            if (values.length == 0 || values[0].equals("")) {
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                criterio.setAttribute(Columns.CSA_CODIGO, values[0]);
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));
            }
            subtitulo.append(System.getProperty("line.separator"));
        }

        // Correspondente
        List<String> corCodigos = new ArrayList<>();

        if (parameterMap.containsKey("corCodigo")) {
            String correspondentes[] = (parameterMap.get("corCodigo"));
            if(!correspondentes[0].equals("")) {
                if (correspondentes.length == 0 || correspondentes[0].substring(0, 2).equals("-1")) {
                    corCodigos.add("-1");
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                    for(String cor : correspondentes){
                        String [] correspondente = cor.split(";");
                        corCodigos.add(correspondente[0]);
                        subtitulo.append(correspondente[2]).append(", ");
                    }
                    subtitulo.deleteCharAt(subtitulo.length()-2);
                }
                subtitulo.append(System.getProperty("line.separator"));
                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            }
        }

        String titulo = relatorio.getTitulo();
        String strFormato = getStrFormato();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

        String reportName = null;
        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

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
}