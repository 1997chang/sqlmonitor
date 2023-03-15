package com.moxiao.sqlmonitor.notice;

import com.moxiao.sqlmonitor.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

public class SlowSqlEntity {
    
    private final String statementId;
    
    private final String sql;
    
    private final LocalDateTime startTime;
    
    private final List<String> executorStack;
    
    private final int executorTime;

    public SlowSqlEntity(String statementId, String sql, LocalDateTime startTime, List<String> executorStack, int milliInterval) {
        this.statementId = statementId;
        this.sql = sql;
        this.startTime = startTime;
        this.executorStack = executorStack;
        this.executorTime = milliInterval;
    }

    public String getStatementId() {
        return statementId;
    }

    public String getSql() {
        return sql;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public List<String> getExecutorStack() {
        return executorStack;
    }

    public String dingdingNotice() {
        StringJoiner stackJoiner = new StringJoiner("\n", "[", "]");
        for (String stack : executorStack) {
            stackJoiner.add(stack);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("# <font color=\"FF4500\">监控慢SQL告警: </font> \n")
                .append("#### 已经执行了：\n > **").append(executorTime).append("** ms，并且还在执行中。\n")
                .append("#### Mapper路径：\n > ").append(statementId).append(" \n")
                .append("#### SQL语句为：\n > **").append(sql).append("**\n")
                .append("#### 开始执行SQL语句时间为：\n > **").append(DateUtils.formatLocalDateTime(startTime)).append("**\n")
                .append("##### 执行SQL语句的栈内容：\n > ").append(stackJoiner).append(" \n");
        return stringBuilder.toString();
    }
}
