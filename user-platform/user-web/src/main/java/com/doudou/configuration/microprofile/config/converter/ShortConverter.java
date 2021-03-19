package com.doudou.configuration.microprofile.config.converter;

public class ShortConverter extends DefaultAbstractConverter<Short> {

    @Override
    protected Short doConvert(String value) {
        return Short.valueOf(value);
    }
}
