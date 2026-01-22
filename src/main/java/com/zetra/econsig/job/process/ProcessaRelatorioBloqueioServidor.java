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
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioBloqueioServidorInfo;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaBloqueioServidor</p>
 * <p>Description: Classe para processamento de relatorio de bloqueio servidor
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioBloqueioServidor extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioBloqueioServidor.class);

    public ProcessaRelatorioBloqueioServidor(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String paramIniPeriodo = "";
        String paramFimPeriodo = "";

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
            paramIniPeriodo = reformat(getParametro("periodoIni", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(getParametro("periodoFim", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        String titulo = relatorio.getTitulo();
        StringBuilder subtitulo = new StringBuilder();

        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.bloqueio.servidor", responsavel), responsavel, parameterMap, null);
        titulo = titulo.replaceAll("Servidores", com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.servidor.plural", responsavel)).toLowerCase();

        List<String> orgCodigos = null;
        String csaCodigo = responsavel.getCsaCodigo();

        List<String> svcCodigos = new ArrayList<>();

        if ((!responsavel.isOrg() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)))) {
            List<String> orgNames = null;
            if (parameterMap.containsKey("orgCodigo")) {
                String[] org = parameterMap.get("orgCodigo");
                if (org[0].equals("")) {
                    subtitulo.append(System.lineSeparator()).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for (final String value : org) {
                            String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } else if(responsavel.isOrg()){
            orgCodigos = new ArrayList<>();
            orgCodigos.add(responsavel.getOrgCodigo());
        }
        criterio.setAttribute(Columns.CNV_ORG_CODIGO, orgCodigos);
        if (responsavel.isCseSupOrg()) {
            if (parameterMap.containsKey("csaCodigo")) {
                String values[] = (parameterMap.get("csaCodigo"));
                if (values.length == 0 || values[0].equals("")) {
                    subtitulo.append(System.lineSeparator());
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                } else {
                    values = values[0].split(";");
                    csaCodigo = values[0];
                    subtitulo.append(System.lineSeparator());
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.upper.arg0", responsavel, values[2]));
                }
            }
        }
        if (parameterMap.containsKey("svcCodigo")) {
            String svcs[] = (parameterMap.get("svcCodigo"));
            if (!svcs[0].equals("")) {
                String values[];
                subtitulo.append(System.lineSeparator());
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.upper.arg0", responsavel, ""));
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
                subtitulo.append(System.lineSeparator());
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        String reportName = null;
        try {
            List<String> campos = setaCampos();
            criterio.setAttribute("CAMPOS", campos);
            criterio.setAttribute("DATA_INI", paramIniPeriodo);
            criterio.setAttribute("DATA_FIM", paramFimPeriodo);
            criterio.setAttribute("ORG_CODIGO", orgCodigos);
            criterio.setAttribute("CSA_CODIGO", csaCodigo);
            criterio.setAttribute("SVC_CODIGO", svcCodigos);

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toUpperCase());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            RelatorioBloqueioServidorInfo bloqServidorReportInfo = new RelatorioBloqueioServidorInfo(relatorio);
            bloqServidorReportInfo.setCriterios(criterio);
            bloqServidorReportInfo.buildJRXML(parameters, responsavel);
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

    private List<String> setaCampos() throws ReportControllerException {
        String strFormato = getStrFormato();
        List<String> campos = new ArrayList<>();
        if (strFormato.equals("PDF") || strFormato.equals("XLS") || strFormato.equals("XLSX") || strFormato.equals("DOC")) {
            if (!responsavel.isOrg()) {
                campos.add(Columns.SER_NOME);
                campos.add(Columns.SER_CPF);
                campos.add(Columns.SRS_DESCRICAO);
                campos.add(Columns.ORG_NOME);
                campos.add(Columns.CSA_NOME);
                campos.add(Columns.CNV_COD_VERBA);
                campos.add(Columns.PCR_VLR);
                campos.add(Columns.PCR_OBS);
                campos.add(Columns.PCR_DATA_CADASTRO);
            } else {
                // Usuário de órgão não exibe o órgão nas colunas do relatório
                campos.add(Columns.SER_NOME);
                campos.add(Columns.SER_CPF);
                campos.add(Columns.SRS_DESCRICAO);
                campos.add(Columns.CSA_NOME);
                campos.add(Columns.CNV_COD_VERBA);
                campos.add(Columns.PCR_VLR);
                campos.add(Columns.PCR_OBS);
                campos.add(Columns.PCR_DATA_CADASTRO);
            }
        } else if (strFormato.equals("TEXT") || strFormato.equals("CSV")) {
            campos.add(Columns.SER_NOME);
            campos.add(Columns.SER_CPF);
            campos.add(Columns.SRS_DESCRICAO);
            campos.add(Columns.SVC_DESCRICAO);
            campos.add(Columns.ORG_NOME);
            campos.add(Columns.CSA_NOME);
            campos.add(Columns.CNV_COD_VERBA);
            campos.add(Columns.PCR_VLR);
            campos.add(Columns.PCR_OBS);
            campos.add(Columns.PCR_DATA_CADASTRO);
        } else {
            throw new ReportControllerException("mensagem.erro.interno.contate.administrador", responsavel);
        }
        return campos;
    }
}
