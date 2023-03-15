package com.moxiao.sqlmonitor.property;

import com.moxiao.sqlmonitor.notice.dingding.DingDingType;
import com.moxiao.sqlmonitor.util.ClassUtils;
import com.moxiao.sqlmonitor.util.MybatisUtils;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonitorPropertyTest {

    @Test
    public void buildBySimpleProperty() {
        Properties properties = new Properties();
        properties.put("executeTimeLimit", 1000);
        properties.put("slowSqlMonitor", "2000");
        SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();
        assertEquals(5000, sqlMonitorProperty.getExecuteTimeLimit());
        assertEquals(5000, sqlMonitorProperty.getSlowSqlMonitor());
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertEquals(1000, sqlMonitorProperty.getExecuteTimeLimit());
        assertEquals(2000, sqlMonitorProperty.getSlowSqlMonitor());
    }

    @Test
    public void buildByClassProperty() {
        Properties properties = new Properties();
        SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();
        properties.put("monitorStackClass", "com.moxiao.sqlmonitor.util.ClassUtils");

        assertNull(sqlMonitorProperty.getMonitorStackClass());
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertNotNull(sqlMonitorProperty.getMonitorStackClass());
        assertEquals(ClassUtils.class, sqlMonitorProperty.getMonitorStackClass());
        
    }

    @Test
    public void buildByNestedProperty() {
        Properties properties = new Properties();
        SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();

        properties.put("threadPoolConfig.corePoolSize", "5");
        
        assertNull(sqlMonitorProperty.getThreadPoolConfig());
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertNotNull(sqlMonitorProperty.getThreadPoolConfig());
        assertEquals(5, sqlMonitorProperty.getThreadPoolConfig().getCorePoolSize());
        assertEquals(60000L, sqlMonitorProperty.getThreadPoolConfig().getKeepAliveTime());
        assertEquals("LOGGER", sqlMonitorProperty.getThreadPoolConfig().getRejectedExecutionPolicy());
    }

    @Test
    public void buildByNestedPropertyInRejectPolicy() {
        Properties properties = new Properties();
        SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();
        properties.put("threadPoolConfig.rejectedExecutionPolicy", "abort");
        assertNull(sqlMonitorProperty.getThreadPoolConfig());
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertEquals("ABORT", sqlMonitorProperty.getThreadPoolConfig().getRejectedExecutionPolicy());

        properties.put("threadPoolConfig.rejectedExecutionPolicy", "abort1");
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertEquals("LOGGER", sqlMonitorProperty.getThreadPoolConfig().getRejectedExecutionPolicy());

        assertTrue(sqlMonitorProperty.getThreadPoolConfig().getDaemon());
        properties.put("threadPoolConfig.daemon", false);
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertFalse(sqlMonitorProperty.getThreadPoolConfig().getDaemon());
    }

    @Test
    public void buildNestedDingding() {
        Properties properties = new Properties();
        SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();
        properties.put("dingdingConfig.type", "TEXT1");
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertEquals(DingDingType.MARKDOWN, sqlMonitorProperty.getDingdingConfig().getType());

        properties.put("dingdingConfig.type", "TEXT");
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertEquals(DingDingType.TEXT, sqlMonitorProperty.getDingdingConfig().getType());
    }

    @Test
    public void buildListDingding() {
        Properties properties = new Properties();
        SqlMonitorProperty sqlMonitorProperty = new SqlMonitorProperty();
        properties.put("dingdingConfig.atMobiles", "1212, 45, 451  ,  55");
        MybatisUtils.setProperty(properties, sqlMonitorProperty);
        assertEquals(4, sqlMonitorProperty.getDingdingConfig().getAtMobiles().size());
        assertEquals("1212", sqlMonitorProperty.getDingdingConfig().getAtMobiles().get(0));
        assertEquals("45", sqlMonitorProperty.getDingdingConfig().getAtMobiles().get(1));
        assertEquals("451", sqlMonitorProperty.getDingdingConfig().getAtMobiles().get(2));
        assertEquals("55", sqlMonitorProperty.getDingdingConfig().getAtMobiles().get(3));
    }

}