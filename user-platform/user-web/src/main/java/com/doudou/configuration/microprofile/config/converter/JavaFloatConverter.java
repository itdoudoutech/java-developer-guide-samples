package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class JavaFloatConverter implements Converter<Float> {

    @Override
    public Float convert(String value) throws IllegalArgumentException, NullPointerException {
        if (null == value || value.isEmpty()) {
            throw new NullPointerException();
        }
        return Float.parseFloat(value);
    }
}
