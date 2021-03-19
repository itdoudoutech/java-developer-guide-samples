package com.doudou.configuration.microprofile.config.converter;

public class DoubleConverter extends DefaultAbstractConverter<Double> {

    @Override
    protected Double doConvert(String value) {
        return Double.valueOf(value);
    }
}
