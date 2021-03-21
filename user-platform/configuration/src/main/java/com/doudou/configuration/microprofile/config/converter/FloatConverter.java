package com.doudou.configuration.microprofile.config.converter;

public class FloatConverter extends DefaultAbstractConverter<Float> {

    @Override
    protected Float doConvert(String value) {
        return Float.valueOf(value);
    }
}
