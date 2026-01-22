package com.zetra.econsig.helper.margem.variacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * DataSet para o gráfico de variação de margem
 * @author marcelo
 * $Author$
 * $Revision$
 * $Date$
*/
public class VariacaoMargemGraficoDataSet {
    private static final int QTDE_MESES_VARIACAO = 24;

    private final AcessoSistema responsavel;
    private final String rseCodigo;
    private String orgCodigo;

    // Datas inicial e final do período de variação de margem em questão.
    private Date dataInicialPerido;
    private Date dataFinalPeriodo;

    private boolean seriesCriadas;
    private boolean seriesCriadasBrutas;

    // Lista com os valores de variação de margem para montagem da tabela
    private List<Date> datasVariacaoMargem;
    private List<Date> datasVariacaoMargemBruta;

    private Map<Date, Map<Short, Double>> variacaoMargem;
    private Map<Date, Map<Short, Double>> variacaoMargemBruta;


    // Quantidade de amostras de dados que compõe o período de variação de margem.
    private Map<Short, Integer> qtdeAmostrasPeriodo;
    private Map<Short, Integer> qtdeAmostrasPeriodoBruta;

    // Somatório das margens no período.
    private Map<Short, Double> somatorioPeriodoMargem;

    // Somatório das margens no período.
    private Map<Short, Double> somatorioPeriodoMargemBruta;

    // Margens a serem consideradas
    private final List<MargemTO> margens;

