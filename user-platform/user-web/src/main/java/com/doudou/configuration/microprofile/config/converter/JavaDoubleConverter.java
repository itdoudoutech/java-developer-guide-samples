package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class JavaDoubleConverter implements Converter<Double> {

    @Override
    public Double convert(String value) throws IllegalArgumentException, NullPointerException {
        if (null == value || value.isEmpty()) {
            throw new NullPointerException();
        }
        return Double.parseDouble(value);
    }
}
