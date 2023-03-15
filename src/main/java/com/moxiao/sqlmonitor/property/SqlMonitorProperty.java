package com.moxiao.sqlmonitor.property;

import static com.moxiao.sqlmonitor.constant.MonitorConstants.TIME_DELAY;

public class SqlMonitorProperty {

    /**
     * 最大执行SQL语句的最大时间，单位为毫秒
     */
    private int executeTimeLimit = TIME_DELAY;

    /**
     * 慢SQL监控任务的时间延迟
     */
    private int slowSqlMonitor = TIME_DELAY;

    /**
     * 检测SQL栈的匹配模式，优先monitorStackClass
     */
    private String monitorStackPrefix;

    /**
     * 检测SQL栈的匹配模式
     */
    private Class<?> monitorStackClass;

    /**
     * SQL栈的最大长度
     */
    private int maxStackLength = 30;

    /**
     * 存储策略
     */
    private String storePolicy = "LOGGER";

    /**
     * 执行时间超过执行阈值，进行通知
     */
    private String noticePolicy = "DINGDING";

    /**
     * 线程池的配置
     */
    private ThreadPoolProperty threadPoolConfig;
    
    private DingDingProperty dingdingConfig;
    
    private ElasticsearchProperty esConfig;

    public int getExecuteTimeLimit() {
        return executeTimeLimit;
    }

    public void setExecuteTimeLimit(int executeTimeLimit) {
        this.executeTimeLimit = executeTimeLimit;
    }

    public String getMonitorStackPrefix() {
        return monitorStackPrefix;
    }

    public void setMonitorStackPrefix(String monitorStackPrefix) {
        this.monitorStackPrefix = monitorStackPrefix;
    }

    public Class<?> getMonitorStackClass() {
        return monitorStackClass;
    }

    public void setMonitorStackClass(Class<?> monitorStackClass) {
        this.monitorStackClass = monitorStackClass;
    }

    public String getStorePolicy() {
        return storePolicy;
    }

    public void setStorePolicy(String storePolicy) {
        this.storePolicy = storePolicy;
    }

    public ThreadPoolProperty getThreadPoolConfig() {
        return threadPoolConfig;
    }

    public void setThreadPoolConfig(ThreadPoolProperty threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }

    public int getMaxStackLength() {
        return maxStackLength;
    }

    public void setMaxStackLength(int maxStackLength) {
        this.maxStackLength = maxStackLength;
    }

    public String getNoticePolicy() {
        return noticePolicy;
    }

    public void setNoticePolicy(String noticePolicy) {
        this.noticePolicy = noticePolicy;
    }

    public DingDingProperty getDingdingConfig() {
        return dingdingConfig;
    }

    public void setDingdingConfig(DingDingProperty dingdingConfig) {
        this.dingdingConfig = dingdingConfig;
    }

    public int getSlowSqlMonitor() {
        return slowSqlMonitor;
    }

    public void setSlowSqlMonitor(int slowSqlMonitor) {
        this.slowSqlMonitor = slowSqlMonitor;
    }

    public ElasticsearchProperty getEsConfig() {
        return esConfig;
    }

    public void setEsConfig(ElasticsearchProperty esConfig) {
        this.esConfig = esConfig;
    }
}
