package com.doudou.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JavaOSPropertiesConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public JavaOSPropertiesConfigSource() {
        this.properties = new HashMap<>();
        System.getenv().forEach((k, v) -> properties.put(k, k));
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "Java OS Properties";
    }

    @Override
    public int getOrdinal() {
        return 0;
    }
}
