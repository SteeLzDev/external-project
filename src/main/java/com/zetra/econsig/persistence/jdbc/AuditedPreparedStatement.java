package com.zetra.econsig.persistence.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import com.zetra.econsig.helper.sistema.DebugHelper;

public class AuditedPreparedStatement implements PreparedStatement {
    private static final String SKIP_LOG = "SKIP_LOG";
    private final Connection connection;
    private final PreparedStatement sqlPreparedStatement;
    private final String initialSQL;
    private final Object[] parameters;
    private final boolean skipLog;

    public AuditedPreparedStatement(Connection connection, PreparedStatement sqlPreparedStatement, String initialSQL) {
        this.connection = connection;
        this.sqlPreparedStatement = sqlPreparedStatement;
        this.initialSQL = initialSQL;
        // por convenção os índices de parametros de preparedstatement começam de 1.
        // espera-se que na posição 0 (zero) algo apenas para fazer o shift.
        parameters = new Object[StringUtils.countMatches(initialSQL, "?")+1];
        skipLog = (initialSQL.toUpperCase().indexOf(SKIP_LOG) != -1);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return sqlPreparedStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return sqlPreparedStatement.isWrapperFor(iface);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public void close() throws SQLException {
        sqlPreparedStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return sqlPreparedStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        sqlPreparedStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return sqlPreparedStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        sqlPreparedStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        sqlPreparedStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return sqlPreparedStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        sqlPreparedStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        sqlPreparedStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return sqlPreparedStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        sqlPreparedStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        sqlPreparedStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return sqlPreparedStatement.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return sqlPreparedStatement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return sqlPreparedStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return sqlPreparedStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        sqlPreparedStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return sqlPreparedStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        sqlPreparedStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return sqlPreparedStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return sqlPreparedStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return sqlPreparedStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        sqlPreparedStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        sqlPreparedStatement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return sqlPreparedStatement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return sqlPreparedStatement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return sqlPreparedStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new SQLException("Not allowed");
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return sqlPreparedStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return sqlPreparedStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        sqlPreparedStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return sqlPreparedStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        sqlPreparedStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return sqlPreparedStatement.isCloseOnCompletion();
    }

    @Override
    public boolean execute() throws SQLException {
        if (!skipLog) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(DebugHelper.generateActualSql(initialSQL, parameters));
        }
        return sqlPreparedStatement.execute();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        if (!skipLog) {
            DebugHelper log = DebugHelper.getCallerLog();
            log.debug(DebugHelper.generateActualSql(initialSQL, parameters));
        }
        return sqlPreparedStatement.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        DebugHelper log = null;
        if (!skipLog) {
            log = DebugHelper.getCallerLog();
            log.debug(DebugHelper.generateActualSql(initialSQL, parameters));
        }
        int rows = sqlPreparedStatement.executeUpdate();
        if (!skipLog) {
            log.debug("Linhas afetadas: " + rows);
        }
        return rows;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameters[parameterIndex] = null;
        sqlPreparedStatement.setNull(parameterIndex, sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBoolean(parameterIndex, value);
    }

    @Override
    public void setByte(int parameterIndex, byte value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setByte(parameterIndex, value);
    }

    @Override
    public void setShort(int parameterIndex, short value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setShort(parameterIndex, value);
    }

    @Override
    public void setInt(int parameterIndex, int value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setInt(parameterIndex, value);
    }

    @Override
    public void setLong(int parameterIndex, long value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setLong(parameterIndex, value);
    }

    @Override
    public void setFloat(int parameterIndex, float value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setFloat(parameterIndex, value);
    }

    @Override
    public void setDouble(int parameterIndex, double value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setDouble(parameterIndex, value);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBigDecimal(parameterIndex, value);
    }

    @Override
    public void setString(int parameterIndex, String value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setString(parameterIndex, value);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBytes(parameterIndex, value);
    }

    @Override
    public void setDate(int parameterIndex, Date value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setDate(parameterIndex, value);
    }

    @Override
    public void setTime(int parameterIndex, Time value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setTime(parameterIndex, value);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setTimestamp(parameterIndex, value);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream value, int length) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setAsciiStream(parameterIndex, value, length);
    }

    @Override
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream value, int length) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setUnicodeStream(parameterIndex, value, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream value, int length) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBinaryStream(parameterIndex, value, length);
    }

    @Override
    public void clearParameters() throws SQLException {
        sqlPreparedStatement.clearParameters();
    }

    @Override
    public void setObject(int parameterIndex, Object value, int targetSqlType) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setObject(parameterIndex, value, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setObject(parameterIndex, value);
    }

    @Override
    public void addBatch() throws SQLException {
        sqlPreparedStatement.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        sqlPreparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setRef(parameterIndex, value);
    }

    @Override
    public void setBlob(int parameterIndex, Blob value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBlob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Clob value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setClob(parameterIndex, value);
    }

    @Override
    public void setArray(int parameterIndex, Array value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setArray(parameterIndex, value);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return sqlPreparedStatement.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date value, Calendar cal) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setDate(parameterIndex, value, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time value, Calendar cal) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setTime(parameterIndex, value, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp value, Calendar cal) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setTimestamp(parameterIndex, value, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        parameters[parameterIndex] = null;
        sqlPreparedStatement.setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setURL(parameterIndex, value);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return sqlPreparedStatement.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setRowId(parameterIndex, value);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setNString(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setNClob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        sqlPreparedStatement.setClob(parameterIndex, reader, length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        sqlPreparedStatement.setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        sqlPreparedStatement.setNClob(parameterIndex, reader, length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML valuemlObject) throws SQLException {
        sqlPreparedStatement.setSQLXML(parameterIndex, valuemlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object value, int targetSqlType, int scaleOrLength) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setObject(parameterIndex, value, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream value, long length) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setAsciiStream(parameterIndex, value, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream value, long length) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBinaryStream(parameterIndex, value, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        sqlPreparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setAsciiStream(parameterIndex, value);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setBinaryStream(parameterIndex, value);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        sqlPreparedStatement.setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        parameters[parameterIndex] = value;
        sqlPreparedStatement.setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        sqlPreparedStatement.setClob(parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        sqlPreparedStatement.setBlob(parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        sqlPreparedStatement.setNClob(parameterIndex, reader);
    }
}
