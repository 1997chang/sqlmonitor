package com.moxiao.sqlmonitor.executor;

import java.util.concurrent.atomic.AtomicInteger;

class CustomizedThreadCreator {

    /**
     * 线程池的前缀
     */
    private String threadPoolPrefix = "sql-monitor-";
    
    private static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;
    
    private boolean daemon = true;
    
    private ThreadGroup threadGroup = new ThreadGroup("sql-MonitorGroup");

    private final AtomicInteger threadCount = new AtomicInteger(0);

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setThreadPoolPrefix(String threadPoolPrefix) {
        this.threadPoolPrefix = threadPoolPrefix;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    public void setThreadGroup(ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }

    public Thread createThread(Runnable runnable) {
        Thread thread = new Thread(getThreadGroup(), runnable, nextThreadName());
        thread.setDaemon(isDaemon());
        thread.setPriority(THREAD_PRIORITY);
        return thread;
    }

    private String nextThreadName() {
        return threadPoolPrefix + this.threadCount.getAndIncrement();
    }
}
