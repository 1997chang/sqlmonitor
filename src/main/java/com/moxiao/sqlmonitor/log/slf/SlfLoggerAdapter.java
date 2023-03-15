package com.moxiao.sqlmonitor.log.slf;

import com.moxiao.sqlmonitor.log.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

public class SlfLoggerAdapter implements Logger {
    
    private Logger logger;

    public SlfLoggerAdapter(String clazz) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(clazz);
        
        if (logger instanceof LocationAwareLogger) {
            try {
                logger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class,
                        Throwable.class);
                this.logger = new SlfLocationLoggerAdapter((LocationAwareLogger)logger);
                return;
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
        this.logger = new SlfStandardLoggerAdapter(logger);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String message) {
        logger.trace(message);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Throwable cause) {
        logger.error(message, cause);
    }
}
