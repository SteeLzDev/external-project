package com.zetra.econsig.persistence.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

public class AuditedConnecton implements Connection {
    private final Connection sqlConnection;
    private final Set<String> excludedClasses;

    public AuditedConnecton(Connection sqlConnection, Set<String> excludedClasses) {
        this.sqlConnection = sqlConnection;
        this.excludedClasses = excludedClasses;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return sqlConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return sqlConnection.isWrapperFor(iface);
    }

    @Override
    public Statement createStatement() throws SQLException {
        Statement sqlStatement = sqlConnection.createStatement();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedStatement(this, sqlStatement);
        } else {
            return sqlStatement;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement sqlPreparedStatement = sqlConnection.prepareStatement(sql);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedPreparedStatement(this, sqlPreparedStatement, sql);
        } else {
            return sqlPreparedStatement;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        sqlConnection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return sqlConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        sqlConnection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        sqlConnection.rollback();
    }

    @Override
    public void close() throws SQLException {
        sqlConnection.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return sqlConnection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return sqlConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        sqlConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return sqlConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        sqlConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return sqlConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        sqlConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return sqlConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return sqlConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        sqlConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        Statement sqlStatement = sqlConnection.createStatement(resultSetType, resultSetConcurrency);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedStatement(this, sqlStatement);
        } else {
            return sqlStatement;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        PreparedStatement sqlPreparedStatement = sqlConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        AuditedPreparedStatement aPreparedStatement = new AuditedPreparedStatement(this, sqlPreparedStatement, sql);
        return aPreparedStatement;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return sqlConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        sqlConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        sqlConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return sqlConnection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return sqlConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return sqlConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        sqlConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        sqlConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        Statement sqlStatement = sqlConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedStatement(this, sqlStatement);
        } else {
            return sqlStatement;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        PreparedStatement sqlPreparedStatement = sqlConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedPreparedStatement(this, sqlPreparedStatement, sql);
        } else {
            return sqlPreparedStatement;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        PreparedStatement sqlPreparedStatement = sqlConnection.prepareStatement(sql, autoGeneratedKeys);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedPreparedStatement(this, sqlPreparedStatement, sql);
        } else {
            return sqlPreparedStatement;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        PreparedStatement sqlPreparedStatement = sqlConnection.prepareStatement(sql, columnIndexes);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedPreparedStatement(this, sqlPreparedStatement, sql);
        } else {
            return sqlPreparedStatement;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        PreparedStatement sqlPreparedStatement = sqlConnection.prepareStatement(sql, columnNames);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String className = this.getClass().getName().equals(stack[2].getClassName()) ? stack[3].getClassName() : stack[2].getClassName();
        if (!excludedClasses.contains(className)) {
            return new AuditedPreparedStatement(this, sqlPreparedStatement, sql);
        } else {
            return sqlPreparedStatement;
        }
    }

    @Override
    public Clob createClob() throws SQLException {
        return sqlConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return sqlConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return sqlConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return sqlConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return sqlConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        sqlConnection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        sqlConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return sqlConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return sqlConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return sqlConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return sqlConnection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        sqlConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return sqlConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        sqlConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        sqlConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return sqlConnection.getNetworkTimeout();
    }
}
