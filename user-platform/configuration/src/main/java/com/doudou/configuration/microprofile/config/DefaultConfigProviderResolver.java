package com.doudou.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultConfigProviderResolver extends ConfigProviderResolver {

    private final ConcurrentMap<ClassLoader, Config> configsRepository = new ConcurrentHashMap<>();

    private final ConcurrentMap<ClassLoader, ConfigBuilder> configBuildersRepository = new ConcurrentHashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        return configsRepository.computeIfAbsent(resolveClassLoader(loader), this::newConfig);
    }

    @Override
    public ConfigBuilder getBuilder() {
        return newConfigBuilder(null);
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        configsRepository.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        List<ClassLoader> targetKeys = new ArrayList<>();
        for (ClassLoader key : configsRepository.keySet()) {
            if (Objects.equals(config, configsRepository.get(key))) {
                targetKeys.add(key);
            }
        }
        targetKeys.forEach(configsRepository::remove);
    }

    protected ConfigBuilder newConfigBuilder(ClassLoader classLoader) {
        final ClassLoader loader = resolveClassLoader(classLoader);
        return configBuildersRepository.computeIfAbsent(loader, t -> new DefaultConfigBuilder(loader));
    }

    protected Config newConfig(ClassLoader classLoader) {
        return newConfigBuilder(classLoader).build();
    }

    private ClassLoader resolveClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.getClass().getClassLoader() : classLoader;
    }
}
