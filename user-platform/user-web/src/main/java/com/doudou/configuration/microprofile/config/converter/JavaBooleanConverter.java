package com.doudou.configuration.microprofile.config.converter;


import org.eclipse.microprofile.config.spi.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaBooleanConverter implements Converter<Boolean> {

    private final Set<String> values = new HashSet<>(Arrays.asList("true", "1", "YES", "Y", "ON"));

    @Override
    public Boolean convert(String value) throws IllegalArgumentException, NullPointerException {
        // return Stream.of("true", "1", "YES", "Y", "ON").filter(p -> p.equals(value)).findFirst().orElse(null) == null;
        return values.contains(value);
    }
}
