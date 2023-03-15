package com.moxiao.sqlmonitor.interceptor;

import com.moxiao.sqlmonitor.exception.SqlMonitorPropertyException;
import com.moxiao.sqlmonitor.executor.CustomizedThreadFactory;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.notice.NoticeHolder;
import com.moxiao.sqlmonitor.notice.NoticePolicy;
import com.moxiao.sqlmonitor.notice.SlowSqlEntity;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;
import com.moxiao.sqlmonitor.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.moxiao.sqlmonitor.constant.MonitorConstants.TIME_DELAY;
import static com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor.statementIdMap;

public class TimeMonitor {

    private static final Logger logger = LoggerFactory.getLog(TimeMonitor.class);

    private static final ScheduledThreadPoolExecutor slowSqlMonitorThreadPool = new ScheduledThreadPoolExecutor(1,
            new CustomizedThreadFactory("slow-sql-monitor-"));
    
    private static ScheduledFuture<?> scheduledFuture;
    
    private static int timeLimit = TIME_DELAY;
    
    static {
        scheduledFuture = slowSqlMonitorThreadPool.scheduleAtFixedRate(
                new MonitorTask(), TIME_DELAY, TIME_DELAY, TimeUnit.MILLISECONDS);
    }

    static void start() {
        logger.info("默认定时监控初始化：添加定时监控慢SQL到Sql-Monitor中");
    }

    static void reStart() {
        logger.info("通过属性重新设置定时监控任务");
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        SqlMonitorProperty sqlMonitorProperty = SqlMonitorInterceptor.getSqlMonitorProperty();
        if (logger.isDebugEnabled()) {
            logger.debug("开启慢SQL监控定时任务，每隔" + sqlMonitorProperty.getSlowSqlMonitor() + "ms执行一次");
        }
        timeLimit = sqlMonitorProperty.getExecuteTimeLimit();
        scheduledFuture = slowSqlMonitorThreadPool.scheduleAtFixedRate(new MonitorTask(), 
                sqlMonitorProperty.getExecuteTimeLimit(), sqlMonitorProperty.getSlowSqlMonitor(), TimeUnit.MILLISECONDS);
    }

    static void shutDown() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        slowSqlMonitorThreadPool.shutdownNow();
    }

    static class MonitorTask implements Runnable {

        @Override
        public void run() {
            try {
                List<SlowSqlEntity> slowSqlList = statementIdMap.values()
                        .stream()
                        .filter(builder -> !builder.isNotifySlowSql())
                        .filter(StatementMonitorTask.Builder::startExecuteSql)
                        .filter(builder -> DateUtils.getMilliInterval(builder.getStartTime(), LocalDateTime.now()) > timeLimit)
                        .map(builder -> {
                            builder.notifySlowSql();
                            builder.executeSql();
                            logger.warn("MapperId: " + builder.getStatementId() + "，开始执行时间为："
                                    + DateUtils.formatLocalDateTime(builder.getStartTime()) + "，时间限制为：" + timeLimit + "ms");
                            if (logger.isDebugEnabled()) {
                                logger.debug("执行的SQL语句为：" + builder.getSql());
                            }
                            //提前获取执行的SQL语句
                            return new SlowSqlEntity(builder.getStatementId(), builder.getSql(), builder.getStartTime(), 
                                    builder.getExecuteStack(), DateUtils.getMilliInterval(builder.getStartTime(), LocalDateTime.now()));
                        }).collect(Collectors.toList());
                if (slowSqlList.size() > 0) {
                    for (NoticePolicy noticePolicy : NoticeHolder.getNoticePolicyList()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("准备使用：" + noticePolicy.getClass().getName() + "进行通知");
                        }
                        noticePolicy.slowSqlNotice(slowSqlList);
                    }
                }
            } catch (Exception e) {
                logger.error("慢SQL监控出现问题", e);
                throw new SqlMonitorPropertyException("慢SQL监控出现问题", e);
            }
            
        }
    }
    
    
}
