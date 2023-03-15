package com.moxiao.sqlmonitor.executor;

import com.moxiao.sqlmonitor.interceptor.StatementMonitorTask;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.property.ThreadPoolProperty;

import java.util.concurrent.*;

import static com.moxiao.sqlmonitor.property.ThreadPoolProperty.AWAIT_TERMINATION_SECONDS;
import static com.moxiao.sqlmonitor.property.ThreadPoolProperty.WAIT_TASKS_TO_COMPLETE_ON_SHUTDOWN;

public class ExecutorThreadPoolFactory {

    private static final Logger logger = LoggerFactory.getLog(ExecutorThreadPoolFactory.class);
    
    private static boolean waitTasksToCompleteOnShutdown = WAIT_TASKS_TO_COMPLETE_ON_SHUTDOWN;
    
    private static int awaitTerminationSeconds = AWAIT_TERMINATION_SECONDS;

    public static void shutdown(ThreadPoolExecutor threadPoolExecutor) {
        if (threadPoolExecutor == null) {
            return;
        }
        logger.info("准备关闭线程池内容");
        if (waitTasksToCompleteOnShutdown) {
            threadPoolExecutor.shutdown();
        } else {
            for (Runnable runnable : threadPoolExecutor.shutdownNow()) {
                cancelRemainingTask(runnable);
            }
        }
        awaitShutdown(threadPoolExecutor);
    }

    private static void awaitShutdown(ThreadPoolExecutor threadPoolExecutor) {
        if (awaitTerminationSeconds > 0) {
            try {
                if (!threadPoolExecutor.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS)) {
                    logger.warn("超过最大等待线程池关闭时间。等待时间为：" + awaitTerminationSeconds);
                }
            } catch (InterruptedException e) {
                logger.warn("在等待关闭线程池进行了中断操作");
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void cancelRemainingTask(Runnable runnable) {
        logger.warn("准备取消一个SQL监控的任务，进行简短描述：");
        if (runnable instanceof StatementMonitorTask) {
            logger.warn(((StatementMonitorTask) runnable).prettyShortData());
        }
    }

    public static ThreadPoolExecutor createThreadPool(ThreadPoolProperty threadPoolProperty) {
        return new ThreadPoolExecutor(threadPoolProperty.getCorePoolSize(),
                threadPoolProperty.getCorePoolSize(), 
                threadPoolProperty.getKeepAliveTime(), 
                TimeUnit.MILLISECONDS,
                createBlockingDeque(threadPoolProperty.getQueueCapacity()), 
                new CustomizedThreadFactory(threadPoolProperty.getDaemon()), 
                threadPoolProperty.getRejectedExecutionHandler());
    }

    private static BlockingQueue<Runnable> createBlockingDeque(int queueCapacity) {
        return queueCapacity > 0 ? new LinkedBlockingDeque<>(queueCapacity) : new SynchronousQueue<>();
    }

    private ExecutorThreadPoolFactory() {}

    public static void setWaitTasksToCompleteOnShutdown(boolean waitTasksToCompleteOnShutdown) {
        ExecutorThreadPoolFactory.waitTasksToCompleteOnShutdown = waitTasksToCompleteOnShutdown;
    }

    public static void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        ExecutorThreadPoolFactory.awaitTerminationSeconds = awaitTerminationSeconds;
    }
}
