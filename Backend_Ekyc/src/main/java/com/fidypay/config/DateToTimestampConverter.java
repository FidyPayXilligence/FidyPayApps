package com.fidypay.config;

import org.springframework.core.convert.converter.Converter;

public class DateToTimestampConverter implements Converter<java.util.Date, java.sql.Timestamp> {
    @Override
    public java.sql.Timestamp convert(java.util.Date date) {
        return new java.sql.Timestamp(date.getTime());
    }
}

