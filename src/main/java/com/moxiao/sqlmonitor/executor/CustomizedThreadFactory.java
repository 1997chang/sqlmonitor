package com.moxiao.sqlmonitor.executor;

import java.util.concurrent.ThreadFactory;

public class CustomizedThreadFactory extends CustomizedThreadCreator implements ThreadFactory {

    public CustomizedThreadFactory(boolean daemon) {
        setDaemon(daemon);
    }

    public CustomizedThreadFactory() {
    }

    public CustomizedThreadFactory(String prefix) {
        setThreadPoolPrefix(prefix);
    }

    @Override
    public Thread newThread(Runnable r) {
        return createThread(r);
    }
}
