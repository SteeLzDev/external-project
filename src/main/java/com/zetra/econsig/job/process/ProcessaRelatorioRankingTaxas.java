package com.zetra.econsig.job.process;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.pdf.PDFHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorioRankingTaxas</p>
 * <p>Description: Classe para processamento de relatorio ranking taxas de juros</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioRankingTaxas extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioRankingTaxas.class);

    private static final String PARAM_NAME_PAGINACAO = "PAGINACAO";

    public ProcessaRelatorioRankingTaxas(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String strDataTaxa = "";
        Date dataTaxa = null;
        String svcCodigo = "";
        String svcDescricao = "";
        String svcIdentificador = "";
        String order = "";
        String prazosInformados = "";
        boolean prazoMultiploDoze = false;

        // Ordenação
        String ordenacao[] = parameterMap.get("ORDENACAO");
        order = parameterMap.containsKey("ORDENACAO") ? ordenacao[0] : "";

        // Serviço
        if (parameterMap.containsKey("svcCodigo")) {
            String values[] = (parameterMap.get("svcCodigo"));
            values = values[0].split(";");
            svcCodigo = values[0];
            svcIdentificador = values[1];
            svcDescricao = values[2];
        }

        // Período
        if (parameterMap.containsKey("dataTaxa")) {
            strDataTaxa = getParametro("dataTaxa", parameterMap);
            if (TextHelper.isNull(strDataTaxa)) {
                strDataTaxa = DateHelper.toDateString(DateHelper.getSystemDatetime());
            }
            try {
                dataTaxa = DateHelper.parse(strDataTaxa, LocaleHelper.getDatePattern());
            } catch (ParseException ex) {
                LOG.debug("Erro no formato da data da taxa.", ex);
            }
            LOG.debug("Relatório de Ranking de taxas: " + dataTaxa);
        }

        // Prazo múltiplo
        if (parameterMap.containsKey("prazoMultiploDoze")) {
            prazoMultiploDoze = Boolean.valueOf(getParametro("prazoMultiploDoze", parameterMap));
        }

        // Prazos
        if (parameterMap.containsKey("PRAZO")) {
            prazosInformados = getParametro("PRAZO", parameterMap);
            // Elimina erros de digitação caso haja duplicidade de vírgula
            String prazosValidos = null;
            if (!TextHelper.isNull(prazosInformados)) {
                String [] prazos = prazosInformados.split(",");
                for (String prazo : prazos) {
                    if (!TextHelper.isNull(prazo)) {
                        prazosValidos = (!TextHelper.isNull(prazosValidos) ? prazosValidos + "," : "") + prazo;
                    }
                }
                if (!TextHelper.isNull(prazosValidos)) {
                    prazosInformados = prazosValidos;
                } else {
                    prazosInformados = null;
                }
            }
        }

        try {
            // Se o relatório é no formato PDF, então divide os prazos em subrelatórios para que
            // a listagem caiba em uma página.
            List<CustomTransferObject> prazosPorSubrelatorio = null;
            if (getStrFormato().equals("PDF")) {
                try {
                    prazosPorSubrelatorio = dividirPrazosSubrelatorios(svcCodigo, !order.equals("CSA"), prazoMultiploDoze, prazosInformados);
                } catch (ReportControllerException ex) {
                    codigoRetorno = ERRO;
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                    LOG.error(ex.getMessage(), ex);
                    return;
                }
            }

            String reportName = null;
            String reportNameZip = null;
            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.taxas", responsavel), responsavel, parameterMap, null);
            // Se é necessário dividir o relatório em subrelatórios
            if (prazosPorSubrelatorio != null && prazosPorSubrelatorio.size() > 1) {
                Iterator<CustomTransferObject> it = prazosPorSubrelatorio.iterator();

                List<String> subrelatorios = new ArrayList<>();
                CustomTransferObject prazosSubrelatorio;
                while(it.hasNext()) {
                    prazosSubrelatorio = it.next();

                    String nomeSubrelatorio = gerarRelatorio(order, strDataTaxa, (String) prazosSubrelatorio.getAttribute("PRAZO_INICIAL"),
                            (String) prazosSubrelatorio.getAttribute("PRAZO_FINAL"),
                            svcCodigo, svcIdentificador, svcDescricao, prazoMultiploDoze, prazosInformados);

                    subrelatorios.add(nomeSubrelatorio);
                }

                String caminhoArquivo = getCaminhoArquivoRelatorio();
                try {
                    reportName = caminhoArquivo + File.separator + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.taxas", responsavel) + "_" + getHoje("ddMMyyHHmmss") + ".pdf";
                    PDFHelper.concatenarPDFs(subrelatorios, reportName, responsavel);
                } catch (ZetraException ex) {
                    // Exclui o novo arquivo.
                    File f = new File(caminhoArquivo);
                    if (f.exists()) {
                        f.delete();
                    }

                    throw new ReportControllerException("mensagem.erro.concatenar.subrelatorios", responsavel, ex);
                } finally {
                    // Exclui os arquivos de subrelatórios.
                    Iterator<String> itSubrelatorios = subrelatorios.iterator();
                    while(itSubrelatorios.hasNext()) {
                        File f = new File(itSubrelatorios.next());
                        if (f.exists()) {
                            f.delete();
                        }
                    }
                }

                reportNameZip = geraZip(nome.toString(), reportName);

            } else {
                reportName = gerarRelatorio(order, strDataTaxa, svcCodigo, svcIdentificador, svcDescricao, prazoMultiploDoze, prazosInformados);

                reportNameZip = geraZip(nome.toString(), reportName);
            }

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    /**
     * No caso de a quantidade de prazos ser grande, subdivide o relatório em relatórios menores de
     * forma que a quantidade de prazos caiba em uma página.
     * @param svcCodigo Código do serviço
     * @param ordenadoPorPrazo Indica se o relatório é ordenado por algum prazo.
     * @return Lista com as faixas de prazo de cada subrelatório.
     * @throws ReportControllerException
     */
    private List<CustomTransferObject> dividirPrazosSubrelatorios(String svcCodigo, boolean ordenadoPorPrazo, boolean prazoMultiploDoze, String prazosInformados) throws ReportControllerException {
        try {
            // Se o relatório for ordenado por prazo, algumas subrelatórios terão uma coluna extra com o prazo utilizado
            // para ordenação.
            int qtdeMaxPrazosPorPagina = ordenadoPorPrazo ? 5 : 6;

            // Recupera todos os prazos que o relatório deverá possuir.
            SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
            List<TransferObject> prazos = simulacaoController.getSvcPrazo(null, svcCodigo, true, prazoMultiploDoze, prazosInformados, responsavel);
            int qtdeTotalPrazos = prazos.size();

            // Guarda a relação dos prazos inicial e final para cada subrelatório.
            List<CustomTransferObject> prazosPorPagina = new ArrayList<>();
            CustomTransferObject faixaPrazos;

            // Se todos os prazos não couberem em uma só página, subdivide em faixas para cada página.
            if (qtdeTotalPrazos == 0) {
                throw new ReportControllerException("mensagem.prazo.nenhum.encontrado", responsavel);
            } else if (qtdeTotalPrazos > qtdeMaxPrazosPorPagina) {
                int qtdePrazosInseridos = 0;
                int qtdePrazosSubrelatorio = 0;
                int qtdeTotalSubrelatorios = (qtdeTotalPrazos / qtdeMaxPrazosPorPagina) + (qtdeTotalPrazos % qtdeMaxPrazosPorPagina > 0 ? 1 : 0);

                while (prazosPorPagina.size() < qtdeTotalSubrelatorios) {
                    qtdePrazosSubrelatorio = (qtdeTotalPrazos / qtdeTotalSubrelatorios) + ((qtdeTotalPrazos - qtdePrazosInseridos) % (qtdeTotalSubrelatorios - prazosPorPagina.size()) > 0 ? 1 : 0);

                    faixaPrazos = new CustomTransferObject();
                    faixaPrazos.setAttribute("PRAZO_INICIAL", prazos.get(qtdePrazosInseridos).getAttribute(Columns.PRZ_VLR).toString());
                    faixaPrazos.setAttribute("PRAZO_FINAL", prazos.get(qtdePrazosInseridos + qtdePrazosSubrelatorio - 1).getAttribute(Columns.PRZ_VLR).toString());

                    qtdePrazosInseridos += qtdePrazosSubrelatorio;
                    prazosPorPagina.add(faixaPrazos);
                }
            } else {
                // Os prazos cabem todos em uma página.
                faixaPrazos = new CustomTransferObject();
                faixaPrazos.setAttribute("PRAZO_INICIAL", prazos.get(0).getAttribute(Columns.PRZ_VLR));
                faixaPrazos.setAttribute("PRAZO_FINAL", prazos.get(prazos.size() - 1).getAttribute(Columns.PRZ_VLR));

                prazosPorPagina.add(faixaPrazos);
            }

            return prazosPorPagina;
        } catch (SimulacaoControllerException e) {
            throw new ReportControllerException(e);
        }
    }

    /**
     * Gera um relatório de ranking de taxas.
     * @param order
     * @param strDataTaxa
     * @param prazoInicial
     * @param prazoFinal
     * @param svcCodigo
     * @param svcIdentificador
     * @param svcDescricao
     * @return
     * @throws ReportControllerException
     */
    private String gerarRelatorio(String order, String strDataTaxa, String prazoInicial, String prazoFinal,
            String svcCodigo, String svcIdentificador, String svcDescricao, boolean prazoMultiploDoze, String prazosInformados) throws ReportControllerException {
        StringBuilder subtitulo = new StringBuilder();
        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.upper.arg0", responsavel, svcIdentificador + " - " + svcDescricao.toUpperCase()));
        subtitulo.append(" / ").append(ApplicationResourcesHelper.getMessage("rotulo.taxas.vigentes.no.dia.upper.arg0", responsavel, strDataTaxa)).append("\n");
        if (prazoMultiploDoze) {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.prazos.multiplos.doze", responsavel).toUpperCase()).append("\n");
        } else if (!TextHelper.isNull(prazosInformados)) {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.prazos.solicitados.upper.arg0", responsavel, prazosInformados)).append("\n");
        }
        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.ordenado.por.upper.arg0", responsavel,
                order.equals("CSA") ? ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel).toUpperCase() + " - " + order));

        if (prazoInicial != null && prazoFinal != null) {
            subtitulo.append(" / ").append(ApplicationResourcesHelper.getMessage("rotulo.prazos.de.arg0.a.arg1", responsavel, prazoInicial, prazoFinal).toUpperCase());
        }

        String [] csaAtivo = (parameterMap.get("CSA_ATIVO"));

        try {
            criterio.setAttribute("CSA_ATIVO", csaAtivo);
            criterio.setAttribute("ordenacao", order);
            criterio.setAttribute("dataTaxa", DateHelper.parse(strDataTaxa, LocaleHelper.getDatePattern()));
            criterio.setAttribute("svcCodigo", svcCodigo);
            criterio.setAttribute("prazoInicial", prazoInicial);
            criterio.setAttribute("prazoFinal", prazoFinal);
            criterio.setAttribute("prazoMultiploDoze", prazoMultiploDoze);
            criterio.setAttribute("prazosInformados", prazosInformados);
        } catch (ParseException ex) {
            throw new ReportControllerException("mensagem.erro.formato.data.taxa", responsavel, ex);
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo().toUpperCase());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

        if (prazoInicial != null && prazoFinal != null) {
            parameters.put(ReportManager.REPORT_FILE_NAME, ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.taxas", responsavel) + "_" + getHoje("ddMMyyHHmmss") + "_" + prazoInicial + "_a_" + prazoFinal);
            parameters.put(PARAM_NAME_PAGINACAO, "NAO");
        } else {
            parameters.put(PARAM_NAME_PAGINACAO, "SIM");
        }

        ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
        return reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);
    }

    /**
     * Gera um relatório de ranking de taxas.
     * @param order
     * @param strDataTaxa
     * @param svcCodigo
     * @param svcIdentificador
     * @param svcDescricao
     * @return
     * @throws ReportControllerException
     */
    private String gerarRelatorio(String order, String strDataTaxa, String svcCodigo, String svcIdentificador, String svcDescricao, boolean prazoMultiploDoze, String prazosInformados) throws ReportControllerException {
        return gerarRelatorio(order, strDataTaxa, null, null, svcCodigo, svcIdentificador, svcDescricao, prazoMultiploDoze, prazosInformados);
    }

    /**
     * Recupera o nome dos arquivos do relatório
     * @return
     * @throws ReportControllerException
     */
    private String getCaminhoArquivoRelatorio() throws ReportControllerException {
        String path = getPath(responsavel);
        if (path == null) {
            throw new ReportControllerException("mensagem.erro.interno.contate.administrador", responsavel);
        }
        path += File.separatorChar + "relatorio" + File.separatorChar
                + getEntidade(responsavel) + File.separatorChar + relatorio.getTipo();
        if (!responsavel.isCseSup()) {
            path += File.separatorChar + responsavel.getCodigoEntidade();
        }

        // Cria a pasta de relatório caso não exista.
        new File(path).mkdirs();

        return path;
    }
}
