package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.PercentualCarteiraBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioPercentualCarteira</p>
 * <p> Description: Classe para processamento de relatorio de percentual de carteira</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioPercentualCarteira extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioPercentualCarteira.class);

    public ProcessaRelatorioPercentualCarteira(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        String dataIni = null;
        String dataFim = null;
        String paramIniPeriodo = null;
        String paramFimPeriodo = null;
        List<String> orgCodigos = null;
        List<String> orgNames = null;
        List<String> svcCodigos = null;
        List<String> origensAdes = null;
        BigDecimal sumQtde = new BigDecimal(0.00);
        BigDecimal sumParcela = new BigDecimal(0.00);
        BigDecimal sumTotal = new BigDecimal(0.00);

        // Data de Inclusão
        if (parameterMap.get("chkTodos") == null || !getParametro("chkTodos", parameterMap).equals("1")) {
            dataIni = getParametro("periodoIni", parameterMap);
            dataFim = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = reformat(getParametro("periodoIni", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(getParametro("periodoFim", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        }

        String titulo = relatorio.getTitulo();

        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.percentual.carteira", responsavel), responsavel, parameterMap, null);
        StringBuilder subtitulo = new StringBuilder();

        // Período
        if (dataIni != null && dataFim != null) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, dataIni, dataFim));
        }

        // Servicos
        svcCodigos = new ArrayList<>();
        if (!TextHelper.isNull(parameterMap.get("svcCodigo")[0])) {
            String svcs[] = parameterMap.get("svcCodigo");
            String values[];
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
            for (String svc : svcs) {
                values = svc.split(";");
                svcCodigos.add(values[0]);
                subtitulo.append(" ").append(values[2]).append(",");
            }
            subtitulo.deleteCharAt(subtitulo.length() - 1);
        }

        // Orgao
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
        }

        // Origem contrato
        String [] origemAde = parameterMap.get("chkOrigem");
        origensAdes = origemAde != null ? Arrays.asList(origemAde) : null;
        if (origensAdes != null && !origensAdes.isEmpty()) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.origem.contrato.arg0", responsavel, ""));
            for (String origem : origemAde) {
                if (origem.equals(CodedValues.ORIGEM_ADE_NOVA)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel).toUpperCase());
                } else if (origem.equals(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel).toUpperCase());
                } else if (origem.equals(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel).toUpperCase());
                }
                subtitulo.append(",");
            }
            subtitulo.deleteCharAt(subtitulo.length() - 1);
        }

        String reportName = null;
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            // Faz o somatorio para depois realizar o percentual
            List<TransferObject> consignatarias = relatorioController.lstPercentualCarteira(paramIniPeriodo, paramFimPeriodo, svcCodigos, orgCodigos, origensAdes, "TODOS", responsavel);
            for (TransferObject to : consignatarias) {
                if (to.getAttribute("PERC_QTDE") != null) {
                    sumQtde = sumQtde.add((BigDecimal) to.getAttribute("PERC_QTDE"));
                }
                if (to.getAttribute("PERC_PARCELA") != null) {
                    sumParcela = sumParcela.add((BigDecimal) to.getAttribute("PERC_PARCELA"));
                }
                if (to.getAttribute("PERC_TOTAL") != null) {
                    sumTotal = sumTotal.add((BigDecimal) to.getAttribute("PERC_TOTAL"));
                }
            }

            String strFormato = getStrFormato();
            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true, session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
            parameters.put("SUM_QTDE", sumQtde);
            parameters.put("SUM_PARCELA", sumParcela);
            parameters.put("SUM_TOTAL", sumTotal);

            List<TransferObject> lista1 = relatorioController.lstPercentualCarteira(paramIniPeriodo, paramFimPeriodo, svcCodigos, orgCodigos, origensAdes, "QTDE", responsavel);
            parameters.put("PERC_CARTEIRA_QTDE", recuperaPercentualCarteiraBean(lista1));

            List<TransferObject> lista2 = relatorioController.lstPercentualCarteira(paramIniPeriodo, paramFimPeriodo, svcCodigos, orgCodigos, origensAdes, "PARCELA", responsavel);
            parameters.put("PERC_CARTEIRA_PARCELA", recuperaPercentualCarteiraBean(lista2));

            List<TransferObject> lista3 = relatorioController.lstPercentualCarteira(paramIniPeriodo, paramFimPeriodo, svcCodigos, orgCodigos, origensAdes, "TOTAL", responsavel);
            parameters.put("PERC_CARTEIRA_TOTAL", recuperaPercentualCarteiraBean(lista3));

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

    public List<PercentualCarteiraBean> recuperaPercentualCarteiraBean(List<TransferObject> lista) {
        List<PercentualCarteiraBean> retorno = new ArrayList<>();

        if (lista != null) {
            Iterator<TransferObject> ite = lista.iterator();
            while (ite.hasNext()) {
                TransferObject to = ite.next();
                PercentualCarteiraBean bean = new PercentualCarteiraBean();
                if (!TextHelper.isNull(to.getAttribute(Columns.CSA_CODIGO))) {
                    bean.setCsaCodigo(to.getAttribute(Columns.CSA_CODIGO).toString());
                }
                if (!TextHelper.isNull(to.getAttribute(Columns.CSA_IDENTIFICADOR))) {
                    bean.setCsaIdentificador(to.getAttribute(Columns.CSA_IDENTIFICADOR).toString());
                }
                if (!TextHelper.isNull(to.getAttribute("CSA_ID"))) {
                    bean.setCsaId(to.getAttribute("CSA_ID").toString());
                }
                if (!TextHelper.isNull(to.getAttribute("CSA"))) {
                    bean.setCsa(to.getAttribute("CSA").toString());
                }
                try {
                    bean.setPercQtde(new BigDecimal(to.getAttribute("PERC_QTDE").toString()));
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                }
                try {
                    bean.setPercParcela(new BigDecimal(to.getAttribute("PERC_PARCELA").toString()));
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                }
                try {
                    bean.setPercTotal(new BigDecimal(to.getAttribute("PERC_TOTAL").toString()));
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                }

                retorno.add(bean);
            }
        }

        return retorno;
    }
}
