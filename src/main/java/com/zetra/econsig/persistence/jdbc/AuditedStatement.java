package com.zetra.econsig.persistence.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import com.zetra.econsig.helper.sistema.DebugHelper;

public class AuditedStatement implements Statement {
    private static final String SKIP_LOG = "SKIP_LOG";
    private final Connection connection;
    private final Statement sqlStatement;

    public AuditedStatement(Connection connection, Statement sqlStatement) {
        this.connection = connection;
        this.sqlStatement = sqlStatement;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return sqlStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return sqlStatement.isWrapperFor(iface);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        if (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) < 0) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        return sqlStatement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        DebugHelper log = null;
        boolean skipLog = (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) != -1);
        if (!skipLog) {
            log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        int rows = sqlStatement.executeUpdate(sql);
        if (!skipLog) {
            log.debug("Linhas afetadas: " + rows);
        }
        return rows;
    }

    @Override
    public void close() throws SQLException {
        sqlStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return sqlStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        sqlStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return sqlStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        sqlStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        sqlStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return sqlStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        sqlStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        sqlStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return sqlStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        sqlStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        sqlStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        if (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) < 0) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        return sqlStatement.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return sqlStatement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return sqlStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return sqlStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        sqlStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return sqlStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        sqlStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return sqlStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return sqlStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return sqlStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        sqlStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        sqlStatement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return sqlStatement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return sqlStatement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return sqlStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        DebugHelper log = null;
        boolean skipLog = (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) != -1);
        if (!skipLog) {
            log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        int rows = sqlStatement.executeUpdate(sql, autoGeneratedKeys);
        if (!skipLog) {
            log.debug("Linhas afetadas: " + rows);
        }
        return rows;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        DebugHelper log = null;
        boolean skipLog = (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) != -1);
        if (!skipLog) {
            log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        int rows = sqlStatement.executeUpdate(sql, columnIndexes);
        if (!skipLog) {
            log.debug("Linhas afetadas: " + rows);
        }
        return rows;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        DebugHelper log = null;
        boolean skipLog = (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) != -1);
        if (!skipLog) {
            log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        int rows = sqlStatement.executeUpdate(sql, columnNames);
        if (!skipLog) {
            log.debug("Linhas afetadas: " + rows);
        }
        return rows;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        if (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) < 0) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        return sqlStatement.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        if (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) < 0) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        return sqlStatement.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        if (sql != null && sql.toUpperCase().indexOf(SKIP_LOG) < 0) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(sql);
        }
        return sqlStatement.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return sqlStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return sqlStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        sqlStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return sqlStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        sqlStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return sqlStatement.isCloseOnCompletion();
    }
}
