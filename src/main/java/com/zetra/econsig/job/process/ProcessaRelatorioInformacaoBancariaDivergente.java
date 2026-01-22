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
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioInformacaoBancariaDivergente</p>
 * <p> Description: Classe para processamento de relatorios de contratos com informação bancária divergente..</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioInformacaoBancariaDivergente extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioInformacaoBancariaDivergente.class);

    public ProcessaRelatorioInformacaoBancariaDivergente(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        if (!parameterMap.containsKey("periodoIni") && !parameterMap.containsKey("periodoFim")) {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
        } else {

            String strIniPeriodo = getParametro("periodoIni", parameterMap);
            String strFimPeriodo = getParametro("periodoFim", parameterMap);

            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.inf.banc.divergente", responsavel), responsavel, parameterMap, null);

            String titulo = relatorio.getTitulo();
            StringBuilder subtitulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo).toUpperCase());

            String periodoIni = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            String periodoFim = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");

            criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO, periodoIni);
            criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_FIM, periodoFim);

            if (!TextHelper.isNull(parameterMap.get("csaCodigo")[0])) {
                String[] csa = TextHelper.split(parameterMap.get("csaCodigo")[0], ";");
                criterio.setAttribute(Columns.CNV_CSA_CODIGO, csa[0]);
                subtitulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.sigla.arg0", responsavel, csa[2]));
            }

            List<String> orgCodigos = null;
            List<String> orgNames = null;
            if (parameterMap.containsKey("orgCodigo")) {
                String[] org = parameterMap.get("orgCodigo");
                if (org[0].equals("")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for (final String value : org) {
                            String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
            criterio.setAttribute(Columns.CNV_ORG_CODIGO, orgCodigos);
            if (!TextHelper.isNull(parameterMap.get("svcCodigo")[0])) {
                String svcs[] = parameterMap.get("svcCodigo");
                List<String> svcCodigos = new ArrayList<>();
                String values[];
                subtitulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.servico.abreviado.upper.arg0", responsavel, ""));
                for (String svc : svcs) {
                    values = svc.split(";");
                    svcCodigos.add(values[0]);
                    subtitulo.append(values[2]).append(",");
                }
                subtitulo.deleteCharAt(subtitulo.length()-1);
                criterio.setAttribute(Columns.CNV_SVC_CODIGO, svcCodigos);
            }

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put("RESPONSAVEL", responsavel);

            String reportName = null;
            try {
                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

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
}
