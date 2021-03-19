package com.doudou.configuration.microprofile.config.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class JavaLocalPropertiesConfigSource extends MapBaseConfigSource {

    private static final String MICROPROFILE_CONFIG_FILE_URL = "META-INF/config.properties";

    public JavaLocalPropertiesConfigSource() {
        super("Java Local Properties", 300);
    }

    @Override
    protected void parseConfigData(Map configData) {
        try {
            Properties properties = new Properties();
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(MICROPROFILE_CONFIG_FILE_URL);
            properties.load(in);
            configData.putAll(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
