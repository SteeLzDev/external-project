package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioIncBeneficiariosPorPeriodo</p>
 * <p>Description: Classe para processamento de relatorio de beneficiários por Período</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioIncBeneficiariosPorPeriodo extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioTaxasEfetivas.class);

    public ProcessaRelatorioIncBeneficiariosPorPeriodo(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        StringBuilder subTitulo = new StringBuilder();
        String benCodigo = null;
        String csaCodigo = null;

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
                csaCodigo = helper[0];
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
                benCodigo = helper[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.arg0", responsavel, helper[1].toUpperCase()));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        StringBuilder titulo = new StringBuilder(relatorio.getTitulo().toUpperCase());
        String formatoArquivo = getStrFormato();
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.inc.beneficiarios.periodo", responsavel), responsavel, parameterMap, null);

        criterio.setAttribute("DATA_INI", dataIni);
        criterio.setAttribute("DATA_FIM", dataFim);
        criterio.setAttribute("BEN_CODIGO", benCodigo);
        criterio.setAttribute("CSA_CODIGO", csaCodigo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formatoArquivo);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);

        String reportName = null;

        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);
            geraZip(nome.toString(), reportName);
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
