package com.moxiao.sqlmonitor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.*;

import java.time.*;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

class JacksonFactory {

    private JacksonFactory() {}

    public static void build(ObjectMapper objectMapper) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //序列化
        javaTimeModule.addSerializer(ZonedDateTime.class, 
                new ZonedDateTimeSerializer(ISO_OFFSET_DATE_TIME));
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateUtils.YYYY_MM_DD_HH_MM_SS_FORMAT));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateUtils.YYYY_MM_DD_FORMAT));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateUtils.HH_MM_SS_FORMAT));
        javaTimeModule.addSerializer(YearMonth.class,
                new YearMonthSerializer(DateUtils.YYYY_MM_FORMAT));
        
        //反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateUtils.YYYY_MM_DD_HH_MM_SS_FORMAT));
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateUtils.YYYY_MM_DD_FORMAT));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateUtils.HH_MM_SS_FORMAT));
        javaTimeModule.addDeserializer(YearMonth.class,
                new YearMonthDeserializer(DateUtils.YYYY_MM_FORMAT));
        
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(javaTimeModule);
    }
    
}
