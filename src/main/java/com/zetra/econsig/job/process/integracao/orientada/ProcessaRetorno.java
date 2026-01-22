package com.zetra.econsig.job.process.integracao.orientada;

import java.io.File;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRetorno</p>
 * <p>Description: Classe para processamento orientado de arquivos de retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRetorno extends com.zetra.econsig.job.process.ProcessaRetorno {
    private final String tipoEntidade;
    private final String codigoEntidade;

    public ProcessaRetorno(String nomeArquivoEntrada, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) {
        super(nomeArquivoEntrada, orgCodigo, estCodigo, tipo, null, responsavel);
        if (!TextHelper.isNull(orgCodigo)) {
            tipoEntidade = "ORG";
            codigoEntidade = orgCodigo;
        } else if (!TextHelper.isNull(estCodigo)) {
            tipoEntidade = "EST";
            codigoEntidade = estCodigo;
        } else {
            tipoEntidade = "CSE";
            codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
        }
    }

    @Override
    protected void executar() {
        String horaInicioStr = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        try {
            if (!hasParcelaPeriodo()) {
                codigoRetorno = AVISO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.aviso.processamento.retorno.sem.parcelas", responsavel, horaInicioStr);
            } else {
                List<String> orgCodigos = new ArrayList<>();
                orgCodigos.add(orgCodigo);
                List<String> estCodigos = new ArrayList<>();
                estCodigos.add(estCodigo);
                PeriodoDelegate perDelegate = new PeriodoDelegate();
                List<TransferObject> periodoExportacao = perDelegate.obtemPeriodoImpRetorno(null, null, false, responsavel);
                Date prdDataDesconto = null;
                String pexPeriodo = null;
                if (!periodoExportacao.isEmpty()) {
                    pexPeriodo = periodoExportacao.get(0).getAttribute(Columns.PEX_PERIODO).toString();
                    prdDataDesconto = DateHelper.parse(pexPeriodo, "yyyy-MM-dd");
                }

                ParametroDelegate parDelegate = new ParametroDelegate();
                String paramRecalculaMargem = CodedValues.TPC_RECALCULA_MARGEM_IMP_RETORNO;
                String recalculaMargem = (String) ParamSist.getInstance().getParam(paramRecalculaMargem, responsavel);
                // Altera os parâmetro no banco de dados
                parDelegate.updateParamSistCse(CodedValues.TPC_NAO, paramRecalculaMargem, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                // Altera o cache de parâmetros em memória
                ParamSist.getInstance().setParam(paramRecalculaMargem, CodedValues.TPC_NAO);

                super.executar();

                // Volta a configuração anterior
                parDelegate.updateParamSistCse(recalculaMargem, paramRecalculaMargem, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                // Altera o cache de parâmetros em memória
                ParamSist.getInstance().setParam(paramRecalculaMargem, recalculaMargem);

                if (codigoRetorno == SUCESSO) {
                    paramRecalculaMargem = CodedValues.TPC_RECALCULA_MARGEM_CONCLUSAO_RETORNO;
                    recalculaMargem = (String) ParamSist.getInstance().getParam(paramRecalculaMargem, responsavel);
                    // Altera os parâmetro no banco de dados
                    parDelegate.updateParamSistCse(CodedValues.TPC_SIM, paramRecalculaMargem, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                    // Altera o cache de parâmetros em memória
                    ParamSist.getInstance().setParam(paramRecalculaMargem, CodedValues.TPC_SIM);

                    // Cria o delegate necessário para o processo
                    ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
                    RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

                    retDelegate.finalizarIntegracaoFolha(tipoEntidade, codigoEntidade, responsavel);
                    relatorioController.geraRelatorioIntegracao(estCodigo, orgCodigo, responsavel);

                    // Volta a configuração anterior
                    parDelegate.updateParamSistCse(recalculaMargem, paramRecalculaMargem, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                    // Altera o cache de parâmetros em memória
                    ParamSist.getInstance().setParam(paramRecalculaMargem, recalculaMargem);

                    // Coloca mensagem de sucesso
                    File entrada = new File(nomeArquivoEntrada);
                    StringBuilder resultado = new StringBuilder();
                    resultado.append(ApplicationResourcesHelper.getMessage("mensagem.sucesso.processamento.retorno", responsavel, horaInicioStr));
                    resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.titulo", responsavel, entrada.getName()));
                    resultado.append("<br>");
                    resultado.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.parcelas.nao.retornadas.pela.folha.foram.marcadas.rejeitadas.relatorios.consignatarias.gerados", responsavel));
                    resultado.append("<br>");
                    ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
                    List<TransferObject> statusList = parcelaController.lstResumoParcelasPerido(prdDataDesconto, null, null, responsavel);
                    BigInteger qtdeLiquidada = BigInteger.ZERO, qtdeRejeitada = BigInteger.ZERO, qtdeSemRetorno = BigInteger.ZERO;
                    for (TransferObject status : statusList) {
                        switch (status.getAttribute(Columns.SPD_CODIGO).toString()) {
                            case CodedValues.SPD_LIQUIDADAFOLHA:
                            case CodedValues.SPD_LIQUIDADAMANUAL:
                                qtdeLiquidada = qtdeLiquidada.add((BigInteger) status.getAttribute("qtde"));
                                break;

                            case CodedValues.SPD_REJEITADAFOLHA:
                                qtdeRejeitada = qtdeRejeitada.add((BigInteger) status.getAttribute("qtde"));
                                break;

                            case CodedValues.SPD_SEM_RETORNO:
                                qtdeSemRetorno = qtdeSemRetorno.add((BigInteger) status.getAttribute("qtde"));
                                break;

                            default:
                                break;
                        }
                    }
                    resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.retorno.liquidadas", responsavel, qtdeLiquidada.toString()));
                    resultado.append("<br>");
                    resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.retorno.rejeitadas", responsavel, qtdeRejeitada.toString()));
                    resultado.append("<br>");
                    resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.retorno.semRetorno", responsavel, qtdeSemRetorno.toString()));

                    mensagem = resultado.toString();
                }
            }

        } catch (ParcelaControllerException | ImpRetornoControllerException | ParametroControllerException | RelatorioControllerException | PeriodoException | ParseException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.conclusao.retorno", responsavel, horaInicioStr) + "<br>"
                    + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }

    private boolean hasParcelaPeriodo() throws ParcelaControllerException {
        List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_EMABERTO);
        spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
        spdCodigos.add(CodedValues.SPD_AGUARD_PROCESSAMENTO);
        ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
        return (parcelaController.countParcelas(tipoEntidade, null, null, null, null, null, spdCodigos, null, responsavel) > 0);
    }
}
