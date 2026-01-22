package com.zetra.econsig.job.process;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioEstatistico</p>
 * <p> Description: Classe para processamento de relatorio estatístico</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioEstatistico extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioEstatistico.class);

    public ProcessaRelatorioEstatistico(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        if (!parameterMap.containsKey("periodo")) {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
        } else {
            Date dataInicio = null;
            Date dataFim = null;
            try {
                dataFim = DateHelper.parsePeriodString(getParametro("periodo", parameterMap));
            } catch (ParseException ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(ex.getMessage(), ex);
                return;
            }

            List<String> referencias = new ArrayList<>();
            String[] titulos = new String[6];

            try {
                boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);
                PeriodoDelegate perDelegate = new PeriodoDelegate();

                for (int i = 0; i < 6; i++) {
                    Date dataRef = null;
                    if (quinzenal) {
                        dataRef = DateHelper.toSQLDate(perDelegate.obtemPeriodoAposPrazo(null, -5 + i, dataFim, false, responsavel));
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dataFim);
                        cal.add(Calendar.MONTH, -5 + i);
                        dataRef = DateHelper.toSQLDate(cal.getTime());
                    }

                    referencias.add(DateHelper.format(dataRef, "yyyy-MM-dd"));
                    titulos[i] = DateHelper.toPeriodMesExtensoString(dataRef);

                    if (i == 0) {
                        dataInicio = dataRef;
                    }
                }
            } catch (PeriodoException ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(ex.getMessage(), ex);
                return;
            }

            StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.estatistico", responsavel), responsavel, parameterMap, null));
            StringBuilder subTitulo = new StringBuilder();

            String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
            List<String> orgCodigos = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
            List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
            List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);

            if (!TextHelper.isNull(csaCodigo)) {
                criterio.setAttribute(Columns.CNV_CSA_CODIGO, csaCodigo);
            }
            if (orgCodigos != null && !orgCodigos.isEmpty()) {
                criterio.setAttribute(Columns.CNV_ORG_CODIGO, orgCodigos);
            }
            if (svcCodigos != null && !svcCodigos.isEmpty()) {
                criterio.setAttribute(Columns.CNV_SVC_CODIGO, svcCodigos);
            }
            if (srsCodigos != null && !srsCodigos.isEmpty()) {
                criterio.setAttribute(Columns.RSE_SRS_CODIGO, srsCodigos);
            }

            // Definição dos parâmetros definidos no arquivo iReports (.jasper)
            String periodoInicio = DateHelper.toPeriodString(dataInicio);
            String periodoFinal = DateHelper.toPeriodString(dataFim);

            String titulo = relatorio.getTitulo();
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_PERIODO_INICIO, periodoInicio);
            parameters.put(ReportManager.PARAM_NAME_PERIODO_FIM, periodoFinal);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());

            parameters.put("TABLE_NAME", "" + Calendar.getInstance().getTimeInMillis());
            parameters.put("REFERENCIAS", referencias);
            parameters.put("TITULO_1", titulos[0]);
            parameters.put("TITULO_2", titulos[1]);
            parameters.put("TITULO_3", titulos[2]);
            parameters.put("TITULO_4", titulos[3]);
            parameters.put("TITULO_5", titulos[4]);
            parameters.put("TITULO_6", titulos[5]);
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            String reportName = null;
            try {
                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

                String reportNameZip = geraZip(nome.toString(), reportName);

                // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                enviaEmail(reportNameZip);

            } catch (Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }

        }
    }
}
