package com.doudou.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

public class Converters implements Iterable<Converter> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public static final int DEFAULT_PRIORITY = 100;

    private final Map<Class<?>, PriorityQueue<PrioritizedConverter>> typedConverters = new HashMap<>();

    private ClassLoader classLoader;

    private boolean addedDiscoveredConverters;

    public Converters() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public Converters(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addDiscoveredConverters() {
        if (addedDiscoveredConverters) {
            return;
        }
        addConverters(ServiceLoader.load(Converter.class, classLoader));
        addedDiscoveredConverters = true;
    }

    public void addConverters(Iterable<Converter> converters) {
        converters.forEach(this::addConverter);
    }

    public void addConverter(Converter converter) {
        addConverter(converter, DEFAULT_PRIORITY);
    }

    public void addConverter(Converter converter, int priority) {
        Class<?> convertedType = resolveConvertedType(converter);
        addConverter(converter, priority, convertedType);
    }

    public void addConverter(Converter converter, int priority, Class<?> convertedType) {
        PriorityQueue<PrioritizedConverter> prioritizedConverters = typedConverters.computeIfAbsent(convertedType, p -> new PriorityQueue<>());
        prioritizedConverters.offer(new PrioritizedConverter(converter, priority));
        logger.info(String.format("add converter: convertedType = %s, converterName = %s",
                converter.getClass().getSimpleName(), converter.getClass().getSimpleName()));
    }

    public void addConverters(Converter... converters) {
        addConverters(Arrays.asList(converters));
    }

    public List<Converter> getConverters(Class<?> convertedType) {
        PriorityQueue<PrioritizedConverter> prioritizedConverters = typedConverters.get(convertedType);
        if (prioritizedConverters == null || prioritizedConverters.isEmpty()) {
            return Collections.emptyList();
        }
        List<Converter> converters = new LinkedList<>();
        for (PrioritizedConverter prioritizedConverter : prioritizedConverters) {
            converters.add(prioritizedConverter.getConverter());
        }
        return converters;
    }

    @Override
    public Iterator<Converter> iterator() {
        List<Converter> allConverters = new ArrayList<>();
        for (PriorityQueue<PrioritizedConverter> values : typedConverters.values()) {
            for (PrioritizedConverter converter : values) {
                allConverters.add(converter);
            }
        }
        return allConverters.iterator();
    }

    public Class<?> resolveConvertedType(Converter<?> converter) {
        assertConverter(converter);
        Class<?> type;
        Class<?> converterClass = converter.getClass();
        while (converterClass != null) {
            Type[] interfaceTypes = converterClass.getGenericInterfaces();
            for (Type interfaceType : interfaceTypes) {
                type = resolveConvertedType(interfaceType);
                if (null != type) {
                    return type;
                }
            }

            Type superType = converterClass.getGenericSuperclass();
            if (ParameterizedType.class.isAssignableFrom(superType.getClass())) {
                type = resolveConvertedType(superType);
                if (null != type) {
                    return type;
                }
            }

            converterClass = converterClass.getSuperclass();
        }

        return null;
    }

    private Class<?> resolveConvertedType(Type type) {
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                Class<?> rawType = (Class) parameterizedType.getRawType();
                if (Converter.class.isAssignableFrom(rawType)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                        return (Class) actualTypeArguments[0];
                    }
                }
            }
        }
        return null;
    }

    private void assertConverter(Converter<?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Converter must not be an interface!");
        }
        if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Converter must not be abstract!");
        }
    }
}
