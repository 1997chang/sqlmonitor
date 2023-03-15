package com.moxiao.sqlmonitor.notice;

import com.moxiao.sqlmonitor.store.StoreExecuteSql;

import java.util.List;

public interface NoticePolicy {

    /**
     * 当SQL语句执行完进行通知
     * @param storeExecuteSql 通知执行的SQL语句
     */
    void notice(StoreExecuteSql storeExecuteSql);

    /**
     * 当慢SQL监控检测到慢SQL语句进行的通知。<br/>
     * 当一个SQL语句执行时间大于executeTimeLimit阈值时，进行通知，提前检测到慢SQL语句
     * @param slowSqlEntity 慢SQL语句
     */
    void slowSqlNotice(List<SlowSqlEntity> slowSqlEntity);
}
