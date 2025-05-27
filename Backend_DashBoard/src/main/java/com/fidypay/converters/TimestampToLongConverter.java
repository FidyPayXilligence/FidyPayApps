package com.fidypay.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author prave
 * @Date 06-09-2023
 */
@Component
public class TimestampToLongConverter implements Converter<Timestamp, Long> {

    @Override
    public Long convert(Timestamp source) {

        if (source == null) {
            return null;
        }
        return source.getTime();
    }
}
