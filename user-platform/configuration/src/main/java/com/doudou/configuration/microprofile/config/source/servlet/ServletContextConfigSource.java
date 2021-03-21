package com.doudou.configuration.microprofile.config.source.servlet;

import com.doudou.configuration.microprofile.config.source.MapBaseConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

public class ServletContextConfigSource extends MapBaseConfigSource {

    // TODO 这里不可以通过构造方法传入 servletContext
    // 暂时用 lazyLoad 解决
    private final ServletContext servletContext;

    public ServletContextConfigSource(ServletContext servletContext) {
        super("Servlet Context Config Source", 400, true);
        this.servletContext = servletContext;
        parseConfigData(super.data);
    }

    @Override
    protected void parseConfigData(Map configData) {
        Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String parameterName = initParameterNames.nextElement();
            configData.put(parameterName, servletContext.getInitParameter(parameterName));
        }
    }
}
