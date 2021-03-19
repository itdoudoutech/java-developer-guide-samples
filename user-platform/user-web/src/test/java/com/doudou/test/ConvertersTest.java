package com.doudou.test;

import com.doudou.configuration.microprofile.config.converter.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConvertersTest {

    @Test
    public void testResolveConvertedType() {
        Converters converters = new Converters();

        assertEquals(Byte.class, converters.resolveConvertedType(new ByteConverter()));
        assertEquals(Short.class, converters.resolveConvertedType(new ShortConverter()));
        assertEquals(Integer.class, converters.resolveConvertedType(new IntegerConverter()));
        assertEquals(Long.class, converters.resolveConvertedType(new LongConverter()));
        assertEquals(Float.class, converters.resolveConvertedType(new FloatConverter()));
        assertEquals(Double.class, converters.resolveConvertedType(new DoubleConverter()));
        assertEquals(String.class, converters.resolveConvertedType(new StringConverter()));
        assertEquals(Boolean.class, converters.resolveConvertedType(new BooleanConverter()));
    }

}
