package com.moxiao.sqlmonitor.notice.dingding;

import com.moxiao.sqlmonitor.executor.CustomizedThreadFactory;
import com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.notice.NoticePolicy;
import com.moxiao.sqlmonitor.notice.SlowSqlEntity;
import com.moxiao.sqlmonitor.property.DingDingProperty;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;
import com.moxiao.sqlmonitor.store.StoreExecuteSql;
import com.moxiao.sqlmonitor.util.ClassUtils;
import com.moxiao.sqlmonitor.util.DingDingUtils;
import com.moxiao.sqlmonitor.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DingDingNoticePolicyImpl implements NoticePolicy {

    private static final Logger logger = LoggerFactory.getLog(DingDingNoticePolicyImpl.class);
    
    private static final int TIME_DELAY = 5;
    
    private static boolean validate = true;
    
    private static boolean collectionAble;
    
    static {
        try {
            ClassUtils.getClazz("okhttp3.OkHttpClient");
        } catch (ClassNotFoundException e) {
            validate = false;
        }
        if (validate) {
            SqlMonitorProperty sqlMonitorProperty = SqlMonitorInterceptor.getSqlMonitorProperty();
            DingDingProperty dingdingConfig = sqlMonitorProperty.getDingdingConfig();
            if (dingdingConfig == null || StringUtils.isBlank(dingdingConfig.getSecret())) {
                logger.warn("不能发送消息，钉钉配置没有进行配置");
                validate = false;
            } else {
                collectionAble = dingdingConfig.isCollectionAble();
            }
        }
        
    }
    
    @Override
    public void notice(StoreExecuteSql storeExecuteSql) {
        if (!validate) {
            return;
        }
        if (!collectionAble) {
            sendContext(storeExecuteSql.dingdingNotice());
        } else {
            //钉钉发送消息有时间限制，如果不进行合并的话，就会进行限流，
            // 每个机器人每分钟最多发送20条消息到群里，在这里进行每5秒进行合并一条发送，从而减少到12条左右
            CollectionThreadPool.addStoreExecuteSql(storeExecuteSql);
        }
    }

    @Override
    public void slowSqlNotice(List<SlowSqlEntity> slowSqlEntity) {
        if (!validate) {
            return;
        }
        String collectionContext = slowSqlEntity
                .stream()
                .map(SlowSqlEntity::dingdingNotice)
                .collect(Collectors.joining("\n"));
        sendContext(collectionContext);
    }

    private static void sendContext(String context) {
        SqlMonitorProperty sqlMonitorProperty = SqlMonitorInterceptor.getSqlMonitorProperty();
        DingDingProperty dingdingConfig = sqlMonitorProperty.getDingdingConfig();
        switch (dingdingConfig.getType()) {
            case TEXT:
                DingDingUtils.sendText(dingdingConfig, context);
                break;
            case MARKDOWN:
                DingDingUtils.sendMarkDown(dingdingConfig, "慢SQL警告", context);
                break;
        }
    }
    
    private static class CollectionThreadPool {

        private static final BlockingQueue<StoreExecuteSql> blockQueue = new LinkedBlockingQueue<>(100);

        private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1,
                new CustomizedThreadFactory("sql-monitor-sendDingding"));
        
        static {
            scheduled.scheduleWithFixedDelay(() -> {
                if (blockQueue.isEmpty()) {
                    return;
                }
                ArrayList<StoreExecuteSql> allData = new ArrayList<>(blockQueue.size());
                blockQueue.drainTo(allData);
                String collectionContext = allData
                        .stream()
                        .map(StoreExecuteSql::dingdingNotice)
                        .collect(Collectors.joining("\n"));
                sendContext(collectionContext);
            }, TIME_DELAY, TIME_DELAY, TimeUnit.SECONDS);
        }

        public static void addStoreExecuteSql(StoreExecuteSql storeExecuteSql) {
            boolean success = blockQueue.offer(storeExecuteSql);
            if (!success) {
                logger.warn("添加钉钉通知失败，队列满，直接发送");
                sendContext(storeExecuteSql.dingdingNotice());
            }
        }
        
    }
}
