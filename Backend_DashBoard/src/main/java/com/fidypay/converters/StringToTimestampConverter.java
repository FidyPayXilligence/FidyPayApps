package com.fidypay.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author prave
 * @Date 08-09-2023
 */
@Component
public class StringToTimestampConverter implements Converter<String, Timestamp> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Timestamp convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            java.util.Date parsedDate = dateFormat.parse(source);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Invalid date format. Please provide a date in the format 'yyyy-MM-dd", ex);
        }
    }
}
