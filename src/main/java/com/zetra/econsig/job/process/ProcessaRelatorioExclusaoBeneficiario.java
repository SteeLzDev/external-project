package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioConsignacao</p>
 * <p>Description: Classe para processamento de relatorio de Exclusao de Beneficiário por Período
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioExclusaoBeneficiario extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioExclusaoBeneficiario.class);

    public ProcessaRelatorioExclusaoBeneficiario(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

    }

    @Override
    protected void executar() {
        try {
            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.exclusao.beneficiario", responsavel), responsavel, parameterMap, null);
            String strFormato = getStrFormato();
            StringBuilder subTitulo = new StringBuilder();

            String strBeneficio = getParametro("BEN_CODIGO", parameterMap);
            String strOperadora = getParametro("csaCodigoOperadora", parameterMap);
            String strTmo = getParametro("tmoCodigo", parameterMap);

            String operadora = null;

            String motivoOperacao = null;

            if (!TextHelper.isNull(strOperadora)) {
                operadora = strOperadora.split(";")[0];
            }

            if (!TextHelper.isNull(strTmo)) {
                motivoOperacao = strTmo.split(";")[0];
            }
            // Pega as datas corretamente
            Map<String, String> datas = getFiltroPeriodo(parameterMap, subTitulo, null, session, responsavel);
            String dataIni = datas.get("PERIODO_INICIAL");
            String dataFim = datas.get("PERIODO_FINAL");

            // Pegar dos campos os valores setados pelo usuario
            // Código da operadora
            if (parameterMap.containsKey("csaCodigoOperadora")) {
                String aux = getParametro("csaCodigoOperadora", parameterMap);
                if (!TextHelper.isNull(aux)) {
                    String helper[] = aux.split(";");
                    strOperadora = helper[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.operadora.arg0", responsavel, helper[2].toUpperCase()));
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.operadora.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                }
            }

            // Código do benefício
            if (parameterMap.containsKey("BEN_CODIGO")) {
                String aux = getParametro("BEN_CODIGO", parameterMap);
                if (!TextHelper.isNull(aux)) {
                    String helper[] = aux.split(";");
                    strBeneficio = helper[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.arg0", responsavel, helper[1].toUpperCase()));
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                }
            }

            // Código do benefício
            if (parameterMap.containsKey("tmoCodigo")) {
                String aux = getParametro("tmoCodigo", parameterMap);
                if (!TextHelper.isNull(aux)) {
                    String helper[] = aux.split(";");
                    strTmo = helper[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, helper[1].toUpperCase()));
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                }
            }

            criterio.setAttribute("dataIni", dataIni);
            criterio.setAttribute("dataFim", dataFim);
            criterio.setAttribute("beneficio", strBeneficio);
            criterio.setAttribute("operadora", operadora);
            criterio.setAttribute("motivoOperacao", motivoOperacao);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo().toUpperCase());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            geraZip(nome.toString(), reportName);

        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
