package com.moxiao.sqlmonitor.executor;

import com.moxiao.sqlmonitor.interceptor.StatementMonitorTask;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CustomizableRejectedLoggerPolicy implements RejectedExecutionHandler {

    private static final Logger logger = LoggerFactory.getLog(CustomizableRejectedLoggerPolicy.class);
    
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.warn("SQL-Monitor出现拒绝任务内容。执行器的内容为：" + executor.toString());
        if (r instanceof StatementMonitorTask) {
            StatementMonitorTask statementMonitorTask = (StatementMonitorTask) r;
            logger.warn(statementMonitorTask.prettyShortData());
        }
    }
}
