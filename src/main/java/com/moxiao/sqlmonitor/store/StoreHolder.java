package com.moxiao.sqlmonitor.store;

import com.moxiao.sqlmonitor.store.es.ElasticStorePolicyImpl;
import com.moxiao.sqlmonitor.store.log.LoggingStorePolicyImpl;
import com.moxiao.sqlmonitor.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.*;

public class StoreHolder {
    
    private static List<StorePolicy> storePolicyList;

    public static synchronized void build(String storePolicy) {
        if (storePolicyList != null) {
            return;
        }
        if (storePolicy == null || storePolicy.isEmpty()) {
            storePolicyList = Collections.emptyList();
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(storePolicy, ",", false);
        HashSet<String> implSet = new HashSet<>(stringTokenizer.countTokens());
        storePolicyList = new ArrayList<>(stringTokenizer.countTokens());
        while (stringTokenizer.hasMoreTokens()) {
            String nextStorePolicyImpl = stringTokenizer.nextToken().trim();
            if (nextStorePolicyImpl.isEmpty() || implSet.contains(nextStorePolicyImpl)) {
                continue;
            }
            implSet.add(nextStorePolicyImpl);
            switch (nextStorePolicyImpl) {
                case "LOGGER" :
                    storePolicyList.add(new LoggingStorePolicyImpl());
                    break;
                case "ES":
                    storePolicyList.add(new ElasticStorePolicyImpl());
                    break;
                default:
                    defaultImpl(nextStorePolicyImpl);
            }
        }
    }

    private static void defaultImpl(String policy) {
        try {
            Class<? extends StorePolicy> storePolicyImpl = ClassUtils.getClazz(policy);
            Constructor<? extends StorePolicy> constructor = storePolicyImpl.getConstructor();
            storePolicyList.add(constructor.newInstance());
        } catch (Exception e) {
            //ignore
        }
    }

    public static List<StorePolicy> getStorePolicyList() {
        return storePolicyList;
    }
}
