package com.moxiao.sqlmonitor.exception;

public class SqlMonitorPropertyException extends RuntimeException {

    public SqlMonitorPropertyException() {
        super();
    }

    public SqlMonitorPropertyException(String message) {
        super(message);
    }

    public SqlMonitorPropertyException(Throwable cause) {
        super(cause);
    }

    public SqlMonitorPropertyException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
