package com.moxiao.sqlmonitor.util;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

public final class SQLParseUtils {

    private SQLParseUtils() {}
    
    public static String buildExecuteSql(BoundSql boundSql) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null && parameterMappings.size() > 0) {
            PreparedStatementParseImpl ps = new PreparedStatementParseImpl(parameterMappings.size());
            StringTokenizer tokenizer = new StringTokenizer(boundSql.getSql(), "?", false);
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    stringBuffer.append(tokenizer.nextToken());
                    stringBuffer.append(getValue(ps, parameterMapping, boundSql, i));
                }
            }
            if (tokenizer.hasMoreTokens()) {
                stringBuffer.append(tokenizer.nextToken());
            }
            return stringBuffer.toString();
        } else {
            return boundSql.getSql();
        }
    }

    private static String getValue(PreparedStatementParseImpl ps, ParameterMapping parameterMapping, BoundSql boundSql, int index) {
        Object value;
        String property = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(property)) {
            value = boundSql.getAdditionalParameter(property);
        } else if (boundSql.getParameterObject() == null) {
            value = null;
        } else if (MybatisUtils.getTypeHandlerRegistry().hasTypeHandler(boundSql.getParameterObject().getClass())) {
            value = boundSql.getParameterObject();
        } else {
            MetaObject metaObject = MybatisUtils.configuration.newMetaObject(boundSql.getParameterObject());
            value = metaObject.getValue(property);
        }
        TypeHandler typeHandler = parameterMapping.getTypeHandler();
        JdbcType jdbcType = parameterMapping.getJdbcType();
        if (value == null && jdbcType == null) {
            jdbcType = MybatisUtils.configuration.getJdbcTypeForNull();
        }
        try {
            typeHandler.setParameter(ps, index, value, jdbcType);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ps.getValue(index);
    }
    
}
