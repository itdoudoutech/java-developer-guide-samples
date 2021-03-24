package com.doudou.configuration.microprofile.config.source;

import com.doudou.configuration.microprofile.config.source.servlet.ServletContextConfigSource;
import com.doudou.context.WebApplicationContextListener;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

@Priority(value = 1)
public class ConfigSourceInitializeListener implements WebApplicationContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver provider = ConfigProviderResolver.instance();
        ConfigBuilder builder = provider.getBuilder();
        // 同步 ClassLoader
        builder.forClassLoader(classLoader);
        // 配置 ConfigSource
        builder.addDefaultSources();
        builder.addDiscoveredSources();
        builder.withSources(new ServletContextConfigSource(servletContext));
        // 配置 Converter
        builder.addDiscoveredConverters();
        // 注册 Config
        provider.registerConfig(builder.build(), classLoader);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
