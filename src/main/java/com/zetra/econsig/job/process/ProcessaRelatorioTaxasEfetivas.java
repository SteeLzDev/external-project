package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.coeficiente.CoeficienteController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioTaxasEfetivas</p>
 * <p>Description: Classe para processamento de relatorio de taxas efetivas.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioTaxasEfetivas extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioTaxasEfetivas.class);

    private final Map<String, BigDecimal> taxasMinimas;
    private final Map<String, BigDecimal> taxasMaximas;
    private final Map<String, BigDecimal> taxasMedias;

    public ProcessaRelatorioTaxasEfetivas(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        taxasMinimas = new HashMap<>();
        taxasMaximas = new HashMap<>();
        taxasMedias = new HashMap<>();

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        StringBuilder subtitulo = new StringBuilder();
        StringBuilder titulo = new StringBuilder(relatorio.getTitulo().toUpperCase());
        String prazosInformados = "";
        List<Integer> prazosInformadosList = new ArrayList<>();
        boolean prazoMultiploDoze = false;

        // periodo
        String periodo = null;
        if (parameterMap.containsKey("periodo")) {
            periodo = getParametro("periodo", parameterMap);
            titulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.mes.competencia.upper.arg0", responsavel, periodo));
            periodo = formatarPeriodo(periodo);
        }
        if (TextHelper.isNull(periodo)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        // orgao
        List<String> orgCodigos = null;
        if (parameterMap.containsKey("orgCodigo")) {
            List<String> orgNames = null;
            String values[] = parameterMap.get("orgCodigo");
            if (values.length == 0 || values[0].equals("")) {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                orgCodigos = new ArrayList<>();
                orgNames = new ArrayList<>();
                try {
                    for (final String value : values) {
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

        // Consignatária
        String csaCodigo = getFiltroCsaCodigo(parameterMap, subtitulo, titulo, session, responsavel);
        if(TextHelper.isNull(csaCodigo)) {
            csaCodigo = "";
        }

        // servico
        List<String> svcCodigos = null;
        String servicos[] = parameterMap.get("svcCodigo");
        if (servicos != null && servicos.length > 0 && !TextHelper.isNull(servicos[0])) {
            svcCodigos = new ArrayList<>();
            if (subtitulo.length() > 0) {
                subtitulo.append(" / ");
            }
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.upper.arg0", responsavel, ""));
            for (String servico : servicos) {
                String values[] = servico.split(";");
                subtitulo.append(values[2].toUpperCase()).append(",");
                svcCodigos.add(values[0]);
            }
            subtitulo.setLength(subtitulo.length() - 1);
        }
        if (svcCodigos == null || svcCodigos.size() == 0) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informe.pelo.menos.um.servico.para.geracao.relatorio", responsavel).toLowerCase());
            return;
        }

        // situação do contrato
        if (subtitulo.length() > 0) {
            subtitulo.append("\n");
        }
        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.situacao.singular.upper.arg0", responsavel, ""));
        List<String> sadCodigos = null;
        String[] situacoes = (parameterMap.get("SAD_CODIGO"));
        if (situacoes != null && situacoes.length > 0 && !TextHelper.isNotNumeric(situacoes[0])) {
            sadCodigos = new ArrayList<>();
            try {
                AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                Map<String, String> status = adeDelegate.selectStatusAutorizacao(responsavel);
                for (String situacoe : situacoes) {
                    sadCodigos.add(situacoe);
                    subtitulo.append(status.get(situacoe).toString().toUpperCase()).append(",");
                }
                subtitulo.setLength(subtitulo.length() - 1);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return;
            }
        } else {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase());
        }

        // Prazo múltiplo
        if (parameterMap.containsKey("prazoMultiploDoze")) {
            prazoMultiploDoze = Boolean.valueOf(getParametro("prazoMultiploDoze", parameterMap));
        }

        // Prazos
        if (parameterMap.containsKey("PRAZO")) {
            prazosInformados = getParametro("PRAZO", parameterMap);
            // monta lista de prazos
            if (!TextHelper.isNull(prazosInformados)) {
                String [] prazos = prazosInformados.split(",");
                for (String prazo : prazos) {
                    if (!TextHelper.isNull(prazo)) {
                        prazosInformadosList.add(Integer.valueOf(prazo.toString()));
                    }
                }
            }
        }


        // Obtém as opções que o usuário do sistema escolheu para filtrar a busca dos usuários
        criterio.setAttribute("periodo", periodo);
        criterio.setAttribute("orgCodigo", orgCodigos);
        criterio.setAttribute("svcCodigos", svcCodigos);
        criterio.setAttribute("sadCodigos", sadCodigos);
        criterio.setAttribute("prazoMultiploDoze", prazoMultiploDoze);
        criterio.setAttribute("prazosInformados", prazosInformadosList);
        criterio.setAttribute("csaCodigo", csaCodigo);

        if(orgCodigos != null){
            for(final String org : orgCodigos){
                setTaxasEfetivas(periodo, org, svcCodigos, sadCodigos, prazoMultiploDoze, prazosInformadosList, csaCodigo);
            }
        } else {
            setTaxasEfetivas(periodo, null, svcCodigos, sadCodigos, prazoMultiploDoze, prazosInformadosList, csaCodigo);
        }

        String formatoArquivo = getStrFormato();
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.taxas.efetivas", responsavel), responsavel, parameterMap, null);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("TAXAS_MINIMAS", taxasMinimas);
        parameters.put("TAXAS_MAXIMAS", taxasMaximas);
        parameters.put("TAXAS_MEDIAS", taxasMedias);
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formatoArquivo);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);

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

    /**
     * Obtém as taxas efetivas das consignações
     * @param periodo
     * @param orgCodigo
     * @param svcCodigos
     * @param sadCodigos
     */
    private void setTaxasEfetivas(String periodo, String orgCodigo, List<String> svcCodigos, List<String> sadCodigos, boolean prazoMultiploDoze, List<Integer> prazosInformados, String csaCodigo) {
        List<String> parametros = new ArrayList<>();
        parametros.add(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF);
        parametros.add(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS);
        parametros.add(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS);

        BigDecimal taxaMinima = null;
        BigDecimal taxaMaxima = null;
        BigDecimal taxa = null;


        try {
            BigDecimal soma = new BigDecimal(0);
            int nroTaxas = 0;
            String svcCodigo = "";

            CoeficienteController coeficienteController = ApplicationContextProvider.getApplicationContext().getBean(CoeficienteController.class);

            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            List<TransferObject> lista = relatorioController.lstTaxasEfetivasContratos(periodo, orgCodigo, svcCodigos, sadCodigos, prazoMultiploDoze, prazosInformados, csaCodigo, responsavel);
            if (lista != null && !lista.isEmpty()) {
                Iterator<TransferObject> it = lista.iterator();
                while (it.hasNext()) {
                    CustomTransferObject ade = (CustomTransferObject) it.next();
                    if (!csaCodigo.equals(ade.getAttribute(Columns.CSA_CODIGO).toString())) {
                        if (nroTaxas > 0) {
                            taxasMedias.put(csaCodigo, soma.divide(new BigDecimal(nroTaxas), 2, java.math.RoundingMode.HALF_UP));
                        }
                        csaCodigo = ade.getAttribute(Columns.CSA_CODIGO).toString();
                        taxaMinima = null;
                        taxaMaxima = null;
                        svcCodigo = "";
                        soma = new BigDecimal(0);
                        nroTaxas = 0;
                    }
                    if (!svcCodigo.equals(ade.getAttribute(Columns.SVC_CODIGO).toString())) {
                        svcCodigo = ade.getAttribute(Columns.SVC_CODIGO).toString();
                    }

                    try {
                        taxa = coeficienteController.getCftVlrByAdeCodigo((String) ade.getAttribute(Columns.ADE_CODIGO), responsavel);

                        if (taxa != null) {
                            soma = soma.add(taxa);
                            nroTaxas++;
                            if (taxaMinima == null || taxa.compareTo(taxaMinima) == -1) {
                                taxaMinima = taxa;
                            }
                            if (taxaMaxima == null || taxa.compareTo(taxaMaxima) >= 0) {
                                taxaMaxima = taxa;
                            }
                        }
                        taxasMinimas.put(csaCodigo, taxaMinima);
                        taxasMaximas.put(csaCodigo, taxaMaxima);
                    } catch (CoeficienteControllerException ex) {
                        // Consignação não possui dados de coeficiente
                    }
                }
            }
            if (nroTaxas > 0) {
                taxasMedias.put(csaCodigo, soma.divide(new BigDecimal(nroTaxas), 2, java.math.RoundingMode.HALF_UP));
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
