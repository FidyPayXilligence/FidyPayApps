package com.fidypay.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * @author prave
 * @Date 08-09-2023
 */
@Component
@ReadingConverter
public class StringToLongConverter implements Converter<String, Long> {


    @Override
    public Long convert(String source) {
        try {
            return Long.parseLong(source);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
