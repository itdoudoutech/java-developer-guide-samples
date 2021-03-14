package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class JavaByteConverter implements Converter<Byte> {

    @Override
    public Byte convert(String value) throws IllegalArgumentException, NullPointerException {
        if (null == value || value.isEmpty()) {
            throw new NullPointerException();
        }
        return Byte.parseByte(value);
    }
}
