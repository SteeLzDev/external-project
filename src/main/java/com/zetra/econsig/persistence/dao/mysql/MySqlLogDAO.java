package com.zetra.econsig.persistence.dao.mysql;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.generic.GenericLogDAO;

/**
 * <p>Title: MySqlLogDAO</p>
 * <p>Description: Implementacao do DAO de Log para o MySql</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlLogDAO extends GenericLogDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlLogDAO.class);

    @Override
    protected void criaTabelaHistoricoLog(String nomeTabela) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        LOG.debug("INICIO CRIA TABELA HISTORICO LOG: " + nomeTabela);
        final StringBuilder query = new StringBuilder();

        try {
            query.setLength(0);
            query.append("SELECT COUNT(*) FROM ").append(nomeTabela);
            LOG.trace(query.toString());
            jdbc.queryForObject(query.toString(), queryParams, Integer.class);

        } catch (Exception e) {
            LOG.debug("Não existe tabela de histórico de log que será criada: ['" + nomeTabela + "'].");

            query.setLength(0);
            query.append("CREATE TABLE ").append(nomeTabela).append(" (");
            query.append("TLO_CODIGO                     varchar(32)    not null, ");
            query.append("TEN_CODIGO                     varchar(32), ");
            query.append("USU_CODIGO                     varchar(32), ");
            query.append("FUN_CODIGO                     varchar(32), ");
            query.append("LOG_DATA                       datetime       not null, ");
            query.append("LOG_OBS                        text           not null, ");
            query.append("LOG_IP                         varchar(45), ");
            query.append("LOG_COD_ENT_00                 varchar(32), ");
            query.append("LOG_COD_ENT_01                 varchar(32), ");
            query.append("LOG_COD_ENT_02                 varchar(32), ");
            query.append("LOG_COD_ENT_03                 varchar(32), ");
            query.append("LOG_COD_ENT_04                 varchar(32), ");
            query.append("LOG_COD_ENT_05                 varchar(32), ");
            query.append("LOG_COD_ENT_06                 varchar(32), ");
            query.append("LOG_COD_ENT_07                 varchar(32), ");
            query.append("LOG_COD_ENT_08                 varchar(32), ");
            query.append("LOG_COD_ENT_09                 varchar(32), ");
            query.append("LOG_COD_ENT_10                 varchar(32), ");
            query.append("LOG_CANAL                      char(1)        not null default '1', ");
            query.append("LOG_PORTA                      int, ");
            query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_1` (`LOG_DATA`), ");
            query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_2` (`USU_CODIGO`), ");
            query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_3` (`FUN_CODIGO`) ");
            query.append(")");

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        }
        LOG.debug("TERMINO CRIA TABELA HISTORICO LOG: " + nomeTabela);
    }
}
