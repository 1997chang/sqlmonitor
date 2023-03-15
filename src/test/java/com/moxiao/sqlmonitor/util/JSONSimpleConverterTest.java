package com.moxiao.sqlmonitor.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JSONSimpleConverterTest {

    @Test
    public void intJsonTest() {
        Assertions.assertEquals("1", JSONSimpleConverter.CONVERTOR.get(Integer.class).transform(1));
    }

    @Test
    public void stringJsonTest() {
        String content = "json";
        Assertions.assertEquals("\"" + content +"\"", JSONSimpleConverter.CONVERTOR.get(String.class).transform(content));
    }
    
}