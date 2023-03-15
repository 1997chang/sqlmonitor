package com.moxiao.sqlmonitor.executor;

import com.moxiao.sqlmonitor.property.ThreadPoolProperty;

import java.util.concurrent.ThreadPoolExecutor;

import static com.moxiao.sqlmonitor.executor.ExecutorThreadPoolFactory.createThreadPool;

public class NoticeThreadPool {

    private static ThreadPoolExecutor threadPoolExecutor;
    
    private static volatile ThreadPoolProperty threadPoolProperty;

    public static ThreadPoolExecutor getThreadPool() {
        if (threadPoolExecutor == null) {
            threadPoolExecutor = NoticeThreadPool.ThreadPoolHolder.getInstance();
        }
        return threadPoolExecutor;
    }

    public static synchronized void setThreadPoolEntity(ThreadPoolProperty threadPoolProperty) {
        if (NoticeThreadPool.threadPoolProperty != null) {
            return;
        }
        NoticeThreadPool.threadPoolProperty = threadPoolProperty;
    }

    /**
     * 线程池占位符
     */
    private static class ThreadPoolHolder {

        private static final ThreadPoolExecutor INSTANCE;

        static {
            ThreadPoolProperty threadPoolProperty = NoticeThreadPool.threadPoolProperty;
            if (threadPoolProperty == null) {
                threadPoolProperty = new ThreadPoolProperty();
            }
            INSTANCE = createThreadPool(threadPoolProperty);
        }

        public static ThreadPoolExecutor getInstance() {
            return INSTANCE;
        }
    }
}
