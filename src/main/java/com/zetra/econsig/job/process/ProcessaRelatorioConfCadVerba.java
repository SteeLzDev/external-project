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
 * <p>Title: ProcessaConsignacao</p>
 * <p>Description: Classe para processamento de relatorio de Consignacao
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioConfCadVerba extends ProcessaRelatorio {
    /**
     * Log object for this class.
     */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConfCadVerba.class);

    public ProcessaRelatorioConfCadVerba(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        String titulo = relatorio.getTitulo();
        StringBuilder subtitulo = new StringBuilder();

        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.conf.cad.verba", responsavel), responsavel, parameterMap, null);

        String csaCodigo = null;
        List<String> orgNames = null;
        List<String> orgCodigos = null;
        List<String> svcCodigos = new ArrayList<>();
        List<String> scvCodigos = new ArrayList<>();

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
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } else if (responsavel.isOrg()) {
            orgCodigos = new ArrayList<>();
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, responsavel.getNomeEntidade()));
            orgCodigos.add(responsavel.getOrgCodigo());
        }

		if (responsavel.isCsa()) {
			csaCodigo = responsavel.getCsaCodigo();
		} else {
	        if (parameterMap.containsKey("csaCodigo")) {
	            String values[] = (parameterMap.get("csaCodigo"));
	            if (values.length == 0 || values[0].equals("")) {
	                subtitulo.append(System.getProperty("line.separator") + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
	            } else {
	                values = values[0].split(";");
	                csaCodigo = values[0];
	                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[1] + " - " + values[2]));
	            }
	            subtitulo.append("\n");
	        }
        }
        if  (parameterMap.containsKey("svcCodigo")) {
            String svcs[] = (parameterMap.get("svcCodigo"));
            if (!svcs[0].equals("")) {
                String values[];
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
                for (int i = 0; i < svcs.length; i++) {
                    values = svcs[i].split(";");
                    svcCodigos.add(values[0]);
                    if (i == (svcs.length - 1)) {
                        subtitulo.append(" ").append(values[2]);
                    } else {
                        subtitulo.append(" ").append(values[2]).append(",");
                    }
                }
            } else {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
            subtitulo.append("\n");
        }
        if  (parameterMap.containsKey("scvCodigo")) {
            String scvs[] = (parameterMap.get("scvCodigo"));
            if (!scvs[0].equals("")) {
                scvCodigos = Arrays.asList(scvs);
            }
        }

        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("ORG_CODIGO", orgCodigos);
        criterio.setAttribute("SVC_CODIGO", svcCodigos);
        criterio.setAttribute("SCV_CODIGO", scvCodigos);

        String strFormato = getStrFormato();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

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
