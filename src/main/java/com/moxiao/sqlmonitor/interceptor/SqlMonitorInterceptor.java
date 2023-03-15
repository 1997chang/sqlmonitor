package com.moxiao.sqlmonitor.interceptor;

import com.moxiao.sqlmonitor.executor.ExecutorThreadPoolFactory;
import com.moxiao.sqlmonitor.executor.NoticeThreadPool;
import com.moxiao.sqlmonitor.executor.StatementTaskThreadPool;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.notice.NoticeHolder;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;
import com.moxiao.sqlmonitor.store.StoreHolder;
import com.moxiao.sqlmonitor.util.MybatisUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, 
                RowBounds.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class SqlMonitorInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLog(SqlMonitorInterceptor.class);
    
    private static final SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();

    private static final ThreadLocal<String> uniqueIdentity = new ThreadLocal<>();

    static final ConcurrentHashMap<String, StatementMonitorTask.Builder> statementIdMap = new ConcurrentHashMap<>();
    

    public SqlMonitorInterceptor() {
        LOGGER.info("添加SQL-Monitor拦截器到Mybatis中.....");
        TimeMonitor.start();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        LOGGER.debug("进入SQL-Monitor监控数据拦截器.");
        LocalDateTime startTime = LocalDateTime.now();
        executePreTask(invocation, startTime);
        Object result = invocation.proceed();
        LocalDateTime endTime = LocalDateTime.now();
        executeBackTask(invocation, endTime);
        return result;
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties != null) {
            MybatisUtils.setProperty(properties, sqlMonitorProperty);
        }
        if (sqlMonitorProperty.getThreadPoolConfig() != null) {
            ExecutorThreadPoolFactory.setAwaitTerminationSeconds(sqlMonitorProperty.getThreadPoolConfig().getAwaitTerminationSeconds());
            ExecutorThreadPoolFactory.setWaitTasksToCompleteOnShutdown(sqlMonitorProperty.getThreadPoolConfig().isWaitTasksToCompleteOnShutdown());
            StatementTaskThreadPool.buildThreadPool(sqlMonitorProperty.getThreadPoolConfig());
            NoticeThreadPool.setThreadPoolEntity(sqlMonitorProperty.getThreadPoolConfig());
        }
        StoreHolder.build(sqlMonitorProperty.getStorePolicy());
        NoticeHolder.build(sqlMonitorProperty.getNoticePolicy());
        TimeMonitor.reStart();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public static SqlMonitorProperty getSqlMonitorProperty() {
        return sqlMonitorProperty;
    }

    private void executePreTask(Invocation invocation, LocalDateTime startTime) {
        Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
            StatementMonitorTask.Builder builder = statementIdMap.get(uniqueIdentity.get());
            boolean prepared = invocation.getArgs()[0] instanceof PreparedStatement;
            BoundSql boundSql = ((StatementHandler) target).getBoundSql();
            builder.setStartTime(startTime)
                    .setUniqueId(UUID.randomUUID().toString())
                    .setPreparedStatement(prepared)
                    .setBoundSql(boundSql);
        } else if (target instanceof Executor) {
            String uniqueId = UUID.randomUUID().toString();
            uniqueIdentity.set(uniqueId);
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            StatementMonitorTask.Builder builder = new StatementMonitorTask.Builder();
            builder.setStatementId(mappedStatement.getId());
            statementIdMap.put(uniqueId, builder);
            MybatisUtils.setConfig(mappedStatement.getConfiguration());
        }
    }

    private void executeBackTask(Invocation invocation, LocalDateTime endTime) {
        Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
            StatementMonitorTask.Builder builder = statementIdMap.get(uniqueIdentity.get());
            StatementMonitorTask statementMonitorTask = builder.setEndTime(endTime).build();
            StatementTaskThreadPool.getThreadPool().execute(statementMonitorTask);
        } else if (target instanceof Executor) {
            statementIdMap.remove(uniqueIdentity.get());
            uniqueIdentity.remove();
        }
    }
    
}
