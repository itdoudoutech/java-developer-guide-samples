package com.doudou.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ConfigSources implements Iterable<ConfigSource> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private boolean addedDefaultConfigSources;

    private boolean addedDiscoveredConfigSources;

    private final List<ConfigSource> configSources = new LinkedList<>();

    private ClassLoader classLoader;

    public ConfigSources(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addDefaultSources() {
        if (addedDefaultConfigSources) {
            return;
        }
        addConfigSources(JavaSystemPropertiesConfigSource.class, JavaOSPropertiesConfigSource.class,
                JavaLocalPropertiesConfigSource.class);
        addedDefaultConfigSources = true;
    }

    public void addDiscoveredSources() {
        if (addedDiscoveredConfigSources) {
            return;
        }
        addConfigSources(ServiceLoader.load(ConfigSource.class, classLoader));
        addedDiscoveredConfigSources = true;
    }

    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        ConfigSource[] configSources = Stream.of(configSourceClasses).map(this::newInstance).toArray(ConfigSource[]::new);
        addConfigSources(configSources);
    }

    public void addConfigSources(ConfigSource... configSources) {
        addConfigSources(Arrays.asList(configSources));
    }

    public void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
        this.configSources.sort(ConfigSourceOrdinalComparator.INSTANCE);
        this.configSources.forEach(p -> logger.info("addConfigSources: " + p.getName()));
    }

    @Override
    public Iterator<ConfigSource> iterator() {
        return configSources.iterator();
    }

    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance = null;
        try {
            instance = configSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }
}
