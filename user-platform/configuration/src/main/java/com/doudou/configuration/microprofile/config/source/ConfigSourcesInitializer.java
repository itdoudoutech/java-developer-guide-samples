package com.doudou.configuration.microprofile.config.source;


import com.doudou.configuration.microprofile.config.source.servlet.ServletContextConfigSource;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class ConfigSourcesInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver provider = ConfigProviderResolver.instance();
        ConfigBuilder builder = provider.getBuilder();
        // 同步 ClassLoader
        builder.forClassLoader(classLoader);
        // 配置 ConfigSource
        builder.addDefaultSources();
        builder.withSources(new ServletContextConfigSource(servletContext));
        // 配置 Converter
        builder.addDiscoveredConverters();
        // 注册 Config
        provider.registerConfig(builder.build(), classLoader);
    }
}
