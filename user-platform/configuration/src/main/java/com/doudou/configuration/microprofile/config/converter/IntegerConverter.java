package com.doudou.configuration.microprofile.config.converter;

public class IntegerConverter extends DefaultAbstractConverter<Integer> {

    @Override
    protected Integer doConvert(String value) {
        return Integer.valueOf(value);
    }
}
