package com.doudou.configuration.microprofile.config.converter;


import org.eclipse.microprofile.config.spi.Converter;

public class JavaStringConverter implements Converter<String> {

    @Override
    public String convert(String value) throws IllegalArgumentException, NullPointerException {
        return value;
    }
}
