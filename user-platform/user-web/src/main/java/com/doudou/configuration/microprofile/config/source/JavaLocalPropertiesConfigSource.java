package com.doudou.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JavaLocalPropertiesConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public JavaLocalPropertiesConfigSource() {
        this.properties = new HashMap<>();
        try {
            Properties properties = new Properties();
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("META-INF/config.properties");
            properties.load(in);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                this.properties.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return "Java System Properties";
    }

    @Override
    public int getOrdinal() {
        return 10;
    }
}