    /**
     * Construtor.
     * @param rseCodigo
     * @param exibeMargem1
     * @param exibeMargem2
     * @param exibeMargem3
     * @param responsavel
     */
    public VariacaoMargemGraficoDataSet(String rseCodigo, List<MargemTO> margens, AcessoSistema responsavel) throws ViewHelperException {
        this.rseCodigo = rseCodigo;
        this.margens = margens;
        this.responsavel = responsavel;

        seriesCriadas = false;
        dataInicialPerido = null;
        dataFinalPeriodo = null;

        // Recupera o órgão do servidor.
        try {
            ServidorDelegate serDelegate = new ServidorDelegate();
            RegistroServidorTO rseTo = serDelegate.findRegistroServidor(rseCodigo, responsavel);

            orgCodigo = rseTo.getOrgCodigo();
        } catch (ServidorControllerException ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Recupera a lista com as datas dos eventos de variação de margem.
     * @return
     * @throws ViewHelperException
     */
    public synchronized List<Date> recuperarDatasVariacaoMargem() throws ViewHelperException {
        if (!seriesCriadas) {
           criaSeries();
        }

        return datasVariacaoMargem;
    }

    /**
     * Recupera a lista com as datas dos eventos de variação de margem.
     * @return
     * @throws ViewHelperException
     */
    public synchronized List<Date> recuperarDatasVariacaoMargemBruta() throws ViewHelperException {
        if (!seriesCriadasBrutas) {
           criaSeriesBruta();
        }

        return datasVariacaoMargemBruta;
    }

    /**
     * Recupera a lista que representa a variação de margem.
     * @return
     * @throws ViewHelperException
     */
    public synchronized Map<Date, Map<Short, Double>> recuperarVariacaoMargem() throws ViewHelperException {
        if (!seriesCriadas) {
           criaSeries();
        }

        return variacaoMargem;
    }

    public synchronized Map<Date, Map<Short, Double>> recuperarVariacaoMargemBruta() throws ViewHelperException {
        if (!seriesCriadasBrutas) {
           criaSeriesBruta();
        }

        return variacaoMargemBruta;
    }

    /**
     * Obtém o valor médio da margem no período.
     * @param margem
     * @return
     */
    public synchronized Double getMediaMargem(Short marCodigo) {
        return (qtdeAmostrasPeriodo.get(marCodigo) != null && qtdeAmostrasPeriodo.get(marCodigo) > 0) ?
                somatorioPeriodoMargem.get(marCodigo) / qtdeAmostrasPeriodo.get(marCodigo) : null;
    }

    /**
     * Obtém a data inicial do período de amostra da variação da margem.
     * @return
     */
    public synchronized Date getInicioPeriodo() {
        return dataInicialPerido;
    }

    /**
     * Obtém a data final do período de amostra da variação da margem.
     * @return
     */
    public synchronized Date getFimPeriodo() {
        return dataFinalPeriodo;
    }

    /**
     * Cria as séries de valores de variação de margem para cada uma das margens.
     * @throws ViewHelperException
     */
    private void criaSeries() throws ViewHelperException {
        try {
            List<TransferObject> lstHistoricoRetorno = new ImpRetornoDelegate().lstHistoricoConclusaoRetorno(orgCodigo, VariacaoMargemGraficoDataSet.QTDE_MESES_VARIACAO, null, responsavel);

            if (lstHistoricoRetorno == null || lstHistoricoRetorno.size() == 0) {
                return;
            }

            // Cria as séries.
            datasVariacaoMargem = new ArrayList<>();
            variacaoMargem = new HashMap<>();
            qtdeAmostrasPeriodo = new HashMap<>();
            somatorioPeriodoMargem = new HashMap<>();

            ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

            for (MargemTO margemTO : margens) {
                Short marCodigo = margemTO.getMarCodigo();
                if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && margemTO.getMarCodigoPai() == null) {
                    ExibeMargem exibeMargem = new ExibeMargem(margemTO, responsavel);

                    // Recupera um mês a mais de histórico de margem, já que pode não existir dado
                    // diretamente para o evento de conclusão de retorno (caso em que a margem não foi alterada).
                    List<TransferObject> lstHistoricoMargem = servidorController.lstHistoricoVariacaoMargem(rseCodigo, marCodigo, VariacaoMargemGraficoDataSet.QTDE_MESES_VARIACAO + 1, responsavel);

                    Iterator<TransferObject> itHistRetorno = lstHistoricoRetorno.iterator();
                    TransferObject histRetorno;
                    while (itHistRetorno.hasNext()) {
                        histRetorno = itHistRetorno.next();

                        Date dataPeriodo = (Date) histRetorno.getAttribute(Columns.HCR_DATA_FIM);
                        if (dataPeriodo != null) {
                            Double valorMargem = recuperaValorHistoricoMargem(lstHistoricoMargem, (Date) histRetorno.getAttribute(Columns.HCR_DATA_FIM));

                            // Adiciona o valor à respectiva série
                            adicionaValorSerie(dataPeriodo, marCodigo, valorMargem, exibeMargem);
                        }
                    }
                }
            }

            seriesCriadas = true;
        } catch (ImpRetornoControllerException | ServidorControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Cria as séries de valores de variação de margem para cada uma das margens.
     * @throws ViewHelperException
     */
    private void criaSeriesBruta() throws ViewHelperException {
        try {
            List<TransferObject> lstHistoricoRetorno = new ImpRetornoDelegate().lstHistoricoConclusaoRetorno(orgCodigo, VariacaoMargemGraficoDataSet.QTDE_MESES_VARIACAO, null, responsavel);

            if (lstHistoricoRetorno == null || lstHistoricoRetorno.size() == 0) {
                return;
            }

            // Cria as séries.
            datasVariacaoMargemBruta = new ArrayList<>();
            variacaoMargemBruta = new HashMap<>();
            qtdeAmostrasPeriodoBruta = new HashMap<>();
            somatorioPeriodoMargemBruta = new HashMap<>();

            ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

            for (MargemTO margemTO : margens) {
                Short marCodigo = margemTO.getMarCodigo();
                if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && margemTO.getMarCodigoPai() == null) {
                    ExibeMargem exibeMargem = new ExibeMargem(margemTO, responsavel);

                    List<TransferObject> lstHistoricoMargemBruta = servidorController.lstHistoricoVariacaoMargemBruta(rseCodigo, marCodigo, VariacaoMargemGraficoDataSet.QTDE_MESES_VARIACAO + 1, responsavel);

                    if (lstHistoricoMargemBruta != null && !lstHistoricoMargemBruta.isEmpty()) {
                        Iterator<TransferObject> itHistRetorno = lstHistoricoRetorno.iterator();
                        TransferObject histRetorno;
                        while (itHistRetorno.hasNext()) {
                            histRetorno = itHistRetorno.next();
                            Date dataPeriodo = (Date) histRetorno.getAttribute(Columns.HCR_DATA_FIM);
                            if (dataPeriodo != null) {
                                Double valorMargem = recuperaValorHistoricoMargemBruta(lstHistoricoMargemBruta, (Date) histRetorno.getAttribute(Columns.HCR_DATA_FIM));

                                // Adiciona o valor à respectiva série
                                adicionaValorSerieBruta(dataPeriodo, marCodigo, valorMargem, exibeMargem);
                            }
                        }
                    }
                }
            }

            seriesCriadasBrutas = true;
        } catch (ImpRetornoControllerException | ServidorControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Dada uma lista de históricos de margens ordenados cronologicamente,
     * recupera o último valor de histórico de margem cujo evento tenha ocorrido
     * até uma data fornecida.
     * @param lstHistoricoMargem
     * @param dataEventoRetorno
     * @return
     */
    private Double recuperaValorHistoricoMargem(List<TransferObject> lstHistoricoMargem, Date dataEventoRetorno) {
        Double valorMargem = null;
        Date dataHistorico = null;
        TransferObject historicoMargem = null;

        if (lstHistoricoMargem != null && lstHistoricoMargem.size() > 0) {
            Iterator<TransferObject> it = lstHistoricoMargem.iterator();
            while (it.hasNext()) {
                historicoMargem = it.next();
                dataHistorico = (Date) historicoMargem.getAttribute(Columns.HMR_DATA);

                // Percorre a lista de registros até ultrapassar a data informada.
                // OBS: Como o histórico de margem é gerado após o histórico de conclusão de retorno, é necessário
                // dar uma tolerância de tempo, necessário para efetuar o cálculo de margem, de modo que o sistema
                // obtenha a data correta para a variação de margem (tolerância: 4 horas - MB).
                if (dataHistorico.getTime() - dataEventoRetorno.getTime() <= (4 * 60 * 60 * 1000)) {
                    if (historicoMargem.getAttribute(Columns.HMR_MARGEM_DEPOIS) != null) {
                        valorMargem = ((BigDecimal) historicoMargem.getAttribute(Columns.HMR_MARGEM_DEPOIS)).doubleValue();
                    }
                } else {
                    break;
                }
            }

            if (valorMargem == null) {
                // Se não encontrou nenhum valor anterior à data do periodo, porém existe
                // registros de histórico, então pega o HMR_MARGEM_ANTES do primeiro
                // registro posterior à data do periodo
                historicoMargem = lstHistoricoMargem.get(0);
                if (historicoMargem.getAttribute(Columns.HMR_MARGEM_ANTES) != null) {
                    valorMargem = ((BigDecimal) historicoMargem.getAttribute(Columns.HMR_MARGEM_ANTES)).doubleValue();
                }
            }
        }

        if (valorMargem == null) {
            valorMargem = 0.0;
        }
        return valorMargem;
    }

    private Double recuperaValorHistoricoMargemBruta(List<TransferObject> lstHistoricoMargem, Date dataEventoRetorno) {
        Double valorMargem = null;
        Date dataHistorico = null;
        TransferObject historicoMargem = null;

        if (lstHistoricoMargem != null && lstHistoricoMargem.size() > 0) {
            Iterator<TransferObject> it = lstHistoricoMargem.iterator();
            while (it.hasNext()) {
                historicoMargem = it.next();
                dataHistorico = (Date) historicoMargem.getAttribute(Columns.HMA_DATA);

                if (dataHistorico.getTime() - dataEventoRetorno.getTime() <= (4 * 60 * 60 * 1000)) {
                    if (historicoMargem.getAttribute(Columns.HMA_MARGEM_FOLHA) != null) {
                        valorMargem = ((BigDecimal) historicoMargem.getAttribute(Columns.HMA_MARGEM_FOLHA)).doubleValue();
                    }
                } else {
                    break;
                }
            }
        }

        if (valorMargem == null) {
            valorMargem = 0.0;
        }
        return valorMargem;
    }

    /**
     * Adiciona o valor de margem à respectiva série.
     * @param dataPeriodo
     * @param marCodigo
     * @param margem
     * @param exibeMargem
     */
    private void adicionaValorSerie(Date dataPeriodo, Short marCodigo, Double margem, ExibeMargem exibeMargem) {
        Double valorMargem;

        if (exibeMargem != null && exibeMargem.isExibeValor()) {
            valorMargem = margem;
            if (!exibeMargem.isSemRestricao()) {
                valorMargem = margem != null && margem.compareTo(0.0) < 0 ? 0.0 : margem;
            }
        } else {
            valorMargem = null;
        }

        // Atualiza as datas de período de análise de varição de margem.
        if (dataInicialPerido == null) {
            dataInicialPerido = dataPeriodo;
        }
        dataFinalPeriodo = dataPeriodo;

        // Atualiza a quantidade de meses no período.
        qtdeAmostrasPeriodo.put(marCodigo, (qtdeAmostrasPeriodo.get(marCodigo) != null ? qtdeAmostrasPeriodo.get(marCodigo).intValue() : 0) + 1);

        // Atualiza o somatório de margens.
        somatorioPeriodoMargem.put(marCodigo, (somatorioPeriodoMargem.get(marCodigo) != null ? somatorioPeriodoMargem.get(marCodigo) : 0.0) + (valorMargem != null ? valorMargem : 0.0));

        // Insere sempre no índice zero para inverter a ordem.
        if (!variacaoMargem.containsKey(dataPeriodo)) {
            variacaoMargem.put(dataPeriodo, new HashMap<Short, Double>());
        }
        variacaoMargem.get(dataPeriodo).put(marCodigo, valorMargem);
        if (!datasVariacaoMargem.contains(dataPeriodo)) {
            datasVariacaoMargem.add(0, dataPeriodo);
        }
    }

    private void adicionaValorSerieBruta(Date dataPeriodo, Short marCodigo, Double margem, ExibeMargem exibeMargem) {
        Double valorMargem;

        if (exibeMargem != null && exibeMargem.isExibeValor()) {
            valorMargem = margem;
            if (!exibeMargem.isSemRestricao()) {
                valorMargem = margem != null && margem.compareTo(0.0) < 0 ? 0.0 : margem;
            }
        } else {
            valorMargem = null;
        }

        // Atualiza as datas de período de análise de varição de margem.
        if (dataInicialPerido == null) {
            dataInicialPerido = dataPeriodo;
        }
        dataFinalPeriodo = dataPeriodo;

        // Atualiza a quantidade de meses no período.
        qtdeAmostrasPeriodoBruta.put(marCodigo, (qtdeAmostrasPeriodoBruta.get(marCodigo) != null ? qtdeAmostrasPeriodoBruta.get(marCodigo).intValue() : 0) + 1);

        // Atualiza o somatório de margens.
        somatorioPeriodoMargemBruta.put(marCodigo, (somatorioPeriodoMargemBruta.get(marCodigo) != null ? somatorioPeriodoMargemBruta.get(marCodigo) : 0.0) + (valorMargem != null ? valorMargem : 0.0));

        // Insere sempre no índice zero para inverter a ordem.
        if (!variacaoMargemBruta.containsKey(dataPeriodo)) {
            variacaoMargemBruta.put(dataPeriodo, new HashMap<Short, Double>());
        }
        variacaoMargemBruta.get(dataPeriodo).put(marCodigo, valorMargem);
        if (!datasVariacaoMargemBruta.contains(dataPeriodo)) {
            datasVariacaoMargemBruta.add(0, dataPeriodo);
        }
    }
}
