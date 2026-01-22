package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: ProcessaRelatorioConfCadMargem</p>
 * <p>Description: Classe para processamento de relatorio de conferência de cadastro de margem
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioConfCadMargem extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConfCadMargem.class);

    public ProcessaRelatorioConfCadMargem(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
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
        String formato = getStrFormato();

        String estabelecimento = "";
        String orgao = "";
        String situacao = "";
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.conf.cad.margem", responsavel), responsavel, parameterMap, null);

        String estCodigo = responsavel.getEstCodigo();
        List<String> orgCodigos = null;
        String rseTipo = "";

        List<String> srsCodigos = new ArrayList<>();
        Map<Short, List<String>> sinalMargem = new HashMap<>();
        String margem1 = "";
        String margem2 = "";
        String margem3 = "";

        String[] strComprometimentoMargem = parameterMap.get("comprometimentoMargem");
        List<String> comprometimentoMargem = strComprometimentoMargem != null ? Arrays.asList(strComprometimentoMargem) : null;

        String [] srtPercentualVariacaoMargemInicio = parameterMap.get("percentualVariacaoMargemInicio");
        String [] srtPercentualVariacaoMargemFim = parameterMap.get("percentualVariacaoMargemFim");
        String percentualVariacaoMargemInicio = srtPercentualVariacaoMargemInicio !=null && !TextHelper.isNull(srtPercentualVariacaoMargemInicio[0]) ? srtPercentualVariacaoMargemInicio[0] : null;
        String percentualVariacaoMargemFim = srtPercentualVariacaoMargemFim !=null && !TextHelper.isNull(srtPercentualVariacaoMargemFim[0]) ? srtPercentualVariacaoMargemFim[0] : null;

        Date penultimoPeriodoHisticoMargem = null;

        try {
            if (parameterMap.containsKey("estCodigo")) {
                String values[] = (parameterMap.get("estCodigo"));
                if (values.length == 0 || values[0].equals("")) {
                    estabelecimento = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
                } else {
                    values = values[0].split(";");
                    estCodigo = values[0];
                    estabelecimento = values[2];
                }
            } else if (!TextHelper.isNull(estCodigo)) {
                ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

                try {
                    EstabelecimentoTransferObject cto = cseDelegate.findEstabelecimento(estCodigo, responsavel);
                    if (cto != null) {
                        estabelecimento = cto.getEstNome();
                    }
                } catch (ConsignanteControllerException ex) {
                    LOG.error("Falha ao recuperar nome do estabelecimento.", ex);
                }
            }

            if (parameterMap.containsKey("orgCodigo")) {
                List<String> orgNames = null;
                String values[] = parameterMap.get("orgCodigo");
                if (values.length == 0 || values[0].equals("")) {
                    orgao = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for (final String value : values) {
                            String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            } else if (!TextHelper.isNull(responsavel.getOrgCodigo())) {
                ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

                try {
                    OrgaoTransferObject cto = cseDelegate.findOrgao(responsavel.getOrgCodigo(), responsavel);
                    if (cto != null) {
                        orgao = cto.getOrgNome();
                    }
                } catch (ConsignanteControllerException ex) {
                    LOG.error("Falha ao recuperar nome do órgão.", ex);
                }
            }

            if (parameterMap.containsKey("SRS_CODIGO")) {
                String values[] = (parameterMap.get("SRS_CODIGO"));
                if (values.length == 0 || values[0].equals("")) {
                    situacao = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
                    srsCodigos = null;
                } else {
                    for (int i = 0; i < values.length; i++) {
                        String[] aux = values[i].split(";");
                        srsCodigos.add(aux[0]);
                        situacao += aux[1];
                        if (i + 1 != values.length) {
                            situacao += " / ";
                        }
                    }
                }
            } else {
                srsCodigos = null;
            }

            if (parameterMap.containsKey("RSE_TIPO")) {
                rseTipo = parameterMap.get("RSE_TIPO")[0];
            }

            List<String> marCodigos = getFiltroMarCodigo();
            if (formato.equalsIgnoreCase("PDF") && marCodigos.size() > 3) {
                throw new ReportControllerException("mensagem.erro.relatorio.pdf.somente.tres.margens", responsavel);
            }

            try {
                MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);

                penultimoPeriodoHisticoMargem = margemController.recuperaPenultimoPeriodoHistoricoMargem(responsavel);

                if ((!TextHelper.isNull(percentualVariacaoMargemInicio) || !TextHelper.isNull(percentualVariacaoMargemFim)) && TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                    throw new ReportControllerException("mensagem.erro.relatorio.variacao.percentual.sem.cadastro.historico", responsavel);
                }

                for (MargemTO margens : margemController.lstMargemRaiz(responsavel)) {
                    Short marCodigo = margens.getMarCodigo();
                    if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && marCodigos.contains(marCodigo.toString())) {
                        String desc = "SINAL" + marCodigo;
                        if (parameterMap.containsKey(desc)) {
                            String values[] = (parameterMap.get(desc));
                            String margem = margens.getMarDescricao() + ": ";
                            if (values.length == 0 || values[0].equals("")) {
                                margem = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
                            } else {
                                for (int i = 0; i < values.length; i++) {
                                    String aux = values[i];

                                    List<String> sinal = sinalMargem.get(marCodigo);
                                    if (sinal == null) {
                                        sinal = new ArrayList<>();
                                    }
                                    sinal.add(aux);
                                    sinalMargem.put(marCodigo, sinal);

                                    if(aux.equals("1")){
                                        margem += ApplicationResourcesHelper.getMessage("rotulo.positiva.singular", responsavel);
                                    }
                                    if(aux.equals("0")){
                                        margem += ApplicationResourcesHelper.getMessage("rotulo.zerada.singular", responsavel);
                                    }
                                    if(aux.equals("-1")){
                                        margem += ApplicationResourcesHelper.getMessage("rotulo.negativa.singular", responsavel);
                                    }
                                    if (i + 1 != values.length) {
                                        margem += " / ";
                                    }
                                }
                            }

                            if (TextHelper.isNull(margem1)) {
                                margem1 = margem;
                            } else if (TextHelper.isNull(margem2)) {
                                margem2 = margem;
                            } else if (TextHelper.isNull(margem3)) {
                                margem3 = margem;
                            }
                        }
                    }
                }
            } catch (MargemControllerException ex) {
                LOG.error("Falha ao recuperar margens utilizadas.", ex);
            }

            if (comprometimentoMargem != null && !comprometimentoMargem.isEmpty()) {
                subtitulo.append(System.lineSeparator()).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.confcadmargem.comprometimento.margem", responsavel)).append(": ");

                for (String comprometimento : comprometimentoMargem) {
                    if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.menor.zero", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.0.a.10", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.10.a.20", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.20.a.30", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.30.a.40", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.40.a.50", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.50.a.60", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.60.a.70", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.70.a.80", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.80.a.90", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.90.a.100", responsavel)).append(" - ");
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM)) {
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.maior.cem", responsavel)).append(" - ");
                    }
                }
            }

            String reportName = null;
            criterio.setAttribute(Columns.ORG_CODIGO, orgCodigos);
            criterio.setAttribute(Columns.EST_CODIGO, estCodigo);
            criterio.setAttribute(Columns.MAR_CODIGO, marCodigos);
            criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
            criterio.setAttribute("SINAL_MARGEM", sinalMargem);
            criterio.setAttribute(Columns.RSE_TIPO, rseTipo);
            criterio.setAttribute("COMPROMETIMENTO_MARGEM", comprometimentoMargem);
            criterio.setAttribute("PERCENTUAL_VARIACAO_MARGEM_INICIO", percentualVariacaoMargemInicio);
            criterio.setAttribute("PERCENTUAL_VARIACAO_MARGEM_FIM", percentualVariacaoMargemFim);
            criterio.setAttribute("PENULTIMO_PERIODO", penultimoPeriodoHisticoMargem);
            criterio.setAttribute("responsavel", responsavel);

            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formato);
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_SITUACAO, situacao);
            parameters.put(ReportManager.PARAM_NAME_ORGAO, orgao);
            parameters.put(ReportManager.PARAM_NAME_ESTABELECIMENTO, estabelecimento);
            parameters.put(ReportManager.PARAM_NAME_MARGEM, margem1);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_2, margem2);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_3, margem3);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO, marCodigos != null && marCodigos.size() > 0 ? Short.valueOf(marCodigos.get(0)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_2, marCodigos != null && marCodigos.size() > 1 ? Short.valueOf(marCodigos.get(1)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_3, marCodigos != null && marCodigos.size() > 2 ? Short.valueOf(marCodigos.get(2)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_4, marCodigos != null && marCodigos.size() > 3 ? Short.valueOf(marCodigos.get(3)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_5, marCodigos != null && marCodigos.size() > 4 ? Short.valueOf(marCodigos.get(4)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_6, marCodigos != null && marCodigos.size() > 5 ? Short.valueOf(marCodigos.get(5)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_7, marCodigos != null && marCodigos.size() > 6 ? Short.valueOf(marCodigos.get(6)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_8, marCodigos != null && marCodigos.size() > 7 ? Short.valueOf(marCodigos.get(7)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_9, marCodigos != null && marCodigos.size() > 8 ? Short.valueOf(marCodigos.get(8)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_10, marCodigos != null && marCodigos.size() > 9 ? Short.valueOf(marCodigos.get(9)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_11, marCodigos != null && marCodigos.size() > 10 ? Short.valueOf(marCodigos.get(10)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_12, marCodigos != null && marCodigos.size() > 11 ? Short.valueOf(marCodigos.get(11)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_13, marCodigos != null && marCodigos.size() > 12 ? Short.valueOf(marCodigos.get(12)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_14, marCodigos != null && marCodigos.size() > 13 ? Short.valueOf(marCodigos.get(13)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_15, marCodigos != null && marCodigos.size() > 14 ? Short.valueOf(marCodigos.get(14)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_16, marCodigos != null && marCodigos.size() > 15 ? Short.valueOf(marCodigos.get(15)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_17, marCodigos != null && marCodigos.size() > 16 ? Short.valueOf(marCodigos.get(16)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_18, marCodigos != null && marCodigos.size() > 17 ? Short.valueOf(marCodigos.get(17)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_19, marCodigos != null && marCodigos.size() > 18 ? Short.valueOf(marCodigos.get(18)) : null);
            parameters.put(ReportManager.PARAM_NAME_MARGEM_CODIGO_20, marCodigos != null && marCodigos.size() > 19 ? Short.valueOf(marCodigos.get(19)) : null);
            parameters.put(ReportManager.PARAM_NAME_QTDE_MARGEM, marCodigos != null ? marCodigos.size() : 0);
            parameters.put("EXIBE_VARIACAO", !TextHelper.isNull(percentualVariacaoMargemInicio) || !TextHelper.isNull(percentualVariacaoMargemFim));
            parameters.put("RESPONSAVEL", responsavel);

            try {
                RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
                parameters.put("MARGENS", relatorioController.lstRelConfCadMargem(criterio, responsavel));
            } catch (RelatorioControllerException e) {
                LOG.error(e.getMessage(), e);
            }

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(formato, criterio, parameters, relatorio, responsavel);

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

