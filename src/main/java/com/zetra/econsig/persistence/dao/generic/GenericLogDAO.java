package com.zetra.econsig.persistence.dao.generic;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.LogDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericLogDAO</p>
 * <p>Description: Implementacao Genérica do DAO de Log. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericLogDAO implements LogDAO {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericLogDAO.class);

    private static final String PARAM_NOME_TABELA = "NOME_TABELA";
    private static final String PARAM_LOG_DATA_INI = "LOG_DATA_INI";
    private static final String PARAM_LOG_DATA_FIM = "LOG_DATA_FIM";

    private static final String PERIODICIDADE_LOG_ANUAL = "A";
    private static final String PERIODICIDADE_LOG_SEMESTRAL = "S";
    private static final String PERIODICIDADE_LOG_TRIMESTRAL = "T";

    private void criaHistoricoLog(Date dataInicio, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final String geraLogPeriodicidade = (String) ParamSist.getInstance().getParam(CodedValues.TPC_GERA_TABELA_LOG_PERIODICIDADE, responsavel);
            if (TextHelper.isNull(geraLogPeriodicidade)) {
                throw new DAOException("mensagem.erro.historico.log.tabela.nao.gerada", responsavel);
            }

            final Map<String, Object> param = recuperaParamHistoricoLog(dataInicio, geraLogPeriodicidade);

            final String nomeTabela = (String) param.get(PARAM_NOME_TABELA);
            final Date logDataIni = (Date) param.get(PARAM_LOG_DATA_INI);
            final Date logDataFim = (Date) param.get(PARAM_LOG_DATA_FIM);

            queryParams.addValue("logDataIni", logDataIni);
            queryParams.addValue("logDataFim", logDataFim);

            // Cria a tabela de histórico de log
            criaTabelaHistoricoLog(nomeTabela);

            // Quantidade de registros que serão migrados para a tabela de histórico
            final StringBuilder query = new StringBuilder();
            query.append(" SELECT count(*) AS QTDE ");
            query.append(" FROM ").append(Columns.TB_LOG);
            query.append(" WHERE 1 = 1 ");
            query.append(" AND LOG_DATA  >= :logDataIni ");
            query.append(" AND LOG_DATA  <= :logDataFim ");
            LOG.trace(query.toString());
            final Integer count = jdbc.queryForObject(query.toString(), queryParams, Integer.class);

            if (count != null) {
                LOG.debug("Quantidade de logs que serão migrados: " + count);
                if (count == 0) {
                    // Se não tem nada para migrar, então termina a rotina
                    return;
                }
            }

            // Migra os registros para a tabela de histórico
            query.setLength(0);
            query.append(" INSERT INTO ").append(nomeTabela).append(" ");
            query.append(" (TLO_CODIGO, TEN_CODIGO, USU_CODIGO, FUN_CODIGO, LOG_DATA, LOG_OBS, LOG_IP, LOG_COD_ENT_00, LOG_COD_ENT_01, LOG_COD_ENT_02, LOG_COD_ENT_03, LOG_COD_ENT_04, LOG_COD_ENT_05, LOG_COD_ENT_06, LOG_COD_ENT_07, LOG_COD_ENT_08, LOG_COD_ENT_09, LOG_COD_ENT_10, LOG_CANAL, LOG_PORTA) ");
            query.append(" SELECT TLO_CODIGO, TEN_CODIGO, USU_CODIGO, FUN_CODIGO, LOG_DATA, LOG_OBS, LOG_IP, LOG_COD_ENT_00, LOG_COD_ENT_01, LOG_COD_ENT_02, LOG_COD_ENT_03, LOG_COD_ENT_04, LOG_COD_ENT_05, LOG_COD_ENT_06, LOG_COD_ENT_07, LOG_COD_ENT_08, LOG_COD_ENT_09, LOG_COD_ENT_10, LOG_CANAL, LOG_PORTA ");
            query.append(" FROM ").append(Columns.TB_LOG);
            query.append(" WHERE 1 = 1 ");
            query.append(" AND LOG_DATA  >= :logDataIni ");
            query.append(" AND LOG_DATA  <= :logDataFim ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Exclui os registros migrados da tabela de log
            query.setLength(0);
            query.append(" DELETE ");
            query.append(" FROM ").append(Columns.TB_LOG);
            query.append(" WHERE 1 = 1 ");
            query.append(" AND LOG_DATA  >= :logDataIni ");
            query.append(" AND LOG_DATA  <= :logDataFim ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Exclui o antigo histórico de arquivamento de log
            query.setLength(0);
            query.append("DELETE FROM tb_historico_arquivamento_log WHERE HAL_NOME_TABELA = :nomeTabela");
            queryParams.addValue("nomeTabela", nomeTabela);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Verifica quantidade de registros pós arquivamento
            query.setLength(0);
            query.append("SELECT COUNT(*) FROM ").append(nomeTabela);
            LOG.trace(query.toString());
            final Integer newCount = jdbc.queryForObject(query.toString(), queryParams, Integer.class);
            queryParams.addValue("total", newCount);
            queryParams.addValue("dataAtual", DateHelper.getSystemDatetime());

            // Inclui o novo histórico de arquivamento de log
            query.setLength(0);
            query.append("INSERT INTO tb_historico_arquivamento_log (HAL_NOME_TABELA, HAL_DATA, HAL_DATA_INI_LOG, HAL_DATA_FIM_LOG, HAL_QTD_REGISTROS) ");
            query.append("VALUES (:nomeTabela, :dataAtual, :logDataIni, :logDataFim, :total)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);            

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Recupera um mapeamento de parâmetros para gerar o histórico do log.
     * PARAM_NOME_TABELA  = Nome da tabela que armazenará o log do período.
     * PARAM_LOG_DATA_INI = Data inicial do log que deverá ser armazenado.
     * PARAM_LOG_DATA_FIM = Data final do log que deverá ser armazenado.
     *
     * @param dataInicio Será utilizada somente no caso para gerar histórico antigo de log.
     * @param geraLogPeriodicidade Periodicidade de armazenamento para o log.
     * @return Retorna um mapeamento de parâmetros para gerar o histórico do log.
     */
    private Map<String, Object> recuperaParamHistoricoLog(Date dataInicio, String geraLogPeriodicidade) {
        Map<String, Object> retorno = new HashMap<String, Object>();

        String nomeTabela = "tb_log";
        Date logDataIni = null;
        Date logDataFim = null;
        Calendar calendar = Calendar.getInstance();

        int mes = 0;
        if (dataInicio != null) {
            calendar.setTime(dataInicio);
            mes = calendar.get(Calendar.MONTH);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            logDataFim = calendar.getTime();
        } else {
            mes = calendar.get(Calendar.MONTH);
            int dias = getQtdDiasPeriodo(mes, calendar.get(Calendar.YEAR), geraLogPeriodicidade);
            LOG.debug("DIAS: " + dias);

            calendar.add(Calendar.DAY_OF_YEAR, dias*-1);
            logDataFim = calendar.getTime();

        }
        LOG.debug("Hoje: " + calendar.getTime());

        // Seta para o primeiro dia do mês para recuperar a data inicial
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        if (geraLogPeriodicidade.equals(PERIODICIDADE_LOG_ANUAL)) {
            nomeTabela += "_" + (calendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            logDataIni = calendar.getTime();

        } else if (geraLogPeriodicidade.equals(PERIODICIDADE_LOG_SEMESTRAL)) {
            if (mes < 6) {
                nomeTabela += "_" + (calendar.get(Calendar.YEAR)) + "_s2";
                calendar.set(Calendar.MONTH, Calendar.JULY);
                logDataIni = calendar.getTime();

            } else {
                nomeTabela += "_" + (calendar.get(Calendar.YEAR)) + "_s1";
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                logDataIni = calendar.getTime();
            }

        } else if (geraLogPeriodicidade.equals(PERIODICIDADE_LOG_TRIMESTRAL)) {
            if (mes <= Calendar.MARCH) {
                nomeTabela += "_" + (calendar.get(Calendar.YEAR)) + "_t4";
                calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                logDataIni = calendar.getTime();

            } else if (mes <= Calendar.JUNE) {
                nomeTabela += "_" + (calendar.get(Calendar.YEAR)) + "_t1";
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                logDataIni = calendar.getTime();

            } else if (mes <= Calendar.SEPTEMBER) {
                nomeTabela += "_" + (calendar.get(Calendar.YEAR)) + "_t2";
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                logDataIni = calendar.getTime();

            } else if (mes <= Calendar.DECEMBER) {
                nomeTabela += "_" + (calendar.get(Calendar.YEAR)) + "_t3";
                calendar.set(Calendar.MONTH, Calendar.JULY);
                logDataIni = calendar.getTime();
            }
        }

        // Data de início para a hora Zero
        logDataIni = DateHelper.clearHourTime(logDataIni);
        // Data fim para a hora 23:59:59
        logDataFim = DateHelper.getEndOfDay(logDataFim);

        LOG.debug("Tabela: " + nomeTabela);
        LOG.debug("Data Inicio: " + logDataIni);
        LOG.debug("Data Fim: " + logDataFim);

        retorno.put(PARAM_NOME_TABELA, nomeTabela);
        retorno.put(PARAM_LOG_DATA_INI, logDataIni);
        retorno.put(PARAM_LOG_DATA_FIM, logDataFim);

        return retorno;
    }

    /**
     * Verdade <code>true</code> caso o ano passado seja bissexto.
     *
     * @param ano Ano que será verificado se é ano bissexto.
     * @return Verdade <code>true</code> caso o ano passado seja bissexto.
     */
    private static boolean isBissexto(int ano) {
        return new GregorianCalendar().isLeapYear(ano);
    }

    /**
     * Retorna a quantidade de dias do periodo desejado, levando em consideração se é um ano bissexto.
     *
     * @param mes Mês a partir do qual deverá ser contada a quantidade de dias do período.
     * @param ano Ano base para o cálculo.
     * @param geraLogPeriodicidade Periodo que deverá ser contado.
     * @return Quantidade de dias do periodo desejado
     */
    private static int getQtdDiasPeriodo(int mes, int ano, String geraLogPeriodicidade) {

        int diasMes[] = {31, 28, 31, 30, /* jan fev mar abr */
                         31, 30, 31, 31, /* mai jun jul ago */
                         30, 31, 30, 31 /* set out nov dez */
                        };

        if (isBissexto(ano) && mes > Calendar.JANUARY) {
            diasMes[1] = 29;
        }

        int contador = 0;
        int periodo = 0;
        if (geraLogPeriodicidade.equals(PERIODICIDADE_LOG_ANUAL)) {
            periodo = 12;
        } else if (geraLogPeriodicidade.equals(PERIODICIDADE_LOG_SEMESTRAL)) {
            periodo = 6;
        } else if (geraLogPeriodicidade.equals(PERIODICIDADE_LOG_TRIMESTRAL)) {
            periodo = 3;
        }

        int qtdDias = 0;
        int index = mes;
        while (contador < periodo) {
            qtdDias += diasMes[index];
            if (index > 0) {
                index--;
            } else {
                index = 11;
            }

            contador++;
        }

        return qtdDias;

    }

    @Override
    public void geraHistoricoLog(AcessoSistema responsavel) throws DAOException {
        criaHistoricoLog(null, responsavel);
    }

    protected abstract void criaTabelaHistoricoLog(String nomeTabela) throws DataAccessException;
}
