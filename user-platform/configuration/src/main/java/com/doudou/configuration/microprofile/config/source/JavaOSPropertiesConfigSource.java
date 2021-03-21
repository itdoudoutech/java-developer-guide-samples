package com.doudou.configuration.microprofile.config.source;

import java.util.Map;

public class JavaOSPropertiesConfigSource extends MapBaseConfigSource {

    public JavaOSPropertiesConfigSource() {
        super("Java OS Properties Config Source", 100);
    }

    @Override
    protected void parseConfigData(Map configData) {
        logger.info("Load config from OS properties success");
        configData.putAll(System.getProperties());
    }
}
