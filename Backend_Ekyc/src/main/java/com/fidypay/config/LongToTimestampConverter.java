package com.fidypay.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author prave
 * @Date 04-01-2024
 */
@Component
public class LongToTimestampConverter implements Converter<Long, Timestamp> {

    @Override
    public Timestamp convert(Long source) {

        if (source == null) {
            return null;
        }
        return new Timestamp(source);
    }
}
