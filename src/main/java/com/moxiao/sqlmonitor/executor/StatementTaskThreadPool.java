package com.moxiao.sqlmonitor.executor;

import com.moxiao.sqlmonitor.property.ThreadPoolProperty;

import java.util.concurrent.ThreadPoolExecutor;

import static com.moxiao.sqlmonitor.executor.ExecutorThreadPoolFactory.createThreadPool;

public class StatementTaskThreadPool {

    private static ThreadPoolExecutor threadPoolExecutor;

    public static ThreadPoolExecutor getThreadPool() {
        if (threadPoolExecutor == null) {
            threadPoolExecutor = ThreadPoolHolder.getInstance();
        }
        return threadPoolExecutor;
    }

    public static synchronized void buildThreadPool(ThreadPoolProperty threadPoolProperty) {
        if (threadPoolProperty == null || threadPoolExecutor != null) {
            return;
        }
        threadPoolExecutor = createThreadPool(threadPoolProperty);
    }

    /**
     * 线程池占位符
     */
    private static class ThreadPoolHolder {

        private static final ThreadPoolExecutor INSTANCE;

        static {
            ThreadPoolProperty threadPoolProperty = new ThreadPoolProperty();
            INSTANCE = createThreadPool(threadPoolProperty);
        }

        public static ThreadPoolExecutor getInstance() {
            return INSTANCE;
        }
    }
}
