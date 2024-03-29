package com.moxiao.sqlmonitor.store.log;

import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.store.StoreExecuteSql;
import com.moxiao.sqlmonitor.store.StorePolicy;

public class LoggingStorePolicyImpl implements StorePolicy {

    private static final Logger logger = LoggerFactory.getLog(LoggingStorePolicyImpl.class);
    
    @Override
    public void storeData(StoreExecuteSql storeExecuteSql) {
        if (logger.isDebugEnabled()) {
            logger.debug(storeExecuteSql.printSqlDataIncludeStack());
        }
    }

    @Override
    public void storeSlowData(StoreExecuteSql storeExecuteSql) {
        logger.warn("SQL语句的执行时间超过指定时间。SQL语句信息为：" + storeExecuteSql.printSqlDataIncludeStack());
    }
}
