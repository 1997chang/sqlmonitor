package com.moxiao.sqlmonitor.util;

import com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExecuteStackTool {

    private static final String ignorePrefix = "com.moxiao.sqlmonitor";
    
    private ExecuteStackTool(){}

    public static List<String> getExecuteStack(SqlMonitorProperty sqlMonitorProperty) {
        if (sqlMonitorProperty.getMonitorStackClass() != null) {
            return getExecuteStack(sqlMonitorProperty.getMonitorStackClass());
        } else if (sqlMonitorProperty.getMonitorStackPrefix() != null) {
            return getExecuteStack(sqlMonitorProperty.getMonitorStackPrefix());
        } else {
            return getExecuteStack("");
        }
    }
    
    public static List<String> getExecuteStack() {
        return getExecuteStack("");
    }

    public static List<String> getExecuteStack(Class<?> clazz) {
        return getExecuteStack(clazz.getPackage().getName());
    }

    public static List<String> getExecuteStack(Package pack) {
        return getExecuteStack(pack.getName());
    }

    public static List<String> getExecuteStack(String prefix) {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        List<String> executorStack = new ArrayList<>(stackTrace.length);
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().startsWith(ignorePrefix)) {
                continue;
            }
            if (stackTraceElement.getClassName().startsWith(prefix)) {
                executorStack.add(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() 
                        + "[" + stackTraceElement.getLineNumber() + "]");
            }
            if (executorStack.size() > SqlMonitorInterceptor.getSqlMonitorProperty().getMaxStackLength()) {
                break;
            }
        }
        return Collections.unmodifiableList(executorStack);
    }
}
