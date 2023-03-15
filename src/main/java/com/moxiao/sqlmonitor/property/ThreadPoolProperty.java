package com.moxiao.sqlmonitor.property;

import com.moxiao.sqlmonitor.executor.CustomizableRejectedLoggerPolicy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolProperty {
    
    public static final boolean WAIT_TASKS_TO_COMPLETE_ON_SHUTDOWN = false;
    
    public static final int AWAIT_TERMINATION_SECONDS = 0;

    private static final Set<String> ALL_REJECTED_POLICY = new HashSet<>(Arrays.asList("ABORT", "LOGGER", "DISCARD", 
            "DISCARD_OLDEST", "CALLER_RUNNER"));
    
    /**
     * 核心线程池数量
     */
    private Integer corePoolSize = 10;

    /**
     * 额外的线程（除核心线程）保持的时间，单位ms
     */
    private Long keepAliveTime = 60_000L;

    /**
     * 阻塞队列的最大长度
     */
    private Integer queueCapacity = Integer.MAX_VALUE;
    
    /**
     * 拒绝执行的策略，ABORT,LOGGER,DISCARD,DISCARD_OLDEST,CALLER_RUNNER
     */
    private String rejectedExecutionPolicy = "LOGGER";

    /**
     * 是否守护线程
     */
    private boolean daemon = true;

    /**
     * 是否等待任务完成之后关闭线程
     */
    private boolean waitTasksToCompleteOnShutdown = WAIT_TASKS_TO_COMPLETE_ON_SHUTDOWN;

    /**
     * 如果等待任务完成，则最大等待时间
     */
    private int awaitTerminationSeconds = AWAIT_TERMINATION_SECONDS;

    public int getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getRejectedExecutionPolicy() {
        return rejectedExecutionPolicy;
    }

    public void setRejectedExecutionPolicy(String rejectedExecutionPolicy) {
        if (rejectedExecutionPolicy == null || rejectedExecutionPolicy.isEmpty()) {
            rejectedExecutionPolicy = "LOGGER";
        }
        if (!ALL_REJECTED_POLICY.contains(rejectedExecutionPolicy.toUpperCase(Locale.ENGLISH))) {
            rejectedExecutionPolicy = "LOGGER";
        }
        this.rejectedExecutionPolicy = rejectedExecutionPolicy.toUpperCase(Locale.ENGLISH);
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public boolean getDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        switch (rejectedExecutionPolicy) {
            case "LOGGER":
                return new CustomizableRejectedLoggerPolicy();
            case "ABORT":
                return new ThreadPoolExecutor.AbortPolicy();
            case "DISCARD":
                return new ThreadPoolExecutor.DiscardPolicy();
            case "DISCARD_OLDEST":
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            case "CALLER_RUNNER":
                return new ThreadPoolExecutor.CallerRunsPolicy();
            default:
                throw new IllegalArgumentException("rejectedExecutionPolicy:" + rejectedExecutionPolicy
                        + "not in ABORT,LOGGER,DISCARD,DISCARD_OLDEST,CALLER_RUNNER.");
        }
    }

    public boolean isWaitTasksToCompleteOnShutdown() {
        return waitTasksToCompleteOnShutdown;
    }

    public void setWaitTasksToCompleteOnShutdown(boolean waitTasksToCompleteOnShutdown) {
        this.waitTasksToCompleteOnShutdown = waitTasksToCompleteOnShutdown;
    }
}
