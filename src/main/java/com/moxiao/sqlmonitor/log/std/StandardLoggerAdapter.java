package com.moxiao.sqlmonitor.log.std;

import com.moxiao.sqlmonitor.log.Logger;

public class StandardLoggerAdapter implements Logger {

    public StandardLoggerAdapter(String clazz) {
        
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String message) {
        System.out.println(message);
    }

    @Override
    public void debug(String message) {
        System.out.println(message);
    }

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void warn(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }

    @Override
    public void error(String message, Throwable cause) {
        System.err.println(message);
        cause.printStackTrace(System.err);
    }
}
