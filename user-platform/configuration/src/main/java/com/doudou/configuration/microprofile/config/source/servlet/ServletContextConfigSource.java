package com.doudou.configuration.microprofile.config.source.servlet;

import com.doudou.configuration.microprofile.config.source.MapBaseConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

public class ServletContextConfigSource extends MapBaseConfigSource {

    private Map<String, String> data;

    public ServletContextConfigSource(ServletContext servletContext) {
        super("Servlet Context Config Source", 400);

        Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String parameterName = initParameterNames.nextElement();
            data.put(parameterName, servletContext.getInitParameter(parameterName));
        }
        data = null;
    }

    @Override
    protected void parseConfigData(Map configData) {
        this.data = configData;
    }
}
