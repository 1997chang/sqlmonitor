package com.moxiao.sqlmonitor.util;

import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;

public class PreparedStatementParseImpl implements PreparedStatement {

    private static final Logger logger = LoggerFactory.getLog(PreparedStatementParseImpl.class);
    
    private final String[] params;

    public PreparedStatementParseImpl(int size) {
        super();
        params = new String[size];
    }

    public String getValue(int index) {
        if (index > params.length) {
            throw new IllegalArgumentException("索引超过preparedStatement参数的最大长度。index:" + index + "ps的长度为：" + params.length);
        }
        return params[index];
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        params[parameterIndex] = "NULL";
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        params[parameterIndex] = Boolean.toString(x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        params[parameterIndex] = Byte.toString(x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        params[parameterIndex] = Short.toString(x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        params[parameterIndex] = Integer.toString(x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        params[parameterIndex] = Long.toString(x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        params[parameterIndex] = Float.toString(x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        params[parameterIndex] = Double.toString(x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        params[parameterIndex] = x.toPlainString();
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        params[parameterIndex] = x;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        params[parameterIndex] = Arrays.toString(x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logger.warn("setAsciiStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logger.warn("setUnicodeStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logger.warn("setBinaryStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void clearParameters() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public boolean execute() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        logger.warn("setCharacterStream(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        logger.warn("setBlob(" + parameterIndex + ")Blob输入流，使用占位符[Blob]");
        params[parameterIndex] = "[Blob]";
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        logger.warn("setClob(" + parameterIndex + ")Clob输入流，使用占位符[Clob]");
        params[parameterIndex] = "[Clob]";
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        params[parameterIndex] = "NULL";
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        params[parameterIndex] = value;
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        logger.warn("setNCharacterStream(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        logger.warn("setNClob(" + parameterIndex + ")NClob输入流，使用占位符[NClob]");
        params[parameterIndex] = "[NClob]";
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logger.warn("setClob(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        logger.warn("setBlob(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logger.warn("setNClob(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        logger.warn("setSQLXML(" + parameterIndex + ")输入流，使用占位符[xmlObject]");
        params[parameterIndex] = "[xmlObject]";
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        params[parameterIndex] = x.toString();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logger.warn("setAsciiStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logger.warn("setBinaryStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        logger.warn("设置" + parameterIndex + "Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        logger.warn("setAsciiStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        logger.warn("setBinaryStream(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        logger.warn("setCharacterStream(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        logger.warn("setCharacterStream(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        logger.warn("setClob(" + parameterIndex + ")Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        logger.warn("setBlob(" + parameterIndex + ")输入流，使用占位符[InputStream]");
        params[parameterIndex] = "[InputStream]";
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        logger.warn("设置" + parameterIndex + "Reader输入流，使用占位符[Reader]");
        params[parameterIndex] = "[Reader]";
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
