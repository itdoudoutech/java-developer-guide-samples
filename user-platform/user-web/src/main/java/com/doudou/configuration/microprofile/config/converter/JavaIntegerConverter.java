package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class JavaIntegerConverter implements Converter<Integer> {

    @Override
    public Integer convert(String value) throws IllegalArgumentException, NullPointerException {
        if (null == value || value.isEmpty()) {
            throw new NullPointerException();
        }
        return Integer.parseInt(value);
    }
}
