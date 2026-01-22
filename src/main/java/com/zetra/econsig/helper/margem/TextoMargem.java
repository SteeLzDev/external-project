package com.zetra.econsig.helper.margem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.ui.Model;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: TextoMargem</p>
 * <p>Description: Classe para centralização da criação da mensagem com a informação
 * das margens do servidor.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TextoMargem {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TextoMargem.class);

    private String tipoMsg = "";

    private final StringBuilder texto = new StringBuilder();

    public TextoMargem(TransferObject servidor, List<MargemTO> margens, AcessoSistema responsavel, Model model) {
        String msgBr = "";
        final List<Short> marCodigos = new ArrayList<>();
        boolean margemAdequadaPorDecisaoJudicial = false;
        // DESENV-16818 : LICIT-3796 - Rio de Janeiro - Cor de Fundo Consultar Margem
        if (margens.size() == 1) {
            final MargemTO margem = margens.get(0);
            if (margem.getMarDescricao() != null) {
                if (margem.getMrsMargemRest() == null) {
                    texto.append(msgBr).append(TextHelper.forHtmlContent(margem.getObservacao()));
                    msgBr = "<BR>";
                    if (!margem.temMargemDisponivel()) {
                        tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_ERRO : tipoMsg;
                    } else {
                        tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_INFO : tipoMsg;
                    }
                } else {
                    final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(margem.getMarTipoVlr() != null ? margem.getMarTipoVlr().toString() : CodedValues.TIPO_VLR_FIXO);
                    texto.append(msgBr).append(TextHelper.forHtmlContent(margem.getMarDescricao())).append(": ").append(TextHelper.forHtmlContent(labelTipoVlr)).append(" ");
                    texto.append(NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang()));
                    if (!TextHelper.isNull(margem.getObservacao())) {
                        texto.append(" (").append(TextHelper.forHtmlContent(margem.getObservacao())).append(")");
                    }
                    msgBr = "<BR>";
                    if (margem.getMrsMargemRest().signum() < 0) {
                        tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_ERRO : tipoMsg;
                    } else {
                        tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_INFO : tipoMsg;
                    }
                }
                marCodigos.add(margem.getMarCodigo());

                if(margem.getMarCodAdequacao() != null) {
                    margemAdequadaPorDecisaoJudicial = true;
                }
            }
        } else {
            int countMargensNegativas = 0;
            int countMargensZeradas = 0;
            int countMargensPositivas = 0;
            int countNaoExibeValor = 0;

            for (final MargemTO margem : margens) {
                final ExibeMargem exibeMargem = new ExibeMargem(margem, responsavel);
                if (margem.getMarDescricao() != null) {
                    if (margem.getMrsMargemRest() == null) {
                        texto.append(msgBr).append(TextHelper.forHtmlContent(margem.getObservacao()));
                        msgBr = "<BR>";
                        countNaoExibeValor++;
                        continue;
                    } else {
                        final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(margem.getMarTipoVlr() != null ? margem.getMarTipoVlr().toString() : CodedValues.TIPO_VLR_FIXO);
                        texto.append(msgBr).append(TextHelper.forHtmlContent(margem.getMarDescricao())).append(": ").append(TextHelper.forHtmlContent(labelTipoVlr)).append(" ");
                        texto.append(NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang()));
                        if (!TextHelper.isNull(margem.getObservacao())) {
                            texto.append(" (").append(TextHelper.forHtmlContent(margem.getObservacao())).append(")");
                        }
                        msgBr = "<BR>";

                        if (margem.getMrsMargemRest().signum() < 0 && exibeMargem.isExibeValor()) {
                            countMargensNegativas++;
                        } else if (margem.getMrsMargemRest().signum() == 0 && exibeMargem.isExibeValor()) {
                            countMargensZeradas++;
                        } else if (!exibeMargem.isExibeValor()) {
                            countNaoExibeValor++;
                        } else {
                            countMargensPositivas++;
                        }
                    }
                    marCodigos.add(margem.getMarCodigo());
                }

                msgBr = "<BR>";

                if(margem.getMarCodAdequacao() != null) {
                    margemAdequadaPorDecisaoJudicial = true;
                }
            }

            if (countMargensPositivas == margens.size() || countMargensPositivas >= 1 && countMargensNegativas == 0 || countNaoExibeValor == margens.size()) {
                tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_INFO : tipoMsg;
            } else if (countMargensNegativas >= 1 && countMargensZeradas == 0 && countMargensPositivas != 0) {
                tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_ALERT_CONSULTAR_MARGEM : tipoMsg;
            } else if (countMargensNegativas == margens.size() || countMargensNegativas >= 1 && countMargensPositivas == 0) {
                tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_ERRO : tipoMsg;
            } else {
                tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_ALERT_CONSULTAR_MARGEM : tipoMsg;
            }
        }

        if (texto.length() > 0 && servidor != null) {
            final String rotuloBaseCalculo = ApplicationResourcesHelper.getMessage("rotulo.margem.baseCalculo", responsavel);
            if (!TextHelper.isNull(rotuloBaseCalculo) && !"NULL".equalsIgnoreCase(rotuloBaseCalculo)) {
                final BigDecimal valorRestanteMontante = getVlrMaxCapitalDevido(servidor, responsavel);
                if (valorRestanteMontante != null) {
                    texto.append(msgBr).append(rotuloBaseCalculo);
                    texto.append(": ").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel));
                    texto.append(" ").append(NumberHelper.format(valorRestanteMontante.doubleValue(), NumberHelper.getLang()));
                }
            }

            final String msgDataMargem = getMensagemDataMargem(servidor, responsavel);
            if (!TextHelper.isNull(msgDataMargem)) {
                texto.append(msgBr).append(msgDataMargem);
            }

            if(margemAdequadaPorDecisaoJudicial) {
                texto.append("<BR>").append("<span style=\"font-size:0.9rem\">").append(ApplicationResourcesHelper.getMessage("rotulo.info.margem.ajuste.limite.decisao.judicial", responsavel));
                incluirMensagemMargemDecisaoJudicial(servidor, responsavel, model);
            }
        }
        if (servidor != null) {
           String motivoFaltaMargem = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_MOTIVO_FALTA_MARGEM)) ? (String) servidor.getAttribute(Columns.RSE_MOTIVO_FALTA_MARGEM) : null;
            if (motivoFaltaMargem != null) {
                tipoMsg = "".equals(tipoMsg) ? CodedValues.MSG_INFO : tipoMsg;
                texto.append("<BR>").append("<span style=\"font-size:0.9rem\">").append(ApplicationResourcesHelper.getMessage("rotulo.info.motivo.falta.margem", responsavel, motivoFaltaMargem)).append("</span>");
            }
        }
    }

    public boolean isVazio() {
        return texto.length() == 0;
    }

    public String getTexto() {
        return texto.toString();
    }

    public String getTipoMsg() {
        return tipoMsg;
    }

    public static String getMensagemDataMargem(TransferObject servidor, AcessoSistema responsavel) {
        String msgDataMargem = "";
        if (servidor != null) {
            /*
             * Mensagem de carga de margem possui os campos permitidos:
             * <MSG_DATA_CARGA>
             * <MSG_DATA_ALTERACAO>
             * <MSG_DATA_REFERENCIA>
             * <MSG_DATA_REFERENCIA_ANTERIOR>
             */
            msgDataMargem = ApplicationResourcesHelper.getMessage("rotulo.margem.msgDataMargem", responsavel);

            if (msgDataMargem.indexOf("<MSG_DATA_CARGA>") != -1) {
                String dataAtualizacao = "";
                final Date dataCargaMargem = getDataCargaMargem(servidor);
                if (dataCargaMargem != null) {
                    dataAtualizacao = ApplicationResourcesHelper.getMessage("rotulo.margem.dataCarga", responsavel, DateHelper.toDateString(dataCargaMargem));
                }
                msgDataMargem = msgDataMargem.replaceAll("<MSG_DATA_CARGA>", dataAtualizacao);
            }

            if (msgDataMargem.indexOf("<MSG_DATA_ALTERACAO>") != -1) {
                String dataAlteracao = "";
                final Date dataAlteracaoMargem = getDataAlteracaoMargem(servidor);
                if (dataAlteracaoMargem != null) {
                    dataAlteracao = ApplicationResourcesHelper.getMessage("rotulo.margem.dataAlteracao", responsavel, DateHelper.toDateString(dataAlteracaoMargem));
                }
                msgDataMargem = msgDataMargem.replaceAll("<MSG_DATA_ALTERACAO>", dataAlteracao);
            }

            if (msgDataMargem.indexOf("<MSG_DATA_REFERENCIA>") != -1 || msgDataMargem.indexOf("<MSG_DATA_REFERENCIA_ANTERIOR>") != -1) {
                String dataReferencia = "";
                String dataReferenciaAnterior = "";
                final Date[] datasReferenciaMargem = getDatasReferenciaMargem(servidor, responsavel);
                if (datasReferenciaMargem != null) {

                    dataReferencia = ApplicationResourcesHelper.getMessage("rotulo.margem.dataReferencia", responsavel, DateHelper.toPeriodString(datasReferenciaMargem[0]));

                    // Data de referência anterior = último periodo de retorno
                    dataReferenciaAnterior = ApplicationResourcesHelper.getMessage("rotulo.margem.dataReferenciaAnterior", responsavel, DateHelper.toPeriodString(datasReferenciaMargem[1]));
                }
                msgDataMargem = msgDataMargem.replaceAll("<MSG_DATA_REFERENCIA>", dataReferencia);
                msgDataMargem = msgDataMargem.replaceAll("<MSG_DATA_REFERENCIA_ANTERIOR>", dataReferenciaAnterior);
            }

            if (msgDataMargem.indexOf("<MSG_DATA_TRANSFERENCIA>") != -1) {
                String dataTransferencia = "";
                final Date dataTransferenciaMargem = getDataTransferenciaMargem(servidor, responsavel);
                if (dataTransferenciaMargem != null) {
                    dataTransferencia = ApplicationResourcesHelper.getMessage("rotulo.margem.dataTransferencia", responsavel, DateHelper.toDateString(dataTransferenciaMargem));
                }
                msgDataMargem = msgDataMargem.replaceAll("<MSG_DATA_TRANSFERENCIA>", dataTransferencia);
            }
        }
        return msgDataMargem;
    }

    /**
     * Retorna a data de carga das margens deste servidor.
     * @param servidor
     * @return
     */
    public static Date getDataCargaMargem(TransferObject servidor) {
        return (Date) servidor.getAttribute(Columns.RSE_DATA_CARGA);
    }

    /**
     * Retorna a data de alteração das margens do servidor
     * @param servidor
     * @return
     */
    public static Date getDataAlteracaoMargem(TransferObject servidor) {
        return (Date) servidor.getAttribute(Columns.RSE_DATA_ALTERACAO);
    }

    /**
     * Retorna a data de referência das margens, que é o mês seguinte ao último retorno
     * e retorna a data base do último retorno.
     * @param servidor
     * @return
     */
    public static Date[] getDatasReferenciaMargem(TransferObject servidor, AcessoSistema responsavel) {
        try {
            // Obtém o último período de retorno
            final String orgCodigo = (String) servidor.getAttribute(Columns.ORG_CODIGO);
            final ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
            final Date ultPeriodoDate = retDelegate.getUltimoPeriodoRetorno(orgCodigo, null, AcessoSistema.getAcessoUsuarioSistema());

            // Obtem o período anterior ao atual de lançamentos, se for igual ao último de retorno
            // a margem é referente ao periodo atual de lançamentos. Senão, é referente ao anterior,
            // caso que ocorre quando já tem um período exportado, porém aguardando retorno.
            Date dataReferenciaDate = PeriodoHelper.getInstance().getPeriodoAnterior(orgCodigo, responsavel);
            if (ultPeriodoDate.compareTo(dataReferenciaDate) == 0) {
                dataReferenciaDate = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
            }

            return new Date[] { dataReferenciaDate, ultPeriodoDate };
        } catch (final ImpRetornoControllerException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static Date getDataTransferenciaMargem(TransferObject servidor, AcessoSistema responsavel) {
        try {
            final ServidorDelegate serDelegate = new ServidorDelegate();

            final CustomTransferObject criterioPesquisa = new CustomTransferObject();

            criterioPesquisa.setAttribute(Columns.ORS_RSE_CODIGO, servidor.getAttribute(Columns.RSE_CODIGO));
            criterioPesquisa.setAttribute(Columns.ORS_TOC_CODIGO, servidor.getAttribute(CodedValues.TOC_RSE_TRANSFERENCIA_ENTRE_MARGENS));
            final List<TransferObject> lstOcorrencias = serDelegate.lstOrsRegistroServidor(criterioPesquisa, -1, -1, responsavel);

            if (lstOcorrencias != null && lstOcorrencias.size() > 0) {
                return (Date) lstOcorrencias.get(0).getAttribute(Columns.ORS_DATA);
            }
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    private static BigDecimal getVlrMaxCapitalDevido(TransferObject servidor, AcessoSistema responsavel) {
        try {
            final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
            final String orgCodigo = (String) servidor.getAttribute(Columns.ORG_CODIGO);
            final BigDecimal baseCalculo = (BigDecimal) servidor.getAttribute(Columns.RSE_BASE_CALCULO);
            if (baseCalculo != null && baseCalculo.signum() > 0) {
                final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                final BigDecimal vlrTotalCapitalDevidoAtual = adeDelegate.pesquisarVlrCapitalDevidoAberto(rseCodigo, orgCodigo, null, null, responsavel);
                return vlrTotalCapitalDevidoAtual != null ? baseCalculo.subtract(vlrTotalCapitalDevidoAtual) : baseCalculo;
            }
            return null;
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage());
            return null;
        }
    }

    private void incluirMensagemMargemDecisaoJudicial(TransferObject servidor, AcessoSistema responsavel, Model model) {
        try {
            if(ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MSG_MARGEM_ADEQUEADA_DECISAO_JUDICIAL, responsavel) && responsavel.isCseSupOrg()) {
                final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
                final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
                final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);

                final List<String> sadCodigos = new ArrayList<>();
                sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE);

                final List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_ALT_MULT_CONTRATO_MARGEM_LIMITE_DJ);

                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute("existeTipoOcorrencias", tocCodigos);

                final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
                final List<TransferObject> autDescontos = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), rseCodigo, null, null, sadCodigos, null, criterio, responsavel);

                final List<MargemTO> margens = margemController.lstMargemRaiz(true, responsavel);

                if(autDescontos != null && !autDescontos.isEmpty()) {
                    texto.append("<BR>");
                    for(final MargemTO margem : margens) {
                        boolean ocorrenciaIdentificada = false;
                        final Short marCodigo = margem.getMarCodigo();
                        final String marDescricao = margem.getMarDescricao();
                        Date dataOcorrencia = null;
                        final StringBuilder adeNumeros = new StringBuilder();

                        for(final TransferObject autDesconto : autDescontos) {
                            final String marCodigoAde = (String) autDesconto.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_MARGEM_LIMITE_DECISAO_JUDICIAL);
                            final String adeCodigo = (String) autDesconto.getAttribute(Columns.ADE_CODIGO);
                            final String adeNumero = String.valueOf(autDesconto.getAttribute(Columns.ADE_NUMERO));
                            if(!TextHelper.isNull(marCodigoAde) && marCodigoAde.equals(String.valueOf(marCodigo))) {
                                if(!ocorrenciaIdentificada) {
                                    final Collection<OcorrenciaAutorizacao> ocorrencias = autorizacaoController.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_ALT_MULT_CONTRATO_MARGEM_LIMITE_DJ, responsavel);
                                    if (ocorrencias != null && !ocorrencias.isEmpty()) {
                                        final OcorrenciaAutorizacao oca = ocorrencias.iterator().next();
                                        dataOcorrencia = oca.getOcaData();
                                        ocorrenciaIdentificada = true;
                                    }
                                }
                                adeNumeros.append("#linkPesquisarConsignacao1"+adeNumero);
                            }
                        }

                        if(adeNumeros.length() > 0) {
                            // É necessário criar essas tags de link, pois para os textos carregados automaticamentes no sistema é utilizado TextHelper.forHtmlComTags e este método converte para String
                            // todas as tag htmls que não estão ali configuradas, por isso é necessário este escape.

                            texto.append("<BR>").append("<span style=\"font-size:0.9rem\">").append(ApplicationResourcesHelper.getMessage("rotulo.margem.ajuste.limite.decisao.judicial", responsavel, marDescricao,DateHelper.format(dataOcorrencia, LocaleHelper.getDatePattern())));
                            texto.append(" #linkPesquisarConsignacao:").append(rseCodigo).append(adeNumeros).append("#linkPesquisarConsignacao2");
                            texto.append(ApplicationResourcesHelper.getMessage("rotulo.margem.ajuste.limite.decisao.judicial.ver.consignacoes", responsavel)).append("#linkPesquisarConsignacao3").append("</span>");
                            String infoMotivoJudicial = null;
                        	infoMotivoJudicial = ApplicationResourcesHelper.getMessage("rotulo.margem.ajuste.limite.decisao.judicial", responsavel, marDescricao, DateHelper.format(dataOcorrencia, LocaleHelper.getDatePattern()));
                        	model.addAttribute("infoMotivoJudicial", infoMotivoJudicial);
                        }
                    }
                }
            }
        } catch (AutorizacaoControllerException | MargemControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
