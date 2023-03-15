package com.moxiao.sqlmonitor.log;

public class LoggerException extends RuntimeException {

    public LoggerException() {
        super();
    }

    public LoggerException(String message) {
        super(message);
    }

    public LoggerException(Throwable cause) {
        super(cause);
    }

    public LoggerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
