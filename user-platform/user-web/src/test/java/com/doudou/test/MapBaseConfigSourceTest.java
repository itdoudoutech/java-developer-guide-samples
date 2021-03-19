package com.doudou.test;

import com.doudou.configuration.microprofile.config.source.JavaLocalPropertiesConfigSource;
import com.doudou.configuration.microprofile.config.source.MapBaseConfigSource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

public class MapBaseConfigSourceTest {

    @Test
    public void testReadLocalProperties() {
        MapBaseConfigSource source = new JavaLocalPropertiesConfigSource();
        Map<String, String> properties = source.getProperties();
        assertNotNull(properties);
        assertNotEquals(0, properties.size());
    }

}
