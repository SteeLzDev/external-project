package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.relatorio.RelatorioHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorioConsignacao</p>
 * <p>Description: Classe para processamento de relatorio de Consignacao
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioConsignacao extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConsignacao.class);

    private static final String PARAM_NAME_TITULO_TAXA_JUROS = "TITULO_TAXA_JUROS";

    public ProcessaRelatorioConsignacao(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        final HashMap<String, Object> parameters = new HashMap<>();

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }
        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = "CSA";
        }

        final StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.consignacoes", responsavel), responsavel, parameterMap, null));
        final StringBuilder subTitulo = new StringBuilder();
        final Map<String, String> datas = getFiltroPeriodo(parameterMap, subTitulo, nome, session, responsavel);

        final String strPeriodo = getParametro("periodo", parameterMap);
        final String strIniPeriodo = getParametro("periodoIni", parameterMap);
        final String strFimPeriodo = getParametro("periodoFim", parameterMap);
        final String strIniPeriodoLiquidacao = getParametro("periodoIniLiquidacao", parameterMap);
        final String strFimPeriodoLiquidacao = getParametro("periodoFimLiquidacao", parameterMap);

        final String paramPeriodo = datas.get("PERIODO");
        final String paramIniPeriodo = datas.get("PERIODO_INICIAL");
        final String paramFimPeriodo = datas.get("PERIODO_FINAL");

        final boolean obrDataInclusaoPage = Boolean.parseBoolean(getParametro("obrDataInclusaoPage", parameterMap));
        if(obrDataInclusaoPage) {
	        if (TextHelper.isNull(paramPeriodo) && (TextHelper.isNull(paramIniPeriodo) || TextHelper.isNull(paramFimPeriodo))) {
	            codigoRetorno = ERRO;
	            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel);
	            return;
	        }

	        if ((!TextHelper.isNull(paramIniPeriodo) && TextHelper.isNull(paramFimPeriodo)) ||
	            (TextHelper.isNull(paramIniPeriodo) && !TextHelper.isNull(paramFimPeriodo))) {

	            codigoRetorno = ERRO;
	            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel);
	            return;
	        }
        }

        String titulo = relatorio.getTitulo() + " - ";
        if (!TextHelper.isNull(paramPeriodo)) {
            titulo += ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo);
        } else if (!TextHelper.isNull(paramIniPeriodo) && !TextHelper.isNull(paramFimPeriodo)) {
            titulo += ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo);
        }

        if (!TextHelper.isNull(strIniPeriodoLiquidacao) && !TextHelper.isNull(strFimPeriodoLiquidacao)) {
            titulo += ApplicationResourcesHelper.getMessage("rotulo.periodo.liquidacao.de.arg0.a.arg1", responsavel, strIniPeriodoLiquidacao, strFimPeriodoLiquidacao);
        }

        String fileZip = "";

        final String[] sad = (parameterMap.get("SAD_CODIGO"));
        final List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        final List<String> origensAdes = getFiltroOrigemContrato(parameterMap, subTitulo, nome, session, responsavel);
        final String[] motivoTerminoAde = parameterMap.get("chkTermino");
        final List<String> motivoTerminoAdes = motivoTerminoAde != null ? Arrays.asList(motivoTerminoAde) : null;

        if ((motivoTerminoAdes != null) && (origensAdes != null)) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente", responsavel);
            return;
        }

        String order = null;

        final String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String sboCodigo = getFiltroSboCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String uniCodigo = getFiltroUniCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final Boolean tmoDecisaoJudicial = getFiltroDecisaoJudicial(parameterMap, subTitulo, nome, session, responsavel);

        final List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> orgCodigos = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);


        final String estCodigo = ("EST".equals(tipoEntidade)) ? responsavel.getEstCodigo() : getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);

        final String path = RelatorioHelper.getCaminhoRelatorio(relatorio.getTipo(), csaCodigo, responsavel);
        final String strFormato = getStrFormato();
        String rseTipo = "";

        if (path == null) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel);

        } else {
            final String fileName = path + File.separatorChar + nome.toString();

            HashMap<String, List<String>> ocorrencia = new HashMap<>();
            List<String> toc = new ArrayList<>();
            toc.add(CodedValues.TOC_TARIF_RESERVA);
            toc.add(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO);
            ocorrencia.put("INCLUSAO", toc);

            //alterações - TOC_ALTERACAO_CONTRATO
            toc = new ArrayList<>();
            toc.add(CodedValues.TOC_ALTERACAO_CONTRATO);
            ocorrencia.put("ALTERACAO", toc);

            //exclusões - TOC_TARIF_CANCELAMENTO TOC_TARIF_LIQUIDACAO
            toc = new ArrayList<>();
            toc.add(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);
            toc.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            toc.add(CodedValues.TOC_TARIF_LIQUIDACAO);
            ocorrencia.put("EXCLUSAO", toc);

            //estoque  - TOC_ESTOQUE_MENSAL ( a criar )
            toc = new ArrayList<>();
            toc.add(CodedValues.TOC_ESTOQUE_MENSAL);
            ocorrencia.put("ESTOQUE", toc);

            //conclusão - TOC_CONCLUSAO_CONTRATO - TOC_CONCLUSAO_SEM_DESCONTO
            toc = new ArrayList<>();
            toc.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
            toc.add(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
            ocorrencia.put("CONCLUSAO", toc);

            //deferimento  - TOC_DEFERIMENTO_CONTRATO
            toc = new ArrayList<>();
            toc.add(CodedValues.TOC_DEFERIMENTO_CONTRATO);
            ocorrencia.put("DEFERIMENTO", toc);

            //Ao selecionar periodo de liquidação, somente contratos liquidados devem ser retornados
            if (!TextHelper.isNull(strIniPeriodoLiquidacao) && !TextHelper.isNull(strFimPeriodoLiquidacao)) {
                toc = new ArrayList<>();
                ocorrencia = new HashMap<>();

                toc.add(CodedValues.TOC_TARIF_LIQUIDACAO);
                ocorrencia.put("EXCLUSAO", toc);
            }

            // Correspondente
            final List<String> corCodigos = new ArrayList<>();
            if (responsavel.isCor()) {
                corCodigos.add(getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel));
            }

            if (parameterMap.containsKey("RSE_TIPO")) {
                rseTipo = parameterMap.get("RSE_TIPO")[0];
            }

            if (!TextHelper.isNull(rseTipo)) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.categoria.arg0", responsavel, rseTipo));
            }

            try {
                if (responsavel.isCsa() && corCodigos.isEmpty()) {
                    if (orgCodigos == null) {
                        order = "ORGAO";
                    }
                } else if (responsavel.isCor() || (responsavel.isCsa() && (corCodigos != null) && !corCodigos.isEmpty())) {
                    if (orgCodigos == null) {
                        order = "ORGAO";
                    }
                } else if (responsavel.isCseSupOrg() && (csaCodigo == null)) {
                    order = "CONSIGNATARIA";
                }

                final List<String> listArquiv = new ArrayList<>();

                String nomeRelConsig = "";
                for (final Entry<String, List<String>> entry : ocorrencia.entrySet()) {
                    boolean geraRelatorio = false;
                    final String chave = entry.getKey();
                    final String prefixo = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.consignacoes.prefixo." + chave.toLowerCase().trim(), responsavel);
                    final String nomeRelatorio = prefixo.replace(" ", "_") + "_" + nome.toString();
                    nomeRelConsig = path + File.separatorChar + nomeRelatorio;

                    if ("TEXT".equals(strFormato)) {
                        nomeRelConsig += ".txt";
                    } else {
                        nomeRelConsig += "." + strFormato.toLowerCase();
                    }

                    final List<String> tocTemp = entry.getValue();

                    criterio.setAttribute("MATRICULA", getFiltroRseMatricula(parameterMap, subTitulo, nome, session, responsavel));
                    criterio.setAttribute("CPF", getFiltroCpf(parameterMap, subTitulo, nome, session, responsavel));
                    criterio.setAttribute("ADE_NUMERO", getParametro("ADE_NUMERO", parameterMap));
                    criterio.setAttribute("RSE_TIPO", rseTipo);

                    if (!"EXCLUSAO".equals(chave) && !"INCLUSAO".equals(chave) && (motivoTerminoAdes == null) && (origensAdes == null)) {
                        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
                        criterio.setAttribute("PERIODO", paramPeriodo);
                        criterio.setAttribute("DATA_INI", paramIniPeriodo);
                        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
                        criterio.setAttribute("EST_CODIGO", estCodigo);
                        criterio.setAttribute("ORG_CODIGO", orgCodigos);
                        criterio.setAttribute("SBO_CODIGO", sboCodigo);
                        criterio.setAttribute("UNI_CODIGO", uniCodigo);
                        criterio.setAttribute("CSA_CODIGO", csaCodigo);
                        if (parameterMap.containsKey("corCodigo")) {
                            final String[] correspondentes = (parameterMap.get("corCodigo"));
                            if(!"".equals(correspondentes[0])) {
                                if ((correspondentes.length == 0) || "-1".equals(correspondentes[0].substring(0, 2))) {
                                    corCodigos.add("-1");
                                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                                } else {
                                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                                    for(final String cor : correspondentes){
                                        final String [] correspondente = cor.split(";");
                                        corCodigos.add(correspondente[0]);
                                        subTitulo.append(correspondente[2]).append(", ");
                                    }
                                    subTitulo.deleteCharAt(subTitulo.length()-2);
                                }
                                subTitulo.append(System.getProperty("line.separator"));
                                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                            }
                        }
                        criterio.setAttribute("SVC_CODIGO", svcCodigos);
                        criterio.setAttribute("NSE_CODIGO", nseCodigos);
                        criterio.setAttribute("SAD_CODIGO", sadCodigos);
                        criterio.setAttribute("TOC_CODIGO", tocTemp);
                        criterio.setAttribute("ORDER", order);
                        criterio.setAttribute("ORIGEM_ADE", null);
                        criterio.setAttribute("TERMINO_ADE", null);
                        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
                        criterio.setAttribute("TMO_DECISAO_JUDICIAL", tmoDecisaoJudicial);
                        if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
                            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                        }
                        geraRelatorio = true;
                    } else if ("EXCLUSAO".equals(chave) && (origensAdes == null)) {
                        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
                        criterio.setAttribute("PERIODO", paramPeriodo);
                        criterio.setAttribute("DATA_INI", paramIniPeriodo);
                        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
                        criterio.setAttribute("EST_CODIGO", estCodigo);
                        criterio.setAttribute("ORG_CODIGO", orgCodigos);
                        criterio.setAttribute("SBO_CODIGO", sboCodigo);
                        criterio.setAttribute("UNI_CODIGO", uniCodigo);
                        criterio.setAttribute("CSA_CODIGO", csaCodigo);
                        if (!TextHelper.isNull(strIniPeriodoLiquidacao) && !TextHelper.isNull(strFimPeriodoLiquidacao)) {
                            criterio.setAttribute("DATA_INI_LIQUIDACAO", reformat(strIniPeriodoLiquidacao, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00"));
                            criterio.setAttribute("DATA_FIM_LIQUIDACAO", reformat(strFimPeriodoLiquidacao, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59"));
                        }
                        if (parameterMap.containsKey("corCodigo")) {
                            final String[] correspondentes = (parameterMap.get("corCodigo"));
                            if(!"".equals(correspondentes[0])) {
                                if ((correspondentes.length == 0) || "-1".equals(correspondentes[0].substring(0, 2))) {
                                    corCodigos.add("-1");
                                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                                } else {
                                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                                    for(final String cor : correspondentes){
                                        final String [] correspondente = cor.split(";");
                                        corCodigos.add(correspondente[0]);
                                        subTitulo.append(correspondente[2]).append(", ");
                                    }
                                    subTitulo.deleteCharAt(subTitulo.length()-2);
                                }
                                subTitulo.append(System.getProperty("line.separator"));
                                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                            }
                        }
                        criterio.setAttribute("SVC_CODIGO", svcCodigos);
                        criterio.setAttribute("NSE_CODIGO", nseCodigos);
                        criterio.setAttribute("SAD_CODIGO", sadCodigos);
                        criterio.setAttribute("TOC_CODIGO", tocTemp);
                        criterio.setAttribute("ORDER", order);
                        criterio.setAttribute("ORIGEM_ADE", null);
                        criterio.setAttribute("TERMINO_ADE", motivoTerminoAdes);
                        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
                        criterio.setAttribute("TMO_DECISAO_JUDICIAL", tmoDecisaoJudicial);
                        if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
                            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                        }
                        geraRelatorio = true;
                    } else if ("INCLUSAO".equals(chave) && (motivoTerminoAdes == null)) {
                        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
                        criterio.setAttribute("PERIODO", paramPeriodo);
                        criterio.setAttribute("DATA_INI", paramIniPeriodo);
                        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
                        criterio.setAttribute("EST_CODIGO", estCodigo);
                        criterio.setAttribute("ORG_CODIGO", orgCodigos);
                        criterio.setAttribute("SBO_CODIGO", sboCodigo);
                        criterio.setAttribute("UNI_CODIGO", uniCodigo);
                        criterio.setAttribute("CSA_CODIGO", csaCodigo);
                        if (parameterMap.containsKey("corCodigo")) {
                            final String[] correspondentes = (parameterMap.get("corCodigo"));
                            if(!"".equals(correspondentes[0])) {
                                if ((correspondentes.length == 0) || "-1".equals(correspondentes[0].substring(0, 2))) {
                                    corCodigos.add("-1");
                                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                                } else {
                                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                                    for(final String cor : correspondentes){
                                        final String [] correspondente = cor.split(";");
                                        corCodigos.add(correspondente[0]);
                                        subTitulo.append(correspondente[2]).append(", ");
                                    }
                                    subTitulo.deleteCharAt(subTitulo.length()-2);
                                }
                                subTitulo.append(System.getProperty("line.separator"));
                                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                            }
                        }
                        criterio.setAttribute("SVC_CODIGO", svcCodigos);
                        criterio.setAttribute("NSE_CODIGO", nseCodigos);
                        criterio.setAttribute("SAD_CODIGO", sadCodigos);
                        criterio.setAttribute("TOC_CODIGO", tocTemp);
                        criterio.setAttribute("ORDER", order);
                        criterio.setAttribute("ORIGEM_ADE", origensAdes);
                        criterio.setAttribute("TERMINO_ADE", null);
                        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
                        criterio.setAttribute("TMO_DECISAO_JUDICIAL", tmoDecisaoJudicial);
                        if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
                            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                        }
                        geraRelatorio = true;
                    }

                    try {
                        if (geraRelatorio) {
                            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
                            parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio);
                            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
                            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
                            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
                            parameters.put(PARAM_NAME_TITULO_TAXA_JUROS, (ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel) ? ApplicationResourcesHelper.getMessage("rotulo.cet.abreviado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.abreviado", responsavel)));
                            parameters.put("RESPONSAVEL", responsavel);

                            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                            reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

                            listArquiv.add(nomeRelConsig);
                        }
                    } catch (final ReportControllerException ex) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                        LOG.error(mensagem, ex);
                    }
                }

                fileZip = fileName + ".zip";
                FileHelper.zip(listArquiv, fileZip);

                for (final String arqDelete : listArquiv) {
                    FileHelper.delete(arqDelete);
                }

                // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                enviaEmail(fileZip);

                setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);
            } catch (final Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
    }
}
