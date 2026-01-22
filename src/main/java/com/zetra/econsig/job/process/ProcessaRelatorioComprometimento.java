package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p> Title: ProcessaRelatorioComprometimento</p>
 * <p> Description: Classe para processamento de relatorio de compromentimento de margem</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioComprometimento extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioComprometimento.class);

    private List<TransferObject> servicos;

    public ProcessaRelatorioComprometimento(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
        setServicos();
    }

    @Override
    protected void executar() {
        final Map<Short, List<TransferObject>> svcsPorIncidencia = retornaServicosSeparadosPorIncidencia();
        List<String> sinalMargem = new ArrayList<>();
        if (parameterMap.containsKey("SINAL")) {
            sinalMargem = Arrays.asList(parameterMap.get("SINAL"));
        }
        final String[] strComprometimentoMargem = parameterMap.get("comprometimentoMargem");
        final List<String> comprometimentoMargem = strComprometimentoMargem != null ? Arrays.asList(strComprometimentoMargem) : null;

        final String [] srtPercentualVariacaoMargemInicio = parameterMap.get("percentualVariacaoMargemInicio");
        final String [] srtPercentualVariacaoMargemFim = parameterMap.get("percentualVariacaoMargemFim");
        final String percentualVariacaoMargemInicio = (srtPercentualVariacaoMargemInicio !=null) && !TextHelper.isNull(srtPercentualVariacaoMargemInicio[0]) ? srtPercentualVariacaoMargemInicio[0] : null;
        final String percentualVariacaoMargemFim = (srtPercentualVariacaoMargemFim !=null) && !TextHelper.isNull(srtPercentualVariacaoMargemFim[0]) ? srtPercentualVariacaoMargemFim[0] : null;

        Date penultimoPeriodoHisticoMargem = null;

        if (svcsPorIncidencia.isEmpty()) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servico.selecionado.nao.incide.margem", responsavel);
        } else {
        	final StringBuilder subtituloOrgEst = new StringBuilder();

            String estCodigo = responsavel.getEstCodigo();
            final StringBuilder nomeEstabelecimento = new StringBuilder();
            String estabelecimento = "";
            if (parameterMap.containsKey("estCodigo")) {
                String[] values = (parameterMap.get("estCodigo"));
                if ((values.length == 0) || "".equals(values[0])) {
                    subtituloOrgEst.append(System.lineSeparator()).append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                    estabelecimento = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
                } else {
                    values = values[0].split(";");
                    estCodigo = values[0];
                    subtituloOrgEst.append(System.lineSeparator()).append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, values[2]));
                    estabelecimento = values[2];
                    nomeEstabelecimento.append("_").append(values[1]);
                }
            } else if (!TextHelper.isNull(estCodigo)) {
                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

                try {
                    final EstabelecimentoTransferObject cto = cseDelegate.findEstabelecimento(estCodigo, responsavel);
                    if (cto != null) {
                        estabelecimento = cto.getEstNome();
                    }
                } catch (final ConsignanteControllerException ex) {
                    LOG.error("Falha ao recuperar nome do estabelecimento.", ex);
                }
            }
            List<String> orgNames = null;
            List<String> orgCodigos = null;
            if(responsavel.isOrg()){
                orgCodigos = new ArrayList<>();
                orgCodigos.add(responsavel.getOrgCodigo());
            }
            if (parameterMap.containsKey("orgCodigo")) {
                final String[] values = parameterMap.get("orgCodigo");
                if ("".equals(values[0])) {
                    subtituloOrgEst.append(System.lineSeparator() + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for (final String value : values) {
                            final String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subtituloOrgEst.append(System.lineSeparator() + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel,  String.valueOf(orgNames).replace("[", "").replace("]", "")));
                     } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                  }
                }
                subtituloOrgEst.append(System.lineSeparator());
            }

            final List<String> listArquiv = new ArrayList<>();
            String path = getPath(responsavel);
            String nomeZip = null;

            if (path == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            } else {
                final String entidade = getEntidade(responsavel);
                path += File.separatorChar + "relatorio" + File.separatorChar
                        + entidade + File.separatorChar + relatorio.getTipo();
                if (!responsavel.isCseSup()) {
                    path += File.separatorChar + responsavel.getCodigoEntidade();
                }
            }
            final String hoje = getHoje("ddMMyyHHmmss");

            final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);

            for (final Entry<Short, List<TransferObject>> entry : svcsPorIncidencia.entrySet()) {
                //Critérios utilizados para a geração da SQL que irá recuperar os dados do relatório
                final Short incidencia = entry.getKey();
                // Lista de serviços que serão utilizados para gerar o relatorio separado por incidência de margem
                final List<TransferObject> svcs = entry.getValue();

                StringBuilder subtitulo = new StringBuilder();
                subtitulo.append(subtituloOrgEst);
                //Lista de natureza de servico de acordo com a seleção
                final List<String> naturezaSvcCodigo = new ArrayList<>();
                final List<String> naturezaSvcDescricao = new ArrayList<>();

                for (final TransferObject servico : svcs) {
                    try {
                        final CustomTransferObject naturezaServico = servicoController.findNaturezaServico((String) (servico.getAttribute(Columns.SVC_CODIGO)), responsavel);
                        if (naturezaServico != null) {
                            final String nseCodigo = (String) naturezaServico.getAttribute(Columns.NSE_CODIGO);
                            final String nseDescricao = (String) naturezaServico.getAttribute(Columns.NSE_DESCRICAO);

                            if (!naturezaSvcCodigo.contains(nseCodigo)) {
                                naturezaSvcDescricao.add(nseDescricao);
                                naturezaSvcCodigo.add(nseCodigo);
                            }
                        }
                    } catch (final ServicoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.natureza.servico.arg0", responsavel, ""));
                for (final String natureza : naturezaSvcDescricao) {
                    subtitulo.append(natureza).append(",");
                }

                final int labelMaxSize = 75;
                final int valorSubNse =  (subtitulo.length()) - (subtituloOrgEst.length());

                if (valorSubNse > labelMaxSize) {
                	final int valorTotal = labelMaxSize + (subtituloOrgEst.length());
                	subtitulo = new StringBuilder(subtitulo.substring(0, valorTotal - 4)).append(" ...");
                } else {
                	subtitulo.deleteCharAt(subtitulo.length()-1);
                }
                subtitulo.append(System.lineSeparator());

                final StringBuilder titulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.comprometimento.margem.titulo", responsavel).toUpperCase()).append(" - ");
                String margemDescricao = "";
                try {
                    MargemTO margemTO = new MargemTO();
                    margemTO.setMarCodigo(incidencia);
                    final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
                    margemTO = margemController.findMargem(margemTO, responsavel);
                    margemDescricao = margemTO.getMarDescricao();
                    titulo.append(margemDescricao);
                } catch (final MargemControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                try {
                    final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);

                    penultimoPeriodoHisticoMargem = margemController.recuperaPenultimoPeriodoHistoricoMargem(responsavel);

                    if ((!TextHelper.isNull(percentualVariacaoMargemInicio) || !TextHelper.isNull(percentualVariacaoMargemFim)) && TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                        throw new ReportControllerException("mensagem.erro.relatorio.variacao.percentual.sem.cadastro.historico", responsavel);
                    }
                } catch (final MargemControllerException | ReportControllerException ex) {
                    LOG.error("Falha ao recuperar margens utilizadas.", ex);
                }

                subtitulo.append(criaSubtituloSvc(svcs));

                if ((comprometimentoMargem != null) && !comprometimentoMargem.isEmpty()) {
                    subtitulo.append(System.lineSeparator()).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.confcadmargem.comprometimento.margem", responsavel)).append(": ");

                    for (final String comprometimento : comprometimentoMargem) {
                        if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.menor.zero", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.0.a.10", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.10.a.20", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.20.a.30", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.30.a.40", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.40.a.50", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.50.a.60", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.60.a.70", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.70.a.80", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.80.a.90", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.90.a.100", responsavel)).append(" - ");
                        } else if (CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM.equals(comprometimento)) {
                            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.filtro.comprometimento.margem.maior.cem", responsavel)).append(" - ");
                        }
                    }
                }

                criterio.setAttribute(Columns.CNV_SVC_CODIGO, svcs);
                criterio.setAttribute(CodedValues.TPS_INCIDE_MARGEM, incidencia);
                criterio.setAttribute(Columns.EST_CODIGO, estCodigo);
                criterio.setAttribute(Columns.ORG_CODIGO, orgCodigos);
                criterio.setAttribute("SINAL_MARGEM", sinalMargem);
                criterio.setAttribute("COMPROMETIMENTO_MARGEM", comprometimentoMargem);
                criterio.setAttribute("PERCENTUAL_VARIACAO_MARGEM_INICIO", percentualVariacaoMargemInicio);
                criterio.setAttribute("PERCENTUAL_VARIACAO_MARGEM_FIM", percentualVariacaoMargemFim);
                criterio.setAttribute("PENULTIMO_PERIODO", penultimoPeriodoHisticoMargem);

                // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_margem_dataHora
                final String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.comprometimento", responsavel), responsavel, parameterMap, TextHelper.removeAccent(margemDescricao).replace(" ", "_").toUpperCase());

                final HashMap<String, Object> parameters = new HashMap<>();
                parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                parameters.put(ReportManager.PARAM_NAME_ESTABELECIMENTO, estabelecimento);
                parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true, session, responsavel));
                parameters.put(ReportManager.REPORT_FILE_NAME, nome);
                parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
                parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
                parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

                String reportName = null;
                try {
                    final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                    reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

                    listArquiv.add(reportName);

                } catch (final ReportControllerException ex) {
                    codigoRetorno = ERRO;
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                    LOG.error(mensagem, ex);
                    break;
                }
            }

            try{
                nomeZip = (ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.comprometimento", responsavel) + nomeEstabelecimento.append("_").append(hoje).toString()).replace('/', '-');
                final String fileZip = path + File.separatorChar + nomeZip + ".zip";
                FileHelper.zip(listArquiv, fileZip);

                for(final String arqDelete: listArquiv){
                    FileHelper.delete(arqDelete);
                }

                setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);

                enviaEmail(fileZip);

            } catch (final Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
    }


    /**
     * Cria o subtítulo que será utilizado no relatório, com os serviços que estão sendo considerados no relatório.
     *
     * @param svcs Lista de serviços que serão usados para gerar o relatório.
     * @return Retorna o subtítulo do relatório listando os serviços que foram utilizados.
     */
    private String criaSubtituloSvc(List<TransferObject> svcs) {
    	StringBuilder subtitulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
        for (final TransferObject cto : svcs) {
            subtitulo.append(cto.getAttribute(Columns.SVC_DESCRICAO).toString()).append(",");
        }
        subtitulo.deleteCharAt(subtitulo.length()-1);
        final int labelMaxSize = 150;
        if (subtitulo.length() > labelMaxSize) {
            subtitulo = new StringBuilder(subtitulo.substring(0, labelMaxSize - 4)).append(" ...");
        }
        return subtitulo.toString();
    }

    /**
     * Seta os serviços selecionados pelo usuário em uma lista de serviços.
     * Se nenhum serviço for selecionado,
     * serão buscados todos os serviços ativos para que sejam setados para gerar o relatório.
     *
     */
    private void setServicos() {
        servicos = new ArrayList<>();

        final List<String> nseCodigos = new ArrayList<>();
        if (!TextHelper.isNull(parameterMap.get("nseCodigo")[0])) {
            final String[] nses = parameterMap.get("nseCodigo");
            String[] values;
            for (final String nse : nses) {
                values = nse.split(";");
                nseCodigos.add(values[0]);
            }
        }

        if (!TextHelper.isNull(parameterMap.get("svcCodigo")[0])) {
            //Se o usuário selecionou algum serviço,
            //inclui na lista de serviços para serem usados para gerar o relatório
        	final String[] svcs = parameterMap.get("svcCodigo");
            String[] values;
            for (final String svc : svcs) {
                final TransferObject cto = new CustomTransferObject();
                values = svc.split(";");
                cto.setAttribute(Columns.SVC_CODIGO, values[0]);
                cto.setAttribute(Columns.SVC_DESCRICAO, values[2]);
                servicos.add(cto);
            }
        } else {
            //Caso não tenha sido selecionado nenhum serviço, busca todos os serviços
            //para serem usados para gerar o relatório
            try {
                final ConvenioDelegate convenioDelegate = new ConvenioDelegate();
                final TransferObject criterioSvc = new CustomTransferObject();
                criterioSvc.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
                final List<TransferObject> svc = convenioDelegate.lstServicos(criterioSvc, responsavel);
                for (final TransferObject servico : svc) {
	            	if(nseCodigos.contains(servico.getAttribute(Columns.NSE_CODIGO)) || nseCodigos.isEmpty()) {
	            		servicos.add(servico);
	            	}
                }

            } catch (final ConvenioControllerException ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(ex.getMessage(), ex);
            }
        }

    }

    /**
     * Retorna os serviços que serão utilizados para gerar o relatório separados pela incidência na margem.
     * Somente os serviços que incidirem na margem serão retornados.
     *
     * @return Mapeamento de serviços que incidem na margem por incidência.
     */
    private Map<Short, List<TransferObject>> retornaServicosSeparadosPorIncidencia() {
        final Map<Short, List<TransferObject>> retorno = new HashMap<>();

        try {
            final List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_INCIDE_MARGEM);

            final ParametroDelegate parametroDelegate = new ParametroDelegate();
            for (final TransferObject servico : servicos) {
                final String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                final ParamSvcTO paramTO = parametroDelegate.selectParamSvcCse(svcCodigo, tpsCodigo, responsavel);

                final Short incideMargem = paramTO.getTpsIncideMargem();
                if (!incideMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                    //Somente os serviços que incidirem na margem serão utilizados
                    List<TransferObject> svcs = retorno.get(incideMargem);
                    if (svcs == null) {
                        svcs = new ArrayList<>();
                        retorno.put(incideMargem, svcs);
                    }
                    svcs.add(servico);
                }
            }

        } catch (final ParametroControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;

    }
}