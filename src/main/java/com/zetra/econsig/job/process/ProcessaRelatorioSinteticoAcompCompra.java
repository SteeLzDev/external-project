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
 * <p> Title: ProcessaRelatorioSinteticoAcompCompra</p>
 * <p> Description: Classe para processamento de relatorio sintético de acompanhamento de compra.</p>
 * <p> Copyright: Copyright (c) 2010 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioSinteticoAcompCompra extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoAcompCompra.class);

    public ProcessaRelatorioSinteticoAcompCompra(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.acomp.compra", responsavel), responsavel, parameterMap, null);
        StringBuilder subtitulo = new StringBuilder();

        String dataInicio = "";
        String dataFim = "";
        String periodoInicio = "";
        String periodoFinal = "";
        List<String> orgCodigos = null;

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
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.compra.de.arg0.a.arg1", responsavel, periodoInicio, periodoFinal));
            subtitulo.append(System.getProperty("line.separator"));

        } else if (!TextHelper.isNull(periodoInicio)) {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.compra.a.partir.de.arg0", responsavel, periodoInicio));
            subtitulo.append(System.getProperty("line.separator"));

        } else if (!TextHelper.isNull(periodoFinal)) {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.compra.ate.arg0", responsavel, periodoFinal));
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

        // Órgão
        if (parameterMap.containsKey("orgCodigo")) {
            List<String> orgNames = null;
            String[] values = (parameterMap.get("orgCodigo"));
            if (values[0].equals("")) {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                orgCodigos = new ArrayList<>();
                orgNames = new ArrayList<>();
                try {
                    for (final String value : values) {
                        String[] separ = value.split(";");
                        orgCodigos.add(separ[0]);
                        orgNames.add(separ[2] + " ");
                    }
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            subtitulo.append(System.getProperty("line.separator"));
        }
        criterio.setAttribute(Columns.ORG_CODIGO, orgCodigos);

        // Serviço
        if  (parameterMap.containsKey("svcCodigo")) {
            String svcs[] = (parameterMap.get("svcCodigo"));
            if (!svcs[0].equals("")) {
                List<String> svcCodigos = new ArrayList<>();
                String values[];
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
                for (int i = 0; i < svcs.length; i++) {
                    values = svcs[i].split(";");
                    svcCodigos.add(values[0]);
                    if (i == (svcs.length - 1)) {
                        subtitulo.append(" ").append(values[2]);
                    } else {
                        subtitulo.append(" ").append(values[2]).append(",");
                    }
                }
                criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
            } else {
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
            subtitulo.append(System.getProperty("line.separator"));
        }

        // Pendência Vencida
        Boolean pendenciaVencida = Boolean.valueOf(getParametro("pendenciaVencida", parameterMap));
        criterio.setAttribute("EXIBIR_SOMENTE_VENCIDAS", pendenciaVencida);
        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.pendencias.somente.ja.vencidas.arg0", responsavel, ApplicationResourcesHelper.getMessage(pendenciaVencida ? "rotulo.sim" : "rotulo.nao", responsavel)));
        subtitulo.append(System.getProperty("line.separator"));

        String titulo = relatorio.getTitulo();
        String strFormato = getStrFormato();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_PERIODO_INICIO, periodoInicio);
        parameters.put(ReportManager.PARAM_NAME_PERIODO_FIM, periodoFinal);
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
