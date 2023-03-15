package com.moxiao.sqlmonitor.util;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.StringJoiner;

import static com.moxiao.sqlmonitor.util.JSONSimpleConverter.CONVERTOR;

public class JSONUtils {

    private static final Logger logger = LoggerFactory.getLog(JSONUtils.class);
    
    private static boolean IS_JSON2 = true;
    
    private static boolean IS_JSON = false;
    
    private static Gson gson;
    
    private static ObjectMapper objectMapper;

    static {
        try {
            ClassUtils.getClazz("com.alibaba.fastjson2.JSON");
            logger.info("使用fastjson2进行JSON转化");
        } catch (ClassNotFoundException e) {
            IS_JSON2 = false;
            try {
                ClassUtils.getClazz("com.alibaba.fastjson.JSON");
                IS_JSON = true;
                logger.info("使用fastjson进行JSON转化");
            } catch (ClassNotFoundException ex) {
                // ignore
                try {
                    ClassUtils.getClazz("com.google.gson.Gson");
                    gson = GsonFactory.build();
                    logger.info("使用Gson进行JSON转化");
                } catch (ClassNotFoundException exc) {
                    try {
                        ClassUtils.getClazz("com.fasterxml.jackson.databind.ObjectMapper");
                        objectMapper = new ObjectMapper();
                        if (ClassUtils.hasClass("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")) {
                            JacksonFactory.build(objectMapper);
                        }
                        logger.info("使用jackson进行JSON转化");
                    } catch (ClassNotFoundException classNotFoundException) {
                        logger.info("使用简单JSON转化");
                    }
                }
            }
        }
    }
    
    public static String toJsonString(Object object) {
        if (IS_JSON2) {
            return JSON.toJSONString(object);
        } else if (IS_JSON) {
            return com.alibaba.fastjson.JSON.toJSONString(object);
        } else if (gson != null) {
            return gson.toJson(object);
        } else if (objectMapper != null) {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return nativeToJson(object);
        }
    }

    public static <T> T parseString(String jsonString, Class<T> type) {
        if (IS_JSON2) {
            return JSON.parseObject(jsonString, type);
        } else if (IS_JSON) {
            return com.alibaba.fastjson.JSON.parseObject(jsonString, type);
        } else if (gson != null) {
            return gson.fromJson(jsonString, type);
        } else if (objectMapper != null) {
            try {
                return objectMapper.readValue(jsonString, type);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return nativeParse(jsonString, type);
        }
    }

    private static String nativeToJson(Object object) {
        if (object == null) {
            return "null";
        }
        if (CONVERTOR.containsKey(object.getClass())) {
            return CONVERTOR.get(object.getClass()).transform(object);
        }
        if (object.getClass().isEnum()) {
            return ((Enum<?>) object).name();
        }
        MetaObject metaObject = SystemMetaObject.forObject(object);
        StringJoiner stringJoiner = new StringJoiner(",","{", "}");
        for (String getterName : metaObject.getGetterNames()) {
            Object value = metaObject.getValue(getterName);
            if (value == null) {
                continue;
            }
            stringJoiner.add("\"" + getterName + "\":" + nativeToJson(value));
        }
        return stringJoiner.toString();
    }

    /**
     * 反序列化有各种各样的安全问题，建议使用成熟的反序列内容
     * @param parseValue
     * @param type
     * @return
     * @param <T>
     */
    @Deprecated
    private static <T> T nativeParse(String parseValue, Class<T> type) {
        /*if (parseValue.equals("null")) {
            return null;
        }
        if (CONVERTOR.containsKey(type)) {
            return ((JSONSimpleConverter.Convertor<T>) CONVERTOR.get(type)).parse(parseValue);
        }*/
        return null;
    }
    
    
}
