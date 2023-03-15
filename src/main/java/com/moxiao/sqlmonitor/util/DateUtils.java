package com.moxiao.sqlmonitor.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class DateUtils {

    private DateUtils(){}
    
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String HH_MM_SS = "HH:mm:ss";

    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FORMAT = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static final DateTimeFormatter YYYY_MM_DD_FORMAT = DateTimeFormatter.ofPattern(YYYY_MM_DD);

    public static final DateTimeFormatter HH_MM_SS_FORMAT = DateTimeFormatter.ofPattern(HH_MM_SS);

    public static final DateTimeFormatter YYYY_MM_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM");

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(YYYY_MM_DD_HH_MM_SS_FORMAT);
    }

    public static LocalDateTime parseLocalDateTime(String value) {
        return LocalDateTime.parse(value, YYYY_MM_DD_HH_MM_SS_FORMAT);
    }

    public static String formatLocalDate(LocalDate localDate) {
        return localDate.format(YYYY_MM_DD_FORMAT);
    }

    public static LocalDate parseLocalDate(String value) {
        return LocalDate.parse(value, YYYY_MM_DD_FORMAT);
    }

    public static String formatLocalTime(LocalTime localTime) {
        return localTime.format(HH_MM_SS_FORMAT);
    }

    public static LocalTime parseLocalTime(String value) {
        return LocalTime.parse(value, HH_MM_SS_FORMAT);
    }

    public static String formatDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        int year = zdt.getYear();
        int month = zdt.getMonthValue();
        int day = zdt.getDayOfMonth();
        int hour = zdt.getHour();
        int minute = zdt.getMinute();
        int second = zdt.getSecond();
        int millis = zdt.getNano() / 1000_000;
        int offsetSeconds = zdt.getOffset().getTotalSeconds();
        
        //毫秒的长度，包含一个'.'
        int millislen;
        if (millis == 0) {
            millislen = 0;
        } else if (millis < 10) {
            millislen = 4;
        } else {
            if (millis % 100 == 0) {
                millislen = 2;
            } else if (millis % 10 == 0) {
                millislen = 3;
            } else {
                millislen = 4;
            }
        }
        int zonelen = offsetSeconds == 0 ? 1: 6;
        
        int len = 21 + millislen + zonelen;
        char[] chars = new char[len];
        
        chars[0] = '"';
        chars[1] = (char) (year / 1000 + '0');
        chars[2] = (char) ((year / 100) % 10 + '0');
        chars[3] = (char) ((year / 10) % 10 + '0');
        chars[4] = (char) (year % 10 + '0');
        chars[5] = '-';
        chars[6] = (char) (month / 10 + '0');
        chars[7] = (char) (month % 10 + '0');
        chars[8] = '-';
        chars[9] = (char) (day / 10 + '0');
        chars[10] = (char) (day % 10 + '0');
        chars[11] = 'T';
        chars[12] = (char) (hour / 10 + '0');
        chars[13] = (char) (hour % 10 + '0');
        chars[14] = ':';
        chars[15] = (char) (minute / 10 + '0');
        chars[16] = (char) (minute % 10 + '0');
        chars[17] = ':';
        chars[18] = (char) (second / 10 + '0');
        chars[19] = (char) (second % 10 + '0');
        if (millislen > 0) {
            chars[20] = '.';
            if (millislen == 2) {
                chars[21] = (char) (millis / 100 + '0');
            } else if (millislen == 3) {
                chars[21] = (char) (millis / 100 + '0');
                chars[22] = (char) ((millis / 10) % 10 + '0');
            } else {
                chars[21] = (char) (millis / 100 + '0');
                chars[22] = (char) ((millis / 10) % 10 + '0');
                chars[23] = (char) (millis % 100 + '0');
            } 
        }
        if (offsetSeconds == 0) {
            chars[20 + millislen] = 'Z';
        } else {
            int offset = offsetSeconds / 3600;
            int offsetAbs = Math.abs(offset);
            if (offset >= 0) {
                chars[20 + millislen] = '+';
            } else {
                chars[20 + millislen] = '-';
            }
            chars[21 + millislen] = (char) (offsetAbs / 10 + '0');
            chars[22 + millislen] = (char) (offsetAbs % 10 + '0');
            chars[23 + millislen] = ':';
            int offsetMinutes = (offsetSeconds - offset * 3600) / 60;
            if (offsetMinutes < 0) {
                offsetMinutes = -offsetMinutes;
            }
            chars[24 + millislen] = (char) (offsetMinutes / 10 + '0');
            chars[25 + millislen] = (char) (offsetMinutes % 10 + '0');
        }
        chars[chars.length - 1] = '"';
        return new String(chars);
    }

    public static Date formatDate(String value) {
        ZonedDateTime parse = ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return Date.from(parse.toInstant());
    }

    public static int getMilliInterval(LocalDateTime startTime, LocalDateTime endTime) {
        long interval = ChronoUnit.MILLIS.between(startTime, endTime);
        if (interval > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) interval;
        }
    }
    
}
