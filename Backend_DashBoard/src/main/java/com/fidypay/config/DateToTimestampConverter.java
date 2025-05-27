package com.fidypay.config;

import org.springframework.core.convert.converter.Converter;

/**
 * @author prave
 * @Date 27-08-2023
 */
public class DateToTimestampConverter implements Converter<java.util.Date, java.sql.Timestamp> {

    @Override
    public java.sql.Timestamp convert(java.util.Date date) {
        return new java.sql.Timestamp(date.getTime());
    }
    
//	@Override
//    public Timestamp convert(DateToTimestampConverter source) {
//        if (source == null) {
//            return null;
//        }
//        return new Timestamp(source.getTime());
//    }
}

