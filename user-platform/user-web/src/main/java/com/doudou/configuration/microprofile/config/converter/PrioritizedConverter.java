package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class PrioritizedConverter<T> implements Converter<T>, Comparable<PrioritizedConverter<T>> {

    private final Converter<T> converter;

    private final int ordinal;

    public PrioritizedConverter(Converter<T> converter, int ordinal) {
        this.converter = converter;
        this.ordinal = ordinal;
    }

    @Override
    public int compareTo(PrioritizedConverter<T> o) {
        return Integer.compare(o.getOrdinal(), this.getOrdinal());
    }

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        return converter.convert(value);
    }

    public Converter<T> getConverter() {
        return converter;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
