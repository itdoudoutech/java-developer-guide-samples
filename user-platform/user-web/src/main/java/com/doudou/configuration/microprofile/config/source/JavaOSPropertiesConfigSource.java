package com.doudou.configuration.microprofile.config.source;

import java.util.Map;

public class JavaOSPropertiesConfigSource extends MapBaseConfigSource {

    public JavaOSPropertiesConfigSource() {
        super("Java OS Properties", 100);
    }

    @Override
    protected void parseConfigData(Map configData) {
        configData.putAll(System.getProperties());
    }
}
