package com.zetra.econsig.persistence.dao.oracle;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.generic.GenericLogDAO;

/**
 * <p>Title: OracleLogDAO</p>
 * <p>Description: Implementacao do DAO de Log para o Oracle</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleLogDAO extends GenericLogDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleLogDAO.class);

    @Override
    protected void criaTabelaHistoricoLog(String nomeTabela) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        try {
            query.setLength(0);
            query.append("SELECT COUNT(*) FROM ").append(nomeTabela);
            LOG.trace(query.toString());
            jdbc.queryForObject(query.toString(), queryParams, Integer.class);

        } catch (Exception e) {
            query.setLength(0);
            query.append("CREATE TABLE ").append(nomeTabela).append(" ( ");
            query.append("tlo_codigo           VARCHAR2(32 CHAR)    NOT NULL, ");
            query.append("ten_codigo           VARCHAR2(32 CHAR),   ");
            query.append("usu_codigo           VARCHAR2(32 CHAR),   ");
            query.append("fun_codigo           VARCHAR2(32 CHAR),   ");
            query.append("log_data             TIMESTAMP(0)         NOT NULL, ");
            query.append("log_obs              CLOB                 NOT NULL, ");
            query.append("log_ip               VARCHAR2(45 CHAR),   ");
            query.append("log_cod_ent_00       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_01       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_02       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_03       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_04       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_05       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_06       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_07       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_08       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_09       VARCHAR2(32 CHAR),   ");
            query.append("log_cod_ent_10       VARCHAR2(32 CHAR),   ");
            query.append("log_canal            CHAR(1)              DEFAULT '1' NOT NULL, ");
            query.append("log_porta            INTEGER              ");
            query.append(") ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index ").append(nomeTabela).append("_idx_1 on ").append(nomeTabela).append(" ( log_data ) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index ").append(nomeTabela).append("_idx_2 on ").append(nomeTabela).append(" ( usu_codigo ) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index ").append(nomeTabela).append("_idx_3 on ").append(nomeTabela).append(" ( fun_codigo ) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        }
    }
}
