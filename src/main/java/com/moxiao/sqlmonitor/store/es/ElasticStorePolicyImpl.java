package com.moxiao.sqlmonitor.store.es;

import com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor;
import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.property.ElasticsearchProperty;
import com.moxiao.sqlmonitor.property.SqlMonitorProperty;
import com.moxiao.sqlmonitor.store.StoreExecuteSql;
import com.moxiao.sqlmonitor.store.StorePolicy;
import com.moxiao.sqlmonitor.util.EsTools;
import com.moxiao.sqlmonitor.util.StringUtils;

public class ElasticStorePolicyImpl implements StorePolicy {

    private static final Logger logger = LoggerFactory.getLog(ElasticStorePolicyImpl.class);
    
    private static final boolean validate;
    
    static {
        SqlMonitorProperty sqlMonitorProperty = SqlMonitorInterceptor.getSqlMonitorProperty();
        ElasticsearchProperty esConfig = sqlMonitorProperty.getEsConfig();
        if (esConfig == null || StringUtils.isBlank(esConfig.getUri())) {
            logger.warn("使用ES进行存储，但是没有进行ES的配置");
            validate = false;
        } else {
            validate = EsTools.createClient(esConfig);
        } 
    }
    
    @Override
    public void storeData(StoreExecuteSql storeExecuteSql) {
        if (!validate) {
            return;
        }
        String indexName = SqlMonitorInterceptor.getSqlMonitorProperty().getEsConfig().getIndexName();
        EsTools.add(storeExecuteSql, indexName);
    }
}
