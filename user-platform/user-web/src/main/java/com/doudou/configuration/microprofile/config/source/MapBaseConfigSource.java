package com.doudou.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MapBaseConfigSource implements ConfigSource {

    private final String name;

    private final int ordinal;

    private final Map<String, String> data;

    public MapBaseConfigSource(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
        this.data = getProperties();
    }

    protected abstract void parseConfigData(Map configData);

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> configData = new HashMap<>();
        try {
            parseConfigData(configData);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        return Collections.unmodifiableMap(configData);
    }

    @Override
    public Set<String> getPropertyNames() {
        return data.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return data.get(propertyName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }
}
