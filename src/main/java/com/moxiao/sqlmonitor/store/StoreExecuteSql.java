package com.moxiao.sqlmonitor.store;


import com.alibaba.fastjson2.annotation.JSONField;
import com.moxiao.sqlmonitor.interceptor.StatementMonitorTask;
import com.moxiao.sqlmonitor.util.SQLParseUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.StringJoiner;

import static com.moxiao.sqlmonitor.util.DateUtils.formatLocalDateTime;

public class StoreExecuteSql implements Serializable {
    
    private String uniqueId;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @com.alibaba.fastjson.annotation.JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime createTime;

    /**
     * Mapper类名
     */
    private Class<?> mapperClass;
    /**
     * Mapper类名
     */
    private String mapperName;
    
    /**
     * 方法名
     */
    private String mapperMethodName;

    /**
     * 执行的SQL语句
     */
    private String executorSql;

    /**
     * 执行时间
     */
    private Integer executorTime;

    /**
     * 执行语句栈
     */
    private List<String> executorStack;

    /**
     * 开始执行SQL语句时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @com.alibaba.fastjson.annotation.JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startExecuteTime;

    /**
     * 结束执行SQL语句时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @com.alibaba.fastjson.annotation.JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endExecuteTime;

    /**
     * Statement 名称
     */
    private String statementId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getExecutorSql() {
        return executorSql;
    }

    public void setExecutorSql(String executorSql) {
        this.executorSql = executorSql;
    }

    public Integer getExecutorTime() {
        return executorTime;
    }

    public void setExecutorTime(Integer executorTime) {
        this.executorTime = executorTime;
    }

    public List<String> getExecutorStack() {
        return executorStack;
    }

    public void setExecutorStack(List<String> executorStack) {
        this.executorStack = executorStack;
    }

    public LocalDateTime getStartExecuteTime() {
        return startExecuteTime;
    }

    public void setStartExecuteTime(LocalDateTime startExecuteTime) {
        this.startExecuteTime = startExecuteTime;
    }

    public LocalDateTime getEndExecuteTime() {
        return endExecuteTime;
    }

    public void setEndExecuteTime(LocalDateTime endExecuteTime) {
        this.endExecuteTime = endExecuteTime;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public Class<?> getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getMapperMethodName() {
        return mapperMethodName;
    }

    public void setMapperMethodName(String mapperMethodName) {
        this.mapperMethodName = mapperMethodName;
    }

    public static class Builder {
        
        private final StatementMonitorTask statementMonitorTask;
        
        public Builder(StatementMonitorTask statementMonitorTask){
            this.statementMonitorTask = statementMonitorTask;
        }

        public StoreExecuteSql build() {
            StoreExecuteSql storeExecuteSql = new StoreExecuteSql();
            storeExecuteSql.uniqueId = statementMonitorTask.getUniqueId();
            storeExecuteSql.startExecuteTime = statementMonitorTask.getStartTime();
            storeExecuteSql.endExecuteTime = statementMonitorTask.getEndTime();
            storeExecuteSql.executorStack = statementMonitorTask.getExecuteStack();
            storeExecuteSql.createTime = ZonedDateTime.now();
            storeExecuteSql.executorTime = statementMonitorTask.getExecuteTimeConsuming();
            String statementId = statementMonitorTask.getStatementId();
            storeExecuteSql.statementId = statementId;
            int lastDotPosition = statementId.lastIndexOf('.');
            storeExecuteSql.mapperMethodName = statementId;
            if (lastDotPosition > -1) {
                String className = statementId.substring(0, lastDotPosition);
                storeExecuteSql.mapperName = className;
                try {
                    storeExecuteSql.mapperClass = Class.forName(className);
                } catch (Exception e) {
                    // ignore
                }
                storeExecuteSql.mapperMethodName = statementId.substring(lastDotPosition + 1);
            }
            storeExecuteSql.executorSql = statementMonitorTask.getSql();
            if (storeExecuteSql.executorSql == null) {
                if (statementMonitorTask.isPreparedStatement()) {
                    storeExecuteSql.executorSql = SQLParseUtils.buildExecuteSql(statementMonitorTask.getBoundSql());
                } else {
                    storeExecuteSql.executorSql = statementMonitorTask.getBoundSql().getSql();
                }
            }
            return storeExecuteSql;
        }
        
    }

    public String prettyShortSqlData() {
        return new StringBuilder().
                append("SQL-Monitor检测到执行的SQL语句，StatementId:【").
                append(statementId).
                append("】SQL语句为：【").
                append(executorSql).
                append("】，开始执行时间：【").
                append(formatLocalDateTime(startExecuteTime)).
                append("】，耗时：【").
                append(executorTime).
                append("】ms").toString();
    }

    public String printSqlDataIncludeStack() {
        StringJoiner stackJoiner = new StringJoiner("\n", "[", "]");
        for (String stack : executorStack) {
            stackJoiner.add(stack);
        }
        return new StringBuilder().
                append("SQL-Monitor检测到执行的SQL语句，StatementId:【").
                append(statementId).
                append("】SQL语句为：【").
                append(executorSql).
                append("】，开始执行时间：【").
                append(formatLocalDateTime(startExecuteTime)).
                append("】，耗时：【").
                append(executorTime).
                append("】ms，执行栈：").
                append(stackJoiner).toString();
    }

    public String dingdingNotice() {
        StringJoiner stackJoiner = new StringJoiner("\n", "[", "]");
        for (String stack : executorStack) {
            stackJoiner.add(stack);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("# SQL执行汇总信息 \n")
                .append("#### Mapper路径：\n > ").append(statementId).append(" \n")
                .append("#### SQL语句为：\n > **").append(executorSql).append("**\n")
                .append("#### 执行SQL语句总耗时：\n > **").append(executorTime).append("**ms\n")
                .append("##### 执行SQL语句的栈内容：\n > ").append(stackJoiner).append(" \n");
        return stringBuilder.toString();
    }
}
