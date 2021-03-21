package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public abstract class DefaultAbstractConverter<T> implements Converter<T> {

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        if(null == value){
            throw new NullPointerException("The value must not be null!");
        }
        return doConvert(value);
    }

    protected abstract T doConvert(String value);
}
