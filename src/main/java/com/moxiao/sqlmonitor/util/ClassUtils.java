package com.moxiao.sqlmonitor.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ClassUtils {

    private static final Map<String, Class<?>> classMap = new HashMap<>();
    
    private static final Set<Class<?>> SIMPLE_TYPE_SET = new HashSet<>();
    
    static {
        SIMPLE_TYPE_SET.add(Byte.class);
        SIMPLE_TYPE_SET.add(Short.class);
        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(Boolean.class);
    }
    
    private ClassUtils() {}

    public static <T> Class<T> getClazz(String clazz) throws ClassNotFoundException {
        Class<T> result;
        if (classMap.containsKey(clazz)) {
            result = (Class<T>) classMap.get(clazz);
        } else {
            result = (Class<T>) Class.forName(clazz, true, ClassUtils.class.getClassLoader());
            classMap.put(clazz, result);
        }
        return result;
    }

    public static boolean isSimpleClass(Class<?> clazz) {
        return SIMPLE_TYPE_SET.contains(clazz);
    }

    public static boolean hasClass(String clazz) {
        try {
            getClazz(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
