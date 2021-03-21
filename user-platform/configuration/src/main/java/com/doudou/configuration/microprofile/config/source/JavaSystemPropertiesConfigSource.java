package com.doudou.configuration.microprofile.config.source;

import java.util.Map;

public class JavaSystemPropertiesConfigSource extends MapBaseConfigSource {

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     */

    public JavaSystemPropertiesConfigSource() {
        super("Java System Properties Config Source", 200);
    }

    @Override
    protected void parseConfigData(Map configData) {
        logger.info("Load config from java system properties success");
        configData.putAll(System.getProperties());
    }
}
