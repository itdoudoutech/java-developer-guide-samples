package com.doudou.configuration.microprofile.config;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JavaConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private final List<ConfigSource> configSources = new LinkedList<>();
    private final Map<String, Converter> converters = new HashMap<>();

    private static final Comparator<ConfigSource> configSourceComparator =
            (o1, o2) -> Integer.compare(o2.getOrdinal(), o1.getOrdinal());

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> configServiceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        configServiceLoader.forEach(configSources::add);

        // 排序
        configSources.sort(configSourceComparator);

        ServiceLoader<Converter> converterServiceLoader = ServiceLoader.load(Converter.class, classLoader);
        converterServiceLoader.forEach(p -> {
            Type[] interfaceTypes = p.getClass().getGenericInterfaces();
            for (Type interfaceType : interfaceTypes) {
                if (ParameterizedType.class.isAssignableFrom(interfaceType.getClass())) {
                    ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                    if (parameterizedType.getRawType().getTypeName().equals(Converter.class.getName())) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        converters.put(actualTypeArguments[0].getTypeName(), p);
                    }
                }
            }
        });
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        Optional<Converter<T>> converter = getConverter(propertyType);
        return converter.map(tConverter -> tConverter.convert(propertyValue)).orElse(null);
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    protected String getPropertyValue(String propertyName) {
        for (ConfigSource configSource : configSources) {
            String propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                return propertyValue;
            }
        }
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return null;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        return Optional.ofNullable(converters.get(forType.getTypeName()));
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
