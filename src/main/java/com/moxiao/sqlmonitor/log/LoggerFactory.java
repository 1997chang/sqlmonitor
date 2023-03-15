package com.moxiao.sqlmonitor.log;

import com.moxiao.sqlmonitor.log.slf.SlfLoggerAdapter;
import com.moxiao.sqlmonitor.log.std.StandardLoggerAdapter;

import java.lang.reflect.Constructor;

public class LoggerFactory {

    public static final String MARKER = "SQL-MONITOR";

    /**
     * 日志构造函数对象
     */
    private static Constructor<? extends Logger> logConstructor;
    
    static {
        buildLogInstance(SlfLoggerAdapter.class);
        buildLogInstance(StandardLoggerAdapter.class);
    }

    public static Logger getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static Logger getLog(String name) {
        try {
            return logConstructor.newInstance(name);
        } catch (Exception e) {
            throw new LoggerException("Error create Log instance for " + name, e.getCause());
        } 
    }

    private static void buildLogInstance(Class<? extends Logger> logClazz) {
        if (logConstructor == null) {
            try {
                Constructor<? extends Logger> candidateLog = logClazz.getConstructor(String.class);
                Logger logger = candidateLog.newInstance(LoggerFactory.class.getName());
                if (logger.isDebugEnabled()) {
                    logger.debug("Logging instance initialized using " + logClazz + " adapter.");
                }
                logConstructor = candidateLog;
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    
}
