package com.moxiao.sqlmonitor.exception;

public class ElasticCommonException extends RuntimeException {

    public ElasticCommonException() {
        super();
    }

    public ElasticCommonException(String message) {
        super(message);
    }

    public ElasticCommonException(Throwable cause) {
        super(cause);
    }

    public ElasticCommonException(String message, Throwable cause) {
        super(message, cause);
    }
}
