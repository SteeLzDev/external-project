package com.zetra.econsig.helper.folha;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BlocoProcessamentoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaInterromperProcessamentoFolha;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.BlocoProcessamentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

import org.springframework.stereotype.Component;

/**
 * <p>Title: ViewHelper que retorna dados do processamento sem bloqueio em execução</p>
 * <p>Description: Serviço REST para operações sobre entidade consignatária.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class RecuperaDadosProcessamentoSemBloqueioViewHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecuperaDadosProcessamentoSemBloqueioViewHelper.class);

    private static final String TOTAL_ATTRIBUTE = "TOTAL";

    public static DadosProcessamentoSemBloqueio getDadosProcessamentoBloco(String orgaoIdentificadorProcessamento, String orgaoIdentificadorVariacaoMargem, AcessoSistema responsavel) throws ViewHelperException {
        try {
            BlocoProcessamentoController blocoProcessamentoController = ApplicationContextProvider.getApplicationContext().getBean(BlocoProcessamentoController.class);

            BigDecimal percentual = null;
            int totalBlocosRetorno = 0;
            int totalBlocosMargem = 0;
            int totalBlocosComSucessoRetorno = 0;
            int totalBlocosComErroRetorno = 0;
            int totalBlocosComSucessoMargem = 0;
            int totalBlocosComErroMargem = 0;
            int totalParcelasRejeitadasPeriodo = 0;
            String bprPeriodo = "";
            long estimativaTerminoHoras = 0;
            long estimativaTerminoMinutos = 0;

            boolean temBlocoProcessamento = false;
            boolean temProcessoRodando = false;

            double percentualBlocosProcessados = 0.00;
            double percentualBlocosProcessadosMargem = 0.00;
            double percentualBlocosProcessadosRetorno = 0.00;
            double percentualBlocosProcessadosComErro = 0.00;
            double percentualBlocosProcessadosRejeitados = 0.00;
            Map<String, String> dadosMediaMargem = new LinkedHashMap<>();

            DadosProcessamentoSemBloqueio dadosProcessamentoBloco = new DadosProcessamentoSemBloqueio();

            // verifica se o processamento está sendo interrompido
            ProcessaInterromperProcessamentoFolha processo = (ProcessaInterromperProcessamentoFolha) ControladorProcessos.getInstance().getProcesso(ProcessaInterromperProcessamentoFolha.CHAVE);
            if (processo != null) {
                temProcessoRodando = ControladorProcessos.getInstance().verificar(ProcessaInterromperProcessamentoFolha.CHAVE, null);
            }
            if (!temProcessoRodando) {
                // recupera os blocos de processamento
                List<TransferObject> blocosProcessamento = blocoProcessamentoController.listarBlocosProcessamentoDashboard(null, null, null, orgaoIdentificadorProcessamento != null ? AcessoSistema.ENTIDADE_ORG : null, orgaoIdentificadorProcessamento, responsavel);
                if (blocosProcessamento == null || blocosProcessamento.isEmpty()) {
                    // informa ao usuário que nenhum bloco de processamento foi encontrado
                    throw new ViewHelperException("mensagem.erro.exibir.dashboard.processamento.nao.encontrado", responsavel);
                } else {
                    temBlocoProcessamento = true;
                    for (TransferObject blocos : blocosProcessamento) {
                        if (TextHelper.isNull(bprPeriodo)) {
                            bprPeriodo = blocos.getAttribute(Columns.BPR_PERIODO).toString();
                        } else if (!bprPeriodo.equals(blocos.getAttribute(Columns.BPR_PERIODO).toString())) {
                            // caso exista mais de um período, não exibe o dashboard
                            throw new ViewHelperException("mensagem.erro.exibir.dashboard.processamento.periodo", responsavel);
                        }

                        if (!blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.CANCELADO.getCodigo())) {
                            if (blocos.getAttribute(Columns.TBP_CODIGO).equals(TipoBlocoProcessamentoEnum.RETORNO.getCodigo())) {
                                // soma total blocos de retorno
                                totalBlocosRetorno += Integer.parseInt(blocos.getAttribute(TOTAL_ATTRIBUTE).toString());
                                if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo())) {
                                    // total de blocos de retorno processados com sucesso
                                    totalBlocosComSucessoRetorno += Integer.parseInt(blocos.getAttribute(TOTAL_ATTRIBUTE).toString());
                                }
                                if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo())) {
                                    // total de blocos de retorno processados com erro
                                    totalBlocosComErroRetorno += Integer.parseInt(blocos.getAttribute(TOTAL_ATTRIBUTE).toString());
                                }
                            } else if (blocos.getAttribute(Columns.TBP_CODIGO).equals(TipoBlocoProcessamentoEnum.MARGEM.getCodigo())) {
                                // soma total blocos de margem
                                totalBlocosMargem += Integer.parseInt(blocos.getAttribute(TOTAL_ATTRIBUTE).toString());
                                if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo())) {
                                    // total de blocos de margem processados com sucesso
                                    totalBlocosComSucessoMargem += Integer.parseInt(blocos.getAttribute(TOTAL_ATTRIBUTE).toString());
                                }
                                if (blocos.getAttribute(Columns.SBP_CODIGO).equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo())) {
                                    // total de blocos de margem processados com erro
                                    totalBlocosComErroMargem += Integer.parseInt(blocos.getAttribute(TOTAL_ATTRIBUTE).toString());
                                }
                            }
                        }
                    }
                }

                if (temBlocoProcessamento) {
                    // total de blocos de processamento
                    int totalBlocos = totalBlocosRetorno + totalBlocosMargem;
                    // total de blocos processados
                    int totalBlocosProcessados =  totalBlocosComSucessoMargem + totalBlocosComErroMargem + totalBlocosComSucessoRetorno + totalBlocosComErroRetorno;

                    // recupera o início do processamento dos blocos
                    List<String> sbpCodigos = Arrays.asList(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo()
                            , StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo());
                    Date dataInicioProcessamento = blocoProcessamentoController.obterInicioProcessamento(sbpCodigos, responsavel);

                    // calcula a estimativa de término do processamento
                    long estimativaEmMinutos = 0;
                    if (dataInicioProcessamento != null && totalBlocosProcessados > 0 && totalBlocosProcessados < totalBlocos) {
                        Date now = Calendar.getInstance().getTime();
                        long msPermin = 1000l * 60;
                        long diff = (now.getTime() / msPermin) - (dataInicioProcessamento.getTime() / msPermin);
                        Long tempoProcessamento = Long.valueOf(diff);
                        if (tempoProcessamento != 0) {
                            estimativaEmMinutos = (tempoProcessamento * totalBlocos) /  totalBlocosProcessados;
                            estimativaTerminoHoras = estimativaEmMinutos / 60;
                            estimativaTerminoMinutos = estimativaEmMinutos % 60;
                        }
                    }

                    // calcular percentual de blocos processados de margem e retorno
                    if (totalBlocos > 0 && totalBlocosProcessados > 0) {
                        percentual = calcularPercentualBlocos(totalBlocosProcessados, totalBlocos);
                        percentualBlocosProcessados = percentual.setScale(2,java.math.RoundingMode.HALF_UP).doubleValue();
                    }

                    // calcula o percentual de blocos processados de margem
                    int totalBlocosProcessadosMargem =  totalBlocosComSucessoMargem + totalBlocosComErroMargem;
                    if (totalBlocosMargem > 0 && totalBlocosProcessadosMargem > 0) {
                        percentual = calcularPercentualBlocos(totalBlocosProcessadosMargem, totalBlocosMargem);
                        percentualBlocosProcessadosMargem = percentual.setScale(2,java.math.RoundingMode.HALF_UP).doubleValue();
                    }

                    // calcula o percentual de blocos de retorno processados
                    int totalBlocosProcessadosRetorno =  totalBlocosComSucessoRetorno + totalBlocosComErroRetorno;
                    if (totalBlocosRetorno > 0 && totalBlocosProcessadosRetorno > 0) {
                        percentual = calcularPercentualBlocos(totalBlocosProcessadosRetorno, totalBlocosRetorno);
                        percentualBlocosProcessadosRetorno = percentual.setScale(2,java.math.RoundingMode.HALF_UP).doubleValue();
                    }

                    // calcula o percentual de blocos processados com erro de margem e retorno
                    int totalBlocosProcessadosComErro = totalBlocosComErroMargem + totalBlocosComErroRetorno;
                    if (totalBlocosProcessados > 0 && totalBlocosProcessadosComErro > 0) {
                        percentual = calcularPercentualBlocos(totalBlocosProcessadosComErro, totalBlocosProcessados);
                        percentualBlocosProcessadosComErro = percentual.setScale(2,java.math.RoundingMode.HALF_UP).doubleValue();
                    }

                    // calcula o percentual parcelas rejeitadas em relação ao total de blocos processados de retorno
                    Date periodo = DateHelper.parseExceptionSafe(bprPeriodo, "yyyy-MM-dd");
                    totalParcelasRejeitadasPeriodo = blocoProcessamentoController.countParcelasRejeitadasPeriodoAtual(periodo, orgaoIdentificadorProcessamento != null ? AcessoSistema.ENTIDADE_ORG : null, orgaoIdentificadorProcessamento, responsavel);
                    if (totalBlocosProcessados > 0 && totalParcelasRejeitadasPeriodo > 0) {
                        percentual = calcularPercentualBlocos(totalParcelasRejeitadasPeriodo, totalBlocosProcessadosRetorno);
                        percentualBlocosProcessadosRejeitados = percentual.setScale(2,java.math.RoundingMode.HALF_UP).doubleValue();
                    }

                    // recupera o histórico de média de margem dos 6 meses anteriores ao período de processamento (margem 1)
                    Date periodoIni = DateHelper.addMonths(periodo, -6);
                    Date periodoFim = DateHelper.addMonths(periodo, -1);
                    BigDecimal variacaoMargem = null;
                    List<TransferObject> historicoMediaMargem = blocoProcessamentoController.listarHistoricoMediaMargem(periodoIni, periodoFim, orgaoIdentificadorVariacaoMargem != null ? AcessoSistema.ENTIDADE_ORG : null, orgaoIdentificadorVariacaoMargem, CodedValues.INCIDE_MARGEM_SIM, responsavel);
                    if (historicoMediaMargem != null && !historicoMediaMargem.isEmpty()) {
                        for (TransferObject media : historicoMediaMargem) {
                            double margemAntiga = media.getAttribute(Columns.HMM_MEDIA_MARGEM_ANTES) != null ? ((BigDecimal) media.getAttribute(Columns.HMM_MEDIA_MARGEM_ANTES)).doubleValue() : 0.00;
                            double margemAtual = media.getAttribute(Columns.HMM_MEDIA_MARGEM_DEPOIS) != null ? ((BigDecimal) media.getAttribute(Columns.HMM_MEDIA_MARGEM_DEPOIS)).doubleValue() : 0.00;
                            variacaoMargem = calcularVariacaoMargem(margemAntiga, margemAtual);
                            dadosMediaMargem.put(DateHelper.getMonthName((Date) media.getAttribute(Columns.HPM_PERIODO)), variacaoMargem.setScale(2,java.math.RoundingMode.HALF_UP).toString());
                        }
                    }

                    // recupera a média de margem dos servidores que já tiveram os blocos processados
                    List<TransferObject> mediaMargemProcessada = blocoProcessamentoController.obterMediaMargemProcessada(orgaoIdentificadorVariacaoMargem != null ? AcessoSistema.ENTIDADE_ORG : null, orgaoIdentificadorVariacaoMargem, responsavel);
                    if (mediaMargemProcessada != null && !mediaMargemProcessada.isEmpty()) {
                        TransferObject media = mediaMargemProcessada.iterator().next();
                        double margemAntiga = media.getAttribute(Columns.HMR_MARGEM_ANTES) != null ? (double) media.getAttribute(Columns.HMR_MARGEM_ANTES) : 0.00;
                        double margemAtual = media.getAttribute(Columns.HMR_MARGEM_DEPOIS) != null ? (double) media.getAttribute(Columns.HMR_MARGEM_DEPOIS) : 0.00;
                        variacaoMargem = calcularVariacaoMargem(margemAntiga, margemAtual);
                        dadosMediaMargem.put(DateHelper.getMonthName(periodo), variacaoMargem.setScale(2,java.math.RoundingMode.HALF_UP).toString());
                    }
                }
            }

            dadosProcessamentoBloco.setTemBlocoProcessamento(temBlocoProcessamento);
            dadosProcessamentoBloco.setTemProcessoRodando(temProcessoRodando);

            dadosProcessamentoBloco.setBprPeriodo(bprPeriodo);

            dadosProcessamentoBloco.setEstimativaTerminoHoras(estimativaTerminoHoras);
            dadosProcessamentoBloco.setEstimativaTerminoMinutos(estimativaTerminoMinutos);


            dadosProcessamentoBloco.setPercentualBlocosProcessados(percentualBlocosProcessados);
            dadosProcessamentoBloco.setPercentualBlocosProcessadosMargem(percentualBlocosProcessadosMargem);
            dadosProcessamentoBloco.setPercentualBlocosProcessadosRetorno(percentualBlocosProcessadosRetorno);
            dadosProcessamentoBloco.setPercentualBlocosProcessadosComErro(percentualBlocosProcessadosComErro);
            dadosProcessamentoBloco.setPercentualBlocosProcessadosRejeitados(percentualBlocosProcessadosRejeitados);

            dadosProcessamentoBloco.setDadosMediaMargem(dadosMediaMargem);
            dadosProcessamentoBloco.setLstOrgao(carregarListaOrgao(responsavel));

            return dadosProcessamentoBloco;
        } catch (BlocoProcessamentoControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    private static BigDecimal calcularPercentualBlocos(int qtdeParcial, int qtdeTotal) {
        BigDecimal variacaoMargem = BigDecimal.valueOf(0.0);
        if (qtdeParcial > 0 && qtdeTotal > 0 ) {
            variacaoMargem = new BigDecimal(qtdeParcial).multiply(new BigDecimal(100)).divide(new BigDecimal(qtdeTotal),4,java.math.RoundingMode.HALF_UP).setScale(2,java.math.RoundingMode.HALF_UP);
        }
        return variacaoMargem.abs();
    }

    private static BigDecimal calcularVariacaoMargem(double margemAntiga, double margemAtual) {
        BigDecimal variacaoMargem = BigDecimal.valueOf(0.0);
        if (margemAntiga != 0 && margemAtual != 0 ) {
            variacaoMargem = BigDecimal.valueOf(((margemAtual * 100.00) / margemAntiga) - 100.00);
        }
        return variacaoMargem.abs();
    }

    private static List<TransferObject> carregarListaOrgao(AcessoSistema responsavel) {
        List<TransferObject> lstOrgao = null;

        try {
            if (responsavel.isCseSupOrg()) {
                CustomTransferObject criterio = new CustomTransferObject();
                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    criterio.setAttribute(Columns.ORG_EST_CODIGO, responsavel.getEstCodigo());
                } else if (responsavel.isOrg()) {
                    criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
                }
                ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                lstOrgao = consignanteController.lstOrgaos(criterio, responsavel);

            } else if (responsavel.isCsaCor()) {
                String corCodigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
                String csaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade() : ((responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidadePai() : null);
                ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
                lstOrgao = convenioController.getOrgCnvAtivo(csaCodigo, corCodigo, responsavel);
            }
        } catch (ConsignanteControllerException | ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return lstOrgao;
    }

}
