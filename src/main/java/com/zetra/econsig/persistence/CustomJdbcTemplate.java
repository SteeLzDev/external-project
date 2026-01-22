package com.zetra.econsig.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.Nullable;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.sistema.DebugHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

public class CustomJdbcTemplate extends NamedParameterJdbcTemplate {
    private static final Pattern SKIP_LOG_PATTERN = Pattern.compile(".*SKIP_LOG.*", Pattern.CASE_INSENSITIVE);

    /**
     * ResultSetExtractor to avoid EmptyResultDataAccessException when query 
     * doesn't return any data or return more than 1 row.
     * @param <T>
     * @param mapper
     * @return
     */
    private static <T> ResultSetExtractor<T> singletonExtractor(RowMapper<? extends T> mapper) {
        return rs -> rs.next() ? mapper.mapRow(rs, 1) : null;
    }

    public CustomJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
        logSQL(sql, paramSource);
        return super.query(sql, paramSource, singletonExtractor(rowMapper));
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource) throws DataAccessException {
        final boolean debug = logSQL(sql, paramSource);
        final int rows = super.update(sql, paramSource);
        if (debug) {
            DebugHelper.getCallerLog().debug("Rows affected: " + rows);
        }
        return rows;
    }

    @Override
    public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return update(sql, new MapSqlParameterSource(paramMap));
    }
    
    private boolean logSQL(String sql, SqlParameterSource paramSource) {
        if (isLoggingEnabled()) {
            if (sql != null && !SKIP_LOG_PATTERN.matcher(sql).matches()) {
                DebugHelper.getCallerLog().debug(sql);
                return true;
            }
        }
        return false;
    }

    private boolean isLoggingEnabled() {
        if (ParamSist.paramEquals(CodedValues.TPC_IMPRIMIR_LOG_SQLS_ROTINA_PROCESSAMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            final String excludedClassesParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IGNORAR_CLASSES_AO_IMPRIMIR_LOG_SQLS_ROTINA_PROCESSAMENTO, AcessoSistema.getAcessoUsuarioSistema());
            // No exception
            if (TextHelper.isNull(excludedClassesParam)) {
                return true;
            }
    
            final Set<String> excludedClasses = new HashSet<>();
            Collections.addAll(excludedClasses, excludedClassesParam.split("[\\p{Space},;]"));

            // stack[0] = java.lang.Thread.getStackTrace(
            // stack[1] = DBHelper.getAuditedConnecton((Connection)
            // stack[2] = DBHelper.makeConnection(Connection)
            // stack[3] = DBHelper.makeConnection()
            // stack[4] = método que solicita o DBHelper.makeConnection()
            final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            final String className = ((stack != null) && (stack.length >= 5)) ? stack[4].getClassName() : DBHelper.class.getName();
            if (!excludedClasses.contains(className)) {
                return true;
            }
        }
        return false;
    }
}
