package com.moxiao.sqlmonitor.log;

public interface Logger {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void trace(String message);

    void debug(String message);
    
    void info(String message);
    
    void warn(String message);

    void error(String message);

    void error(String message, Throwable cause);
    
}
