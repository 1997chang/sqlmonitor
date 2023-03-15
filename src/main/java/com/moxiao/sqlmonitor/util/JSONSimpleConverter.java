package com.moxiao.sqlmonitor.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONSimpleConverter {

    public static final Map<Class<?>, Convertor> CONVERTOR = new HashMap<>();
    
    static {
        CONVERTOR.put(Integer.class, new IntegerConvertor());
        CONVERTOR.put(Long.class, new LongConvertor());
        CONVERTOR.put(Float.class, new FloatConvertor());
        CONVERTOR.put(Double.class, new DoubleConvertor());
        CONVERTOR.put(Byte.class, new ByteConvertor());
        CONVERTOR.put(Character.class, new CharacterConvertor());
        CONVERTOR.put(Boolean.class, new BooleanConvertor());
        CONVERTOR.put(Short.class, new ShortConvertor());
        CONVERTOR.put(LocalDateTime.class, new LocalDateTimeConvertor());
        CONVERTOR.put(ZonedDateTime.class, new ZonedDateTimeConvertor());
        CONVERTOR.put(LocalDate.class, new LocalDateConvertor());
        CONVERTOR.put(LocalTime.class, new LocalTimeConvertor());
        CONVERTOR.put(Date.class, new DateConvertor());
        CONVERTOR.put(BigDecimal.class, new BigDecimalConvertor());
        CONVERTOR.put(BigInteger.class, new BigIntegerConvertor());
        CONVERTOR.put(List.class, new ListConvertor());
        CONVERTOR.put(String.class, new StringConvertor());
    }
    
    
    static abstract class Convertor<T> {
        
        abstract T parse(String value);
        
        String transform(T t) {
            return t.toString();
        }
    }
    
    static class IntegerConvertor extends Convertor<Integer> {

        @Override
        Integer parse(String value) {
            return Integer.valueOf(value);
        }
    }

    static class LongConvertor extends Convertor<Long> {

        @Override
        Long parse(String value) {
            return Long.valueOf(value);
        }
    }

    static class FloatConvertor extends Convertor<Float> {

        @Override
        Float parse(String value) {
            return Float.valueOf(value);
        }
    }
    
    static class DoubleConvertor extends Convertor<Double> {

        @Override
        Double parse(String value) {
            return Double.valueOf(value);
        }
    }

    static class ShortConvertor extends Convertor<Short> {

        @Override
        Short parse(String value) {
            return Short.valueOf(value);
        }
    }

    static class BooleanConvertor extends Convertor<Boolean> {

        @Override
        Boolean parse(String value) {
            return Boolean.valueOf(value);
        }
    }

    static class ByteConvertor extends Convertor<Byte> {

        @Override
        Byte parse(String value) {
            return Byte.valueOf(value);
        }
    }

    static class CharacterConvertor extends Convertor<Character> {

        @Override
        Character parse(String value) {
            if (value.charAt(0) == '"' && value.length() > 1) {
                return value.charAt(1);
            } else {
                return value.charAt(0);
            }
        }

        @Override
        String transform(Character character) {
            return "\"" + character + "\"";
        }
    }

    static class LocalDateTimeConvertor extends Convertor<LocalDateTime> {

        @Override
        LocalDateTime parse(String value) {
            return DateUtils.parseLocalDateTime(value);
        }

        @Override
        String transform(LocalDateTime localDateTime) {
            return DateUtils.formatLocalDateTime(localDateTime);
        }
    }

    static class ZonedDateTimeConvertor extends Convertor<ZonedDateTime> {

        @Override
        ZonedDateTime parse(String value) {
            return ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        @Override
        String transform(ZonedDateTime localDateTime) {
            return localDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }

    static class LocalDateConvertor extends Convertor<LocalDate> {

        @Override
        LocalDate parse(String value) {
            return DateUtils.parseLocalDate(value);
        }

        @Override
        String transform(LocalDate localDate) {
            return DateUtils.formatLocalDate(localDate);
        }
    }

    static class LocalTimeConvertor extends Convertor<LocalTime> {

        @Override
        LocalTime parse(String value) {
            return DateUtils.parseLocalTime(value);
        }

        @Override
        String transform(LocalTime localTime) {
            return DateUtils.formatLocalTime(localTime);
        }
    }

    static class DateConvertor extends Convertor<Date> {

        @Override
        Date parse(String value) {
            return DateUtils.formatDate(value);
        }

        @Override
        String transform(Date localDate) {
            return DateUtils.formatDate(localDate);
        }
    }

    static class BigDecimalConvertor extends Convertor<BigDecimal> {

        @Override
        BigDecimal parse(String value) {
            return new BigDecimal(value);
        }

        @Override
        String transform(BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        }
    }

    static class BigIntegerConvertor extends Convertor<BigInteger> {

        @Override
        BigInteger parse(String value) {
            return new BigInteger(value);
        }

        @Override
        String transform(BigInteger localDate) {
            return localDate.toString();
        }
    }

    static class ListConvertor extends Convertor<List> {

        @Override
        List parse(String value) {
            return null;
        }

        @Override
        String transform(List list) {
            return ((Stream<String>)list.stream().map(JSONUtils::toJsonString))
                    .collect(Collectors.joining(",", "[", "]"));
        }
    }

    static class StringConvertor extends Convertor<String> {

        @Override
        String parse(String value) {
            if (value.charAt(0) == '"' && value.length() > 1) {
                return value.substring(1, value.length() - 1);
            } else {
                return value;
            }
        }

        @Override
        String transform(String s) {
            return "\"" + s + "\"";
        }
    }
    
}
