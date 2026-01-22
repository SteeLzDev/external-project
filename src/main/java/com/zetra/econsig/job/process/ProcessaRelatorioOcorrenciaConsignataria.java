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
 * <p> Title: ProcessaRelatorioOcorrenciaConsignataria</p>
 * <p> Description: Classe para processamento do relatório de ocorrência de consignatária</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioOcorrenciaConsignataria extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioOcorrenciaConsignataria.class);

    public ProcessaRelatorioOcorrenciaConsignataria(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        String opLogin = "";
        List<String> tocCodigos = new ArrayList<>();
        List<String> tpeCodigos = new ArrayList<>();

        if (parameterMap.containsKey("periodoIni")&& parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }
        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);

        // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_margem_dataHora
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.ocorrencia.csa", responsavel), responsavel, parameterMap, null);

        StringBuilder titulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.ocorrencia.consignatarias.titulo", responsavel)).append(" - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo));
        StringBuilder subtitulo = new StringBuilder("");

        if (parameterMap.containsKey("csaCodigo")) {
            String values[] = (parameterMap.get("csaCodigo"));
            if (values.length == 0 || values[0].equals("")) {
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                String csaCodigo = values[0];
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));
                criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            }
        }

        // login responsável
        opLogin = getParametro("OP_LOGIN", parameterMap);
        if (!TextHelper.isNull(opLogin)) {
            subtitulo.append(System.getProperty("line.separator") ).append(ApplicationResourcesHelper.getMessage("rotulo.login.responsavel.arg0", responsavel, opLogin));
            criterio.setAttribute("OP_LOGIN", opLogin);
        }

        // tipo ocorrencia
        if (parameterMap.containsKey("tocCodigo")) {
            String tocs[] = (parameterMap.get("tocCodigo"));
            if (!tocs[0].equals("")) {
                String values[];
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ""));
                for (int i = 0; i < tocs.length; i++) {
                    values = tocs[i].split(";");
                    tocCodigos.add(values[0]);
                    if (i == (tocs.length - 1)) {
                        subtitulo.append(" ").append(values[1]);
                    } else {
                        subtitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                tocCodigos = CodedValues.TOC_CODIGOS_CONSIGNATARIA;
            }
            criterio.setAttribute(Columns.TOC_CODIGO, tocCodigos);
        }

        // tipo penalidade
        if (parameterMap.containsKey("TPE_CODIGO")) {
            String[] tpe = (parameterMap.get("TPE_CODIGO"));
            if (!tpe[0].equals("")) {
                String values[];
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.penalidade.arg0", responsavel, ""));
                for (int i = 0; i < tpe.length; i++) {
                    values = tpe[i].split(";");
                    tpeCodigos.add(values[0]);
                    if (i == (tpe.length - 1)) {
                        subtitulo.append(" ").append(values[1]);
                    } else {
                        subtitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.penalidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
            criterio.setAttribute("TPE_CODIGO", tpeCodigos);
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

        try {
            String reportName = null;
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