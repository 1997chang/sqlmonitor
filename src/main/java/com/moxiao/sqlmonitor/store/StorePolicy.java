package com.moxiao.sqlmonitor.store;

public interface StorePolicy {

    /**
     * 当SQL语句执行完时，将SQL语句的信息进行存储
     * @param storeExecuteSql 存储SQL语句内容
     */
    void storeData(StoreExecuteSql storeExecuteSql);
    
}
