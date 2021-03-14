package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class JavaLongConverter implements Converter<Long> {

    @Override
    public Long convert(String value) throws IllegalArgumentException, NullPointerException {
        if (null == value || value.isEmpty()) {
            throw new NullPointerException();
        }
        return Long.parseLong(value);
    }
}
