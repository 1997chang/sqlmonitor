package com.moxiao.sqlmonitor.log.slf;

import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SlfLocationLoggerAdapter implements Logger {

    private static final Marker MARKER = MarkerFactory.getMarker(LoggerFactory.MARKER);
    
    private static final String FQCN = SlfLoggerAdapter.class.getName();
    
    private final LocationAwareLogger logger;
    
    public SlfLocationLoggerAdapter(LocationAwareLogger logger) {
        this.logger = logger;
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
        logger.log(MARKER, FQCN, LocationAwareLogger.TRACE_INT, message, null, null);
    }

    @Override
    public void debug(String message) {
        logger.log(MARKER, FQCN, LocationAwareLogger.DEBUG_INT, message, null, null);
    }

    @Override
    public void info(String message) {
        logger.log(MARKER, FQCN, LocationAwareLogger.INFO_INT, message, null, null);
    }

    @Override
    public void warn(String message) {
        logger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, message, null, null);
    }

    @Override
    public void error(String message) {
        logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, message, null, null);
    }

    @Override
    public void error(String message, Throwable cause) {
        logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, message, null, cause);
    }
}
