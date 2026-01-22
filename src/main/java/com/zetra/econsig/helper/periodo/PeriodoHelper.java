package com.zetra.econsig.helper.periodo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateUtils;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PeriodoHelper</p>
 * <p>Description: Singleton repositório das informações sobre o período atual</p>
 * <p>Copyright: Copyright (c) 2013-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class PeriodoHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PeriodoHelper.class);

    private static final String HASHKEY_FOR_NULL = "__NULL__";

    /** Data de controle para atualização diária do cache */
    private Date dataLimite;

    /** Cache das informações do período por órgão */
    private final Map<String, DadosPeriodo> cachePeriodo;
    private final Map<String, DadosPeriodo> cachePeriodoBeneficio;

    private static class SingletonHelper {
        private static final PeriodoHelper instance = new PeriodoHelper();
    }

    public static PeriodoHelper getInstance() {
        return SingletonHelper.instance;
    }

    private PeriodoHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            cachePeriodo = new ExternalMap<>(prefix + "-cachePeriodo", HASHKEY_FOR_NULL);
            cachePeriodoBeneficio = new ExternalMap<>(prefix + "-cachePeriodoBeneficio", HASHKEY_FOR_NULL);
        } else {
            cachePeriodo = new HashMap<>();
            cachePeriodoBeneficio = new HashMap<>();
        }
    }

    public void reset() {
        synchronized (this) {
            dataLimite = null;
            cachePeriodo.clear();
            cachePeriodoBeneficio.clear();
        }
    }

    private void atualizarCache(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        if ((dataLimite == null) || dataLimite.before(DateHelper.getSystemDatetime()) ||
                ((orgCodigo != null) && !cachePeriodo.containsKey(orgCodigo))) {
            synchronized (this) {
                if ((dataLimite == null) || dataLimite.before(DateHelper.getSystemDatetime()) ||
                        ((orgCodigo != null) && !cachePeriodo.containsKey(orgCodigo))) {
                    final PeriodoDelegate perDelegate = new PeriodoDelegate();
                    List<TransferObject> datasPeriodoList = perDelegate.obtemPeriodoAtual(null, null, responsavel);
                    carregaMapPeriodo(datasPeriodoList, cachePeriodo, Columns.PEX_ORG_CODIGO, Columns.PEX_PERIODO, Columns.PEX_DATA_INI, Columns.PEX_DATA_FIM, "DATA_PREVISTA_RETORNO", Columns.PEX_DIA_CORTE, responsavel);

                    // Se o modulo beneficio esta ativado vamos calcular, evitando assim um PeriodoException invalido.
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)) {
                        datasPeriodoList = perDelegate.obtemPeriodoBeneficioAtual(null, null, responsavel);
                        if ((datasPeriodoList != null) && !datasPeriodoList.isEmpty()) {
                            carregaMapPeriodo(datasPeriodoList, cachePeriodoBeneficio, Columns.ORG_CODIGO, Columns.PBE_PERIODO, Columns.PBE_DATA_INI, Columns.PBE_DATA_FIM, "DATA_PREVISTA_RETORNO", Columns.PBE_DIA_CORTE, responsavel);
                        }
                    }

                    // Define a data limite: até a meia note da data atual
                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    cal.set(Calendar.MILLISECOND, 999);
                    dataLimite = cal.getTime();
                }
            }
        }
    }


    /**
     * Metodo generico para carregar os Map com os pariodos atuais.
     * Usado para carregado o Map do periodo consignado e beneficio.
     * @param datasPeriodoList
     * @return
     * @throws PeriodoException
     */
    private synchronized void carregaMapPeriodo(List<TransferObject> datasPeriodoList,Map<String, DadosPeriodo> cacheEntrada,
            String ColumnsOrg, String ColumnsPeriodo, String ColumnsDataIni, String ColumnsDataFim, String columnDataPrevistaRetorno, String DiaCorte, AcessoSistema responsavel) throws PeriodoException {
        if ((datasPeriodoList != null) && (!datasPeriodoList.isEmpty())) {
            // Cria estruturas para verificar quais são os objetos com maior quantidade
            final Map<java.sql.Date, Integer> periodoPorQtd = new HashMap<>();
            final Map<Date, Integer> dataIniPorQtd = new HashMap<>();
            final Map<Date, Integer> dataFimPorQtd = new HashMap<>();
            final Map<Date, Integer> dataPrevistaRetornoPorQtd = new HashMap<>();
            final Map<Short, Integer> diaCortePorQtd = new HashMap<>();

            // DESENV-14829 - Foi identificado uma necessidade de setar o cacheEntrada com chave null com as informações do
            // calendário da cse quando ele existir.
            boolean calendarioFolhaCseExiste = false;

            for (final TransferObject calendarioTO : datasPeriodoList) {
                final String orgCodigo = (String) calendarioTO.getAttribute(ColumnsOrg);
                final java.sql.Date periodo = DateHelper.toSQLDate((Date) calendarioTO.getAttribute(ColumnsPeriodo));
                final Date dataIni = (Date) calendarioTO.getAttribute(ColumnsDataIni);
                final Date dataFim = (Date) calendarioTO.getAttribute(ColumnsDataFim);
                final Date dataPrevistaRetorno = (Date) calendarioTO.getAttribute(columnDataPrevistaRetorno);
                final Short diaCorte = (Short) calendarioTO.getAttribute(DiaCorte);
                final String cfcSistema = (String) calendarioTO.getAttribute("CORTE_SISTEMA");
                if (!TextHelper.isNull(cfcSistema) && "S".equals(cfcSistema)) {
                    calendarioFolhaCseExiste = true;
                    cacheEntrada.computeIfAbsent(null, k -> new DadosPeriodo(periodo, dataIni, dataFim, dataPrevistaRetorno, diaCorte));
                }

                cacheEntrada.put(orgCodigo, new DadosPeriodo(periodo, dataIni, dataFim, dataPrevistaRetorno, diaCorte));

                // Atualiza os contadores por objeto
                periodoPorQtd.put(periodo, (!periodoPorQtd.containsKey(periodo) ? 1 : periodoPorQtd.get(periodo) + 1));
                dataIniPorQtd.put(dataIni, (!dataIniPorQtd.containsKey(dataIni) ? 1 : dataIniPorQtd.get(dataIni) + 1));
                dataFimPorQtd.put(dataFim, (!dataFimPorQtd.containsKey(dataFim) ? 1 : dataFimPorQtd.get(dataFim) + 1));
                dataPrevistaRetornoPorQtd.put(dataPrevistaRetorno, (!dataPrevistaRetornoPorQtd.containsKey(dataPrevistaRetorno) ? 1 : dataPrevistaRetornoPorQtd.get(dataPrevistaRetorno) + 1));
                diaCortePorQtd.put(diaCorte, (!diaCortePorQtd.containsKey(diaCorte) ? 1 : diaCortePorQtd.get(diaCorte) + 1));
            }

            // Seta chave nula no cache com o objeto que aparece em maior quantidade, assim
            // caso os métodos sejam chamados sem informação de órgão, terão a informação que
            // é mais frequente dentre todos os órgãos.
            if (!calendarioFolhaCseExiste) {
                Map.Entry<java.sql.Date, Integer> periodoMaisFreq = null;
                for (final Map.Entry<java.sql.Date, Integer> entry : periodoPorQtd.entrySet()) {
                    if ((periodoMaisFreq == null) || (entry.getValue().compareTo(periodoMaisFreq.getValue()) > 0)) {
                        periodoMaisFreq = entry;
                    }
                }

                Map.Entry<Date, Integer> dataIniMaisFreq = null;
                for (final Map.Entry<Date, Integer> entry : dataIniPorQtd.entrySet()) {
                    if ((dataIniMaisFreq == null) || (entry.getValue().compareTo(dataIniMaisFreq.getValue()) > 0)) {
                        dataIniMaisFreq = entry;
                    }
                }

                Map.Entry<Date, Integer> dataFimMaisFreq = null;
                for (final Map.Entry<Date, Integer> entry : dataFimPorQtd.entrySet()) {
                    if ((dataFimMaisFreq == null) || (entry.getValue().compareTo(dataFimMaisFreq.getValue()) > 0)) {
                        dataFimMaisFreq = entry;
                    }
                }

                Map.Entry<Date, Integer> dataPrevistaRetornoMaisFreq = null;
                for (final Map.Entry<Date, Integer> entry : dataPrevistaRetornoPorQtd.entrySet()) {
                    if ((dataPrevistaRetornoMaisFreq == null) || (entry.getValue().compareTo(dataPrevistaRetornoMaisFreq.getValue()) > 0)) {
                        dataPrevistaRetornoMaisFreq = entry;
                    }
                }

                Map.Entry<Short, Integer> diaCorteMaisFreq = null;
                for (final Map.Entry<Short, Integer> entry : diaCortePorQtd.entrySet()) {
                    if ((diaCorteMaisFreq == null) || (entry.getValue().compareTo(diaCorteMaisFreq.getValue()) > 0)) {
                        diaCorteMaisFreq = entry;
                    }
                }
                cacheEntrada.put(null, new DadosPeriodo(periodoMaisFreq.getKey(), dataIniMaisFreq.getKey(), dataFimMaisFreq.getKey(), dataPrevistaRetornoMaisFreq.getKey(), diaCorteMaisFreq.getKey()));
            }
        } else {
            throw new PeriodoException("mensagem.erro.periodo.atual.verificar.cadastro", responsavel);
        }
    }

    /**
     * Retorna o próximo dia de corte, ou seja quando será encerrado o período
     * atual de lançamentos.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public int getProximoDiaCorte(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.dia.corte.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).diaCorte;
    }

    /**
     * Retorna o periodo atual de lançamento.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date getPeriodoAtual(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).periodo;
    }

    /**
     * Retorna a data de retorno prevista.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Date getDataRetornoPrevista(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).dataPrevistaRetorno;
    }

    /**
     * Retorna o primeiro periodo que permite uma inclusão.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date getPeriodoAtualInclusao(String orgCodigo, String periodicidade, AcessoSistema responsavel) throws PeriodoException {
    	final PeriodoDelegate perDelegate = new PeriodoDelegate();

    	if (periodicidade == null) {
    	    periodicidade = getPeriodicidadeFolha(responsavel);
    	}

        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.verificar.cadastro", responsavel);
        }
        java.sql.Date periodo = cachePeriodo.get(orgCodigo).periodo;
        while (perDelegate.periodoPermiteApenasReducoes(periodo, orgCodigo, responsavel)) {
            periodo = calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(periodo), 1, periodicidade, responsavel);
        }
        return periodo;
    }

    /**
     * Retorna o período anterior, ou seja, um mês antes do atual de lançamento.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date getPeriodoAnterior(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        final java.sql.Date periodoAtual = getPeriodoAtual(orgCodigo, responsavel);

        if (!folhaMensal(responsavel)) {
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            return DateHelper.toSQLDate(perDelegate.obtemPeriodoAposPrazo(orgCodigo, -1, periodoAtual, false, responsavel));
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(periodoAtual);
            cal.add(Calendar.MONTH, -1);
            return DateHelper.toSQLDate(cal.getTime());
        }
    }

    /**
     * Retorna a data inicial do período atual de lançamento
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Date getDataIniPeriodoAtual(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.data.inicial.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).dataIni;
    }

    /**
     * Retorna a data final do período atual de lançamento
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Date getDataFimPeriodoAtual(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.data.fim.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).dataFim;
    }

    /**
     * Calcula a data inicial de um contrato, baseado no período atual
     * de lançamento e da carência informada.
     * @param orgCodigo
     * @param adeCarencia
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date calcularAdeAnoMesIni(String orgCodigo, Integer adeCarencia, String adePeriodicidade, AcessoSistema responsavel) throws PeriodoException {
        return calcularAdeAnoMesIni(orgCodigo, getPeriodoAtual(orgCodigo, responsavel), adeCarencia, adePeriodicidade, responsavel);
    }

    public java.sql.Date calcularAdeAnoMesIni(String orgCodigo, java.sql.Date periodoAtual, Integer adeCarencia, String adePeriodicidade, AcessoSistema responsavel) throws PeriodoException {
        if (periodoAtual == null) {
            periodoAtual = getPeriodoAtual(orgCodigo, responsavel);
        }
        if ((adeCarencia == null) || (adeCarencia <= 0)) {
            return periodoAtual;

        } else if (!folhaMensal(responsavel)) {

            // Em sistemas com agrupamento de período (TPC 492 = S) em que não permite escolha do período (TPC 513 = N), a carência
            // deve começar a contar após o primeiro período fora do agrupamento, como se todos fossem um único período.
            boolean ignoraPeriodosAgrupados = false;
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                    !ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) {
                ignoraPeriodosAgrupados = true;
            }

            // Se no sistema Quinzenal a periodicidade do contrato é Mensal, então multiplica por 2 já que o padrão do sistema é ter duas quinzenas por mês
            adeCarencia = (CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade) ? adeCarencia * 2 : adeCarencia);
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            return DateHelper.toSQLDate(perDelegate.obtemPeriodoAposPrazo(orgCodigo, adeCarencia, periodoAtual, ignoraPeriodosAgrupados, responsavel));

        } else {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(periodoAtual);
            cal.add(Calendar.MONTH, adeCarencia);
            return DateHelper.toSQLDate(cal.getTime());
        }
    }

    /**
     * Calcula a data final de um contrato baseado na data inicial e prazo.
     * @param orgCodigo
     * @param adeAnoMesIni
     * @param adePrazo
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date calcularAdeAnoMesFim(String orgCodigo, Date adeAnoMesIni, Integer adePrazo, String adePeriodicidade, AcessoSistema responsavel) throws PeriodoException {
        if ((adePrazo == null) || (adePrazo <= 0)) {
            return null;

        } else if (adePrazo == 1) {
            return DateHelper.toSQLDate(adeAnoMesIni);

        } else if (!folhaMensal(responsavel)) {
            // Se no sistema Quinzenal a periodicidade do contrato é Mensal, então multiplica por 2 já que o padrão do sistema é ter duas quinzenas por mês
            adePrazo = (CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade) ? (adePrazo - 1) * 2 : adePrazo - 1);
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            return DateHelper.toSQLDate(perDelegate.obtemPeriodoAposPrazo(orgCodigo, adePrazo, adeAnoMesIni, false, responsavel));

        } else {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(adeAnoMesIni);
            cal.add(Calendar.MONTH, adePrazo - 1);
            return DateHelper.toSQLDate(cal.getTime());
        }
    }

    /**
     * Verifica se o "adeAnoMesIni" informado permite inclusões ou alterações para maior, e caso não
     * permita, retorna o próximo período que permite tal operação.
     * @param orgCodigo
     * @param adeAnoMesIni
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date validarAdeAnoMesIni(String orgCodigo, java.sql.Date adeAnoMesIni, AcessoSistema responsavel) throws PeriodoException {
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
            final String adePeriodicidade = getPeriodicidadeFolha(responsavel);
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            while (perDelegate.periodoPermiteApenasReducoes(adeAnoMesIni, orgCodigo, responsavel)) {
                // Soma a data inicial à carência de 1 período (seja mensal ou quinzenal)
                adeAnoMesIni = calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(adeAnoMesIni), 1, adePeriodicidade, responsavel);
            }
        }
        return adeAnoMesIni;
    }

    /**
     * Calcula o prazo de acordo com a data inicial e final do contrato
     * @param orgCodigo
     * @param adeAnoMesIni
     * @param adeAnoMesFim
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Integer calcularPrazo(String orgCodigo, Date adeAnoMesIni, Date adeAnoMesFim, String adePeriodicidade, AcessoSistema responsavel) throws PeriodoException {
        if (adeAnoMesFim == null) {
            // Se não tem período final, prazo é indeterminado
            return null;

        } else if (DateUtils.isSameDay(adeAnoMesIni, adeAnoMesFim)) {
            // Se o período inicial e final são o mesmo dia, então o prazo é 1
            return 1;

        } else if (adeAnoMesIni.after(adeAnoMesFim)) {
            // Se o período inicial é maior que o final, retorna Zero pois no cálculo
            // de carência, caso a data inicial seja anterior ao período atual, a carência é zero.
            return 0;

        } else if (!folhaMensal(responsavel)) {
            // Em qualquer outro caso, se periodicidade quinzenal utiliza o calendário
            // folha para verificar quandos períodos existem entre as duas datas.
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            Integer adePrazo = perDelegate.obtemPrazoEntrePeriodos(orgCodigo, adeAnoMesIni, adeAnoMesFim, responsavel);

            return (CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade) ? Math.round(adePrazo / 2.0f) : adePrazo);

        } else {
            // Se periodicidade mensal, realiza a diferença entre as duas datas
            return DateHelper.dateDiff(DateHelper.format(adeAnoMesIni, "yyyy-MM-dd"), DateHelper.format(adeAnoMesFim, "yyyy-MM-dd"), "yyyy-MM-dd", null, "PRAZO");
        }
    }

    /**
     * Calcula a carência de um contrato de acordo com a data inicial informada,
     * comparada com o período atual de lançamentos.
     * @param orgCodigo
     * @param adeAnoMesIni
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Integer calcularCarencia(String orgCodigo, Date adeAnoMesIni, String adePeriodicidade, AcessoSistema responsavel) throws PeriodoException {
        final Date periodoAtual = getPeriodoAtual(orgCodigo, responsavel);
        Integer prazoCarencia = calcularPrazo(orgCodigo, periodoAtual, adeAnoMesIni, adePeriodicidade, responsavel);
        // Se o prazo for maior que Zero, subtrai 1 pois caso a data inicial e o período atual
        // sejam iguais, o método de prazo irá retornar prazo = 1, porém neste caso a carência é zero.
        if ((prazoCarencia != null) && (prazoCarencia > 0)) {
            prazoCarencia--;
        } else {
            prazoCarencia = 0;
        }
        return prazoCarencia;
    }

    /**
     * Calcula a carência de um contrato de acordo com a data inicial informada,
     * comparada com o período atual de lançamentos, considerando o primeiro período que pode inclusões.
     * @param orgCodigo
     * @param adeAnoMesIni
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Integer calcularCarenciaInclusao(String orgCodigo, Date adeAnoMesIni, String adePeriodicidade, AcessoSistema responsavel) throws PeriodoException {
        final Date periodoAtual = getPeriodoAtualInclusao(orgCodigo, adePeriodicidade, responsavel);
        Integer prazoCarencia = calcularPrazo(orgCodigo, periodoAtual, adeAnoMesIni, adePeriodicidade, responsavel);
        // Se o prazo for maior que Zero, subtrai 1 pois caso a data inicial e o período atual
        // sejam iguais, o método de prazo irá retornar prazo = 1, porém neste caso a carência é zero.
        if ((prazoCarencia != null) && (prazoCarencia > 0)) {
            prazoCarencia--;
        } else {
            prazoCarencia = 0;
        }
        return prazoCarencia;
    }

    /** Meotodo para o modulo beneficio **/

    /**
     * Retorna o próximo dia de corte, ou seja quando será encerrado o período
     * atual de lançamentos beneficio.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public int getProximoDiaCorteBeneficio(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodoBeneficio.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.dia.corte.verificar.cadastro", responsavel);
        }
        return cachePeriodoBeneficio.get(orgCodigo).diaCorte;
    }

    /**
     * Retorna o periodo atual de lançamento beneficio.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date getPeriodoBeneficioAtual(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodoBeneficio.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.verificar.cadastro", responsavel);
        }
        return cachePeriodoBeneficio.get(orgCodigo).periodo;
    }

    /**
     * Retorna a data inicial do período atual de lançamento beneficio.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Date getDataIniPeriodoBeneficioAtual(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.data.inicial.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).dataIni;
    }

    /**
     * Retorna a data final do período atual de lançamento beneficio.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public Date getDataFimPeriodoBeneficioAtual(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        atualizarCache(orgCodigo, responsavel);
        if (!cachePeriodo.containsKey(orgCodigo)) {
            throw new PeriodoException("mensagem.erro.periodo.data.fim.verificar.cadastro", responsavel);
        }
        return cachePeriodo.get(orgCodigo).dataFim;
    }

    /**
     * Retorna o período beneficio anterior, ou seja, um mês antes do atual de lançamento.
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    public java.sql.Date getPeriodoBeneficioAnterior(String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        final java.sql.Date periodoAtual = getPeriodoBeneficioAtual(orgCodigo, responsavel);

        if (!folhaMensal(responsavel)) {
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            return DateHelper.toSQLDate(perDelegate.obtemPeriodoBeneficioAposPrazo(orgCodigo, -1, periodoAtual, false, responsavel));
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(periodoAtual);
            cal.add(Calendar.MONTH, -1);
            return DateHelper.toSQLDate(cal.getTime());
        }
    }

    /**
     * Adiciona ou subtrai períodos a partir de uma data base
     * @param dataBase
     * @param qtdPeriodos
     * @return
     * @throws PeriodoException
     */
    public Date adicionarPeriodoQuinzenal(Date dataBase, int qtdPeriodos)  throws PeriodoException{
        final Calendar cal = new GregorianCalendar();
        cal.setTime(dataBase);

        if(qtdPeriodos > 0) {
            while(qtdPeriodos != 0) {
                if(cal.get(Calendar.DAY_OF_MONTH) == 1) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }else if (cal.get(Calendar.DAY_OF_MONTH) == 2) {
                    cal.add(Calendar.MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                }
                qtdPeriodos--;
            }
        }else if(qtdPeriodos < 0){
            while((qtdPeriodos*-1) != 0) {
                if(cal.get(Calendar.DAY_OF_MONTH) == 1) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.MONTH, -1);
                }else if (cal.get(Calendar.DAY_OF_MONTH) == 2) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                }
                qtdPeriodos++;
            }
        }
        return cal.getTime();
    }

    /**
     * Classe utilizada para armazenar os dados do período em cache
     * evitando recuperá-los sempre que necessário.
     */
    private static class DadosPeriodo implements Serializable {
        private static final long serialVersionUID = 1L;
        java.sql.Date periodo;
        Date dataIni;
        Date dataFim;
        Date dataPrevistaRetorno;
        Short diaCorte;

        DadosPeriodo(java.sql.Date periodo, Date dataIni, Date dataFim, Date dataPrevistaRetorno, Short diaCorte) {
            this.periodo = periodo;
            this.dataIni = dataIni;
            this.dataFim = dataFim;
            this.diaCorte = diaCorte;
            this.dataPrevistaRetorno = dataPrevistaRetorno;
        }
    }

    /**
     * Retorna TRUE se a periodicidade da folha é mensal ou é qualquer outro valor
     * não esperado pelo sistema, incluindo nulo/vazio.
     * @param responsavel
     * @return
     */
    public static boolean folhaMensal(AcessoSistema responsavel) {
        return CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(getPeriodicidadeFolha(responsavel));
    }

    /**
     * Retorna TRUE se a periodicidade informada é mensal ou é qualquer outro valor
     * não esperado pelo sistema, incluindo nulo/vazio.
     * @param adePeriodicidade
     * @param responsavel
     * @return
     */
    public static boolean isMensal(String adePeriodicidade, AcessoSistema responsavel) {
        if (TextHelper.isNull(adePeriodicidade)) {
            adePeriodicidade = getPeriodicidadeFolha(responsavel);
        }
        return CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade);
    }

    /**
     * Retorna a periodicidade da folha. Trata casos onde o valor do parâmetro de sistema não está
     * preenchido ou está preenchido com valores inválidos, sendo portanto a melhor forma de obter
     * esta configuração.
     * @param responsavel
     * @return
     */
    public static String getPeriodicidadeFolha(AcessoSistema responsavel) {
        String periodicidade = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERIODICIDADE_FOLHA, responsavel);
        // Se não tem periodicidade definida, ou o valor é diferente dos esperados pelo sistema, então será mensal
        if (TextHelper.isNull(periodicidade) || (
                !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(periodicidade) &&
                !CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(periodicidade) &&
                !CodedValues.PERIODICIDADE_FOLHA_QUATORZENAL.equals(periodicidade) &&
                !CodedValues.PERIODICIDADE_FOLHA_SEMANAL.equals(periodicidade))) {
            periodicidade = CodedValues.PERIODICIDADE_FOLHA_MENSAL;
            LOG.warn(String.format("Parâmetro de sistema com periodicidade da folha '{}' não setado. Será assumido como Mensal.", CodedValues.TPC_PERIODICIDADE_FOLHA));
        }
        return periodicidade;
    }

    /**
     * Determina a quantidade de períodos folha com base na periodicidade
     * @param responsavel
     * @return
     */
    public static int getQuantidadePeriodosFolha(AcessoSistema responsavel) {
        final String periodicidade = getPeriodicidadeFolha(responsavel);

        int qtdPeriodos = 12;
        switch (periodicidade) {
            case CodedValues.PERIODICIDADE_FOLHA_MENSAL:
                qtdPeriodos = 12;
                break;
            case CodedValues.PERIODICIDADE_FOLHA_QUINZENAL:
                qtdPeriodos = 24;
                break;
            case CodedValues.PERIODICIDADE_FOLHA_QUATORZENAL:
                qtdPeriodos = 26;
                break;
            case CodedValues.PERIODICIDADE_FOLHA_SEMANAL:
                qtdPeriodos = 52;
                break;
        }

        return qtdPeriodos;
    }

    /**
     * Cálculo de prazos limites, que são cadastrados em meses, para que seja compatível com a quantidade
     * de períodos da folha. O cálculo segue a fórmula:
     *
     *   prazo_na_periodicidade = round(qtd_periodos_ano / 12 * prazo_em_meses)
     *
     * OBS: deve ser usado 12.0f para representar um número de ponto flutuante (float)
     * de modo que toda a operação dentro do round seja feito nesta precisão, e só
     * depois seja feito o arredontamento.
     *
     * @param prazoMeses
     * @param responsavel
     * @return
     */
    public static Integer converterPrazoMensalEmPeriodicidade(Integer prazoMeses, AcessoSistema responsavel) {
        if (prazoMeses == null) {
            return null;
        }
        final int qtdPeriodos = getQuantidadePeriodosFolha(responsavel);
        return Math.round((qtdPeriodos / 12.0f) * prazoMeses);
    }

    public static Short converterPrazoMensalEmPeriodicidade(Short prazoMeses, AcessoSistema responsavel) {
        if (prazoMeses == null) {
            return null;
        }
        return converterPrazoMensalEmPeriodicidade(prazoMeses.intValue(), responsavel).shortValue();
    }

    /**
     * Reverte um prazo na periodicidade da folha para o valor mensal. É usado por exemplo para
     * utilização da taxa cadastrada no serviço que fica associada ao prazo mensal.
     *
     * @param prazo
     * @param responsavel
     * @return
     */
    public static Integer reverterPrazoPeriodicidadeParaMensal(Integer prazo, AcessoSistema responsavel) {
        if (prazo == null) {
            return null;
        }
        final int qtdPeriodos = getQuantidadePeriodosFolha(responsavel);
        return Math.round((prazo * 12.0f) / qtdPeriodos);
    }

    public static Short reverterPrazoPeriodicidadeParaMensal(Short prazo, AcessoSistema responsavel) {
        if (prazo == null) {
            return null;
        }
        return reverterPrazoPeriodicidadeParaMensal(prazo.intValue(), responsavel).shortValue();
    }

    /**
     * Transforma uma lista de prazos mensais, como por exemplo os prazos permitidos de um serviço,
     * para uma lista de prazos na periodicidade da folha.
     * @param prazoList
     * @param responsavel
     * @return
     */
    public static Set<Integer> converterListaPrazoMensalEmPeriodicidade(List<? extends TransferObject> prazoList, AcessoSistema responsavel) {
        final boolean mensal = folhaMensal(responsavel);
        final Set<Integer> prazosPeriodicidade = new TreeSet<>();

        for (final TransferObject prazoTO : prazoList) {
            final int prazo = Integer.parseInt(prazoTO.getAttribute(Columns.PRZ_VLR).toString());

            if (mensal) {
                prazosPeriodicidade.add(prazo);
            } else {
                final int prazoMenosUmPeriodicidade = converterPrazoMensalEmPeriodicidade(prazo - 1, responsavel);
                final int prazoPeriodicidade = converterPrazoMensalEmPeriodicidade(prazo, responsavel);
                for (int i = prazoMenosUmPeriodicidade + 1; i <= prazoPeriodicidade; i++) {
                    prazosPeriodicidade.add(i);
                }
            }
        }

        return prazosPeriodicidade;
    }

    /**
     * Converte o número do período na configuração mes-dia de acordo com a periodicidade da folha
     * @param numPeriodo
     * @param responsavel
     * @return
     */
    public static String converterNumPeriodoParaMesDia(int numPeriodo, AcessoSistema responsavel) {
        final String periodicidade = getPeriodicidadeFolha(responsavel);
        int mes = numPeriodo;
        int dia = 1;

        if (CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(periodicidade) || CodedValues.PERIODICIDADE_FOLHA_QUATORZENAL.equals(periodicidade)) {
            dia = (numPeriodo % 2) == 0 ? 2 : 1;
            mes = Math.round(numPeriodo / 2.0f);
            if (numPeriodo > 24) {
                dia += 2;
                mes = 12;
            }
        } else if (CodedValues.PERIODICIDADE_FOLHA_SEMANAL.equals(periodicidade)) {
            dia = (numPeriodo % 4) == 0 ? 4 : numPeriodo % 4;
            mes = Math.round(numPeriodo / 4.0f) + (dia == 1 ? 1 : 0);
            if (numPeriodo > 48) {
                dia += 4;
                mes = 12;
            }
        }

        return String.format("%02d", mes) + "-" + String.format("%02d", dia);
    }

    /**
     * Converte o número do período em um ano específico para um objeto de data que
     * representa o período de acordo com a periodicidade da folha
     * @param numPeriodo
     * @param ano
     * @param responsavel
     * @return
     */
    public static Date converterNumPeriodoNoAnoParaPeriodo(int numPeriodo, int ano, AcessoSistema responsavel) {
        try {
            final String mesDia = converterNumPeriodoParaMesDia(numPeriodo, responsavel);
            final String anoMesDia = String.format("%04d", ano) + "-" + mesDia;
            return DateHelper.clearHourTime(DateHelper.parse(anoMesDia, "yyyy-MM-dd"));
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }
}
