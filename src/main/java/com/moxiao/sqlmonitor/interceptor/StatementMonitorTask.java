package com.moxiao.sqlmonitor.interceptor;

import com.moxiao.sqlmonitor.executor.NoticeThreadPool;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.notice.NoticeException;
import com.moxiao.sqlmonitor.notice.NoticeHolder;
import com.moxiao.sqlmonitor.notice.NoticePolicy;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;
import com.moxiao.sqlmonitor.store.StoreException;
import com.moxiao.sqlmonitor.store.StoreExecuteSql;
import com.moxiao.sqlmonitor.store.StoreHolder;
import com.moxiao.sqlmonitor.store.StorePolicy;
import com.moxiao.sqlmonitor.util.DateUtils;
import com.moxiao.sqlmonitor.util.ExecuteStackTool;
import com.moxiao.sqlmonitor.util.SQLParseUtils;
import org.apache.ibatis.mapping.BoundSql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class StatementMonitorTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLog(StatementMonitorTask.class);

    /**
     * 执行SQL语句的唯一标识
     */
    private String uniqueId;

    /**
     * 执行statement的标识
     */
    private String statementId;
    
    private BoundSql boundSql;

    /**
     * SQL语句执行时间
     */
    private LocalDateTime startTime;

    /**
     * SQL语句结束时间
     */
    private LocalDateTime endTime;

    /**
     * 耗时
     */
    private int executeTimeConsuming;

    /**
     * 是否用解析SQL语句
     */
    private boolean preparedStatement;

    /**
     * 执行的SQL，可能慢SQL提前进了初始化，避免再次初始化
     */
    private String sql;
    
    
    private List<String> executeStack;
    

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("执行SQL语句[" + statementId + "]的时间长度为：" + executeTimeConsuming + "ms");
        }
        StoreExecuteSql storeExecuteSql = new StoreExecuteSql.Builder(this).build();
        storeSql(storeExecuteSql);
        notifySql(storeExecuteSql);
    }

    private void storeSql(StoreExecuteSql storeExecuteSql) {
        SqlMonitorProperty sqlMonitorProperty = SqlMonitorInterceptor.getSqlMonitorProperty();
        int executeTimeLimit = sqlMonitorProperty.getExecuteTimeLimit();
        try {
            for (StorePolicy storePolicy : StoreHolder.getStorePolicyList()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("准备使用存储策略：" + storePolicy.getClass().getName());
                }
                storePolicy.storeData(storeExecuteSql);
                if (executeTimeConsuming > executeTimeLimit) {
                    storePolicy.storeSlowData(storeExecuteSql);
                }
            }
        } catch (Exception e) {
            logger.error("SQL存储出现问题", e);
            throw new StoreException("SQL存储出现问题", e);
        }
    }

    private void notifySql(StoreExecuteSql storeExecuteSql) {
        SqlMonitorProperty sqlMonitorProperty = SqlMonitorInterceptor.getSqlMonitorProperty();
        int executeTimeLimit = sqlMonitorProperty.getExecuteTimeLimit();
        if (executeTimeConsuming > executeTimeLimit) {
            ThreadPoolExecutor noticeThreadPool = NoticeThreadPool.getThreadPool();
            noticeThreadPool.execute(() -> {
                try {
                    if (logger.isTraceEnabled()) {
                        logger.trace("执行时间为：" + executeTimeConsuming + "ms。最大限制时间：" + executeTimeLimit + "ms。准备执行通知");
                    }
                    for (NoticePolicy noticePolicy : NoticeHolder.getNoticePolicyList()) {
                        noticePolicy.notice(storeExecuteSql);
                    }
                } catch (Exception e) {
                    logger.error("通知慢SQL出现问题", e);
                    throw new NoticeException("通知慢SQL出现问题", e);
                }
            });
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }

    public void setBoundSql(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public List<String> getExecuteStack() {
        return executeStack;
    }

    public void setExecuteStack(List<String> executeStack) {
        this.executeStack = executeStack;
    }

    public int getExecuteTimeConsuming() {
        return executeTimeConsuming;
    }

    public void setExecuteTimeConsuming(int executeTimeConsuming) {
        this.executeTimeConsuming = executeTimeConsuming;
    }

    public boolean isPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(boolean preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 简短的信息描述，无SQL语句参数拼接，无SQL栈
     * @return 简短的信息描述
     */
    public String prettyShortData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("执行SQL语句的简洁内容：执行StatementID：【").
                append(getStatementId()).
                append("】。原始SQL语句为：【").
                append(getBoundSql().getSql()).
                append("】。开始执行时间：【").
                append(DateUtils.formatLocalDateTime(getStartTime())).
                append("】。耗时：【").
                append(getExecuteTimeConsuming()).
                append("】。" );
        return stringBuilder.toString();
    }
    
    public static class Builder {
        
        private String uniqueId;

        private BoundSql boundSql;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private String statementId;
        
        private boolean preparedStatement;
        
        private String sql;
        
        private boolean isNotifySlowSql = false;
        
        private final List<String> executeStack = ExecuteStackTool.getExecuteStack(SqlMonitorInterceptor.getSqlMonitorProperty());

        public Builder setStatementId(String statementId) {
            this.statementId = statementId;
            return this;
        }

        public Builder setBoundSql(BoundSql boundSql) {
            this.boundSql = boundSql;
            return this;
        }

        public Builder setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder setPreparedStatement(boolean preparedStatement) {
            this.preparedStatement = preparedStatement;
            return this;
        }

        public void notifySlowSql() {
            this.isNotifySlowSql = true;
        }

        public boolean isNotifySlowSql() {
            return isNotifySlowSql;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public String getStatementId() {
            return statementId;
        }

        public String getSql() {
            return sql;
        }

        public List<String> getExecuteStack() {
            return executeStack;
        }

        public StatementMonitorTask build() {
            StatementMonitorTask statementMonitorTask = new StatementMonitorTask();
            statementMonitorTask.setUniqueId(uniqueId);
            statementMonitorTask.setBoundSql(boundSql);
            statementMonitorTask.setStartTime(startTime);
            statementMonitorTask.setEndTime(endTime);
            statementMonitorTask.setStatementId(statementId);
            statementMonitorTask.setExecuteTimeConsuming(DateUtils.getMilliInterval(startTime, endTime));
            statementMonitorTask.setPreparedStatement(preparedStatement);
            statementMonitorTask.setSql(sql);
            statementMonitorTask.setExecuteStack(executeStack);
            return statementMonitorTask;
        }

        public void executeSql() {
            if (boundSql != null) {
                if (preparedStatement) {
                    sql = SQLParseUtils.buildExecuteSql(boundSql);
                } else {
                    sql = boundSql.getSql();
                }
                return;
            }
            throw new IllegalArgumentException("boundSql为空，无法获取SQL语句");
        }

        public boolean startExecuteSql() {
            return startTime != null;
        }
    }
}
