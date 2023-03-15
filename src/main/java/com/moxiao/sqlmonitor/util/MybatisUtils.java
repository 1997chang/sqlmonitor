package com.moxiao.sqlmonitor.util;

import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class MybatisUtils {

    private static final Logger logger = LoggerFactory.getLog(MybatisUtils.class);
    
    public static volatile Configuration configuration;

    public static void setConfig(Configuration config) {
        if (configuration == null) {
            synchronized (MybatisUtils.class) {
                if (configuration == null) {
                    configuration = config;
                }
            }
        }
    }

    public static void setProperty(Properties properties, SqlMonitorProperty sqlMonitorProperty) {
        MetaObject metaObject = SystemMetaObject.forObject(sqlMonitorProperty);
        
        for (Object key : properties.keySet()) {
            String propertyName = key.toString();
            if (metaObject.hasSetter(propertyName)) {
                setValue(properties.getProperty(propertyName), propertyName, metaObject);
            } else {
                logger.warn("忽略SQL-Monitor中属性配置，配置名：" + propertyName);
            }
        }
    }

    private static void setValue(String value, String propertyName, MetaObject metaObject) {
        Class<?> type = metaObject.getSetterType(propertyName);
        if (Class.class.equals(type)) {
            try {
                Class<?> clazz = ClassUtils.getClazz(value);
                metaObject.setValue(propertyName, clazz);
            } catch (ClassNotFoundException e) {
                logger.warn("设置属性：" + propertyName + "没有找到对应的Class，ignore");
            }
        } else if (type.isAssignableFrom(String.class)) {
            metaObject.setValue(propertyName, value);
        } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            metaObject.setValue(propertyName, Boolean.valueOf(value));
        } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            metaObject.setValue(propertyName, Integer.valueOf(value));
        } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            metaObject.setValue(propertyName, Long.valueOf(value));
        } else if (List.class.equals(type)) {
            StringTokenizer stringTokenizer = new StringTokenizer(value, ", ", false);
            List<Object> atMobiles = new ArrayList<>(stringTokenizer.countTokens());
            while (stringTokenizer.hasMoreTokens()) {
                atMobiles.add(stringTokenizer.nextToken());
            }
            metaObject.setValue(propertyName, atMobiles);
        }
    }

    public static TypeHandlerRegistry getTypeHandlerRegistry() {
        return configuration.getTypeHandlerRegistry();
    } 
    
}
