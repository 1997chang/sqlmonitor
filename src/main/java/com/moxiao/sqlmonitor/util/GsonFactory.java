package com.moxiao.sqlmonitor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

class GsonFactory {
    
    private GsonFactory() {}

    public static Gson build() {
        return new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, 
                        (JsonSerializer<ZonedDateTime>) (src, typeOfSrc, context) -> 
                                new JsonPrimitive(src.format(ISO_OFFSET_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, 
                        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                                new JsonPrimitive(src.format(DateUtils.YYYY_MM_DD_HH_MM_SS_FORMAT)))
                .create();
    }
    
}
