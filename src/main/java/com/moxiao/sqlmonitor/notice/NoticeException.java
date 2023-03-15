package com.moxiao.sqlmonitor.notice;

public class NoticeException extends RuntimeException {

    public NoticeException() {
        super();
    }

    public NoticeException(String message) {
        super(message);
    }

    public NoticeException(Throwable cause) {
        super(cause);
    }

    public NoticeException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
