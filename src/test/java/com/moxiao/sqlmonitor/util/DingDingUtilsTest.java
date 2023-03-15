package com.moxiao.sqlmonitor.util;

import com.moxiao.sqlmonitor.property.DingDingProperty;
import org.junit.jupiter.api.Test;

public class DingDingUtilsTest {

    @Test
    public void sendText() {
        DingDingProperty dingDingProperty = new DingDingProperty();
        dingDingProperty.setSecret("SECd115fa66c6782b3e6bd361a73ee9a66bd53bb3697466cbb6457c27e335799f1a");
        dingDingProperty.setAccessToken("10f12cead3ce688dc030a35ad584f90aed07af401bd918b652c99a2180e77f2b");
        String response = DingDingUtils.sendText(dingDingProperty, "测试内容");
        System.out.println(response);
    }

    @Test
    public void sendMarkDown() {
        DingDingProperty dingDingProperty = new DingDingProperty();
        dingDingProperty.setSecret("SECd115fa66c6782b3e6bd361a73ee9a66bd53bb3697466cbb6457c27e335799f1a");
        dingDingProperty.setAccessToken("10f12cead3ce688dc030a35ad584f90aed07af401bd918b652c99a2180e77f2b");
        String response = DingDingUtils.sendMarkDown(dingDingProperty, "标题", "# SQL语句超时 \n 执行时间为：5s");
        System.out.println(response);
    }
    
}