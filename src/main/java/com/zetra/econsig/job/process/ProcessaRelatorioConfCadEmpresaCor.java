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
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioConfCadEmpresaCor</p>
 * <p> Description: Classe para processamento do relatório de Conferência de Cadastro de Empresa Correspondente.</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioConfCadEmpresaCor extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConfCadEmpresaCor.class);

    public ProcessaRelatorioConfCadEmpresaCor(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado,AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String reportName = null;
        try {

            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.conf.cad.emp.cor", responsavel), responsavel, parameterMap, null);
            StringBuilder subTitulo = new StringBuilder("");

            String ecoCodigo = "";
            if (parameterMap.containsKey("ecoCodigo")) {
                String[] values = parameterMap.get("ecoCodigo");
                if (values.length == 0 || values[0].equals("")) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.empresacorrespondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    values = TextHelper.split(values[0], ";");
                    ecoCodigo = values[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.empresacorrespondente.singular.arg0", responsavel, values[2]));
                }
            }
            String csaCodigo = "";
            if (parameterMap.containsKey("csaCodigo")) {
                String[] values = parameterMap.get("csaCodigo");
                if (values.length == 0 || values[0].equals("")) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                } else {
                    values = TextHelper.split(values[0], ";");
                    csaCodigo = values[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, (values[2])));
                }
            }

            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.ECO_CODIGO, ecoCodigo);

            String titulo = relatorio.getTitulo();
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toUpperCase());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put("RESPONSAVEL", responsavel);

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

