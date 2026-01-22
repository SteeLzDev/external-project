package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
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
public final class ProcessaRelatorioContratosBeneficios extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioContratosBeneficios.class);

    public ProcessaRelatorioContratosBeneficios(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

    }

    @Override
    protected void executar() {
        try {
            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.contratos.beneficios", responsavel), responsavel, parameterMap, null);
            String strFormato = getStrFormato();
            StringBuilder subTitulo = new StringBuilder();

            List<String> statusCodigo = new ArrayList<>();
            List<String> statusDescricao = new ArrayList<>();

            if  (parameterMap.containsKey("scbCodigo")) {
                String scbs[] = (parameterMap.get("scbCodigo"));
                if (!scbs[0].equals("")) {
                    List<String> aux = Arrays.asList(scbs);

                    aux.forEach(item-> {
                        statusCodigo.add(item.split(";")[0]);
                        statusDescricao.add(item.split(";")[1]);
                    });

                    String statusDescricaoStr = statusDescricao.toString().substring(1);
                    statusDescricaoStr = statusDescricaoStr.substring(0, statusDescricaoStr.length() - 1);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.contrato.beneficio.situacao.contrato.filtro", responsavel, statusDescricaoStr.toUpperCase()));
                }


            }

            // Pega as datas corretamente
            Map<String, String> datas = getFiltroPeriodo(parameterMap, subTitulo, null, session, responsavel);
            String dataIni = datas.get("PERIODO_INICIAL");
            String dataFim = datas.get("PERIODO_FINAL");

            criterio.setAttribute("dataIni", dataIni);
            criterio.setAttribute("dataFim", dataFim);
            criterio.setAttribute("status", statusCodigo);

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
